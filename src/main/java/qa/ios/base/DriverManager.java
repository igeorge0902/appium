package qa.ios.base;

import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.options.XCUITestOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qa.ios.util.PropertyUtils;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Optional;

public class DriverManager extends TestBase implements Constants {

    private static final Logger log = LoggerFactory.getLogger(DriverManager.class);

    public static IOSDriver driver = null;
    private static String app = null;

    public DriverManager() {
    }

    public static IOSDriver startDriver(String testngAppPath, String testngDeviceName, int driverTimeoutSeconds) throws MalformedURLException {
        // devices are discovered once by TestBase.discoverDevicesOnce()
        log.info("Attached device: {}, {}, {}", devices.get("deviceName"), devices.get("DeviceClass"), devices.get("ProductVersion"));

        XCUITestOptions options = new XCUITestOptions();

        // If a simulator UDID is explicitly provided, always use simulator mode
        // even when a real USB device is connected.
        String configuredSimulatorUdid = PropertyUtils.resolve("sim.udid", null);
        boolean useSimulator = configuredSimulatorUdid != null || deviceCount == 0;

        if(!useSimulator) {
            // Real device configuration
            options.setPlatformName("iOS");
            options.setPlatformVersion(devices.get("ProductVersion"));
            options.setDeviceName(devices.get("deviceName"));
            options.setUdid(devices.get("UniqueDeviceID"));
            options.setApp(requiredBundleId());
            options.setAutomationName("XCUITest");
            options.setCapability("xcodeOrgId", "X8DL5FV4EA");
            options.setCapability("xcodeSigningId", "Apple Development");
            options.setCapability("updatedWDABundleId", "com.georgegaspar.SwiftLoginScreen");
        }
        else {
            // Simulator configuration
            options.setAutomationName("XCUITest");
            options.setCapability("wdaLaunchTimeout", 240000);
            options.setCapability("wdaConnectionTimeout", 240000);
            options.setCapability("wdaStartupRetries", 4);
            options.setCapability("wdaStartupRetryInterval", 30000);
            options.setCapability("showXcodeLog", true);
            // Let Appium/XCUITest driver build, install, and start WDA on the simulator.
            // Do NOT set webDriverAgentUrl — that tells Appium to skip WDA startup and
            // connect to an already-running instance, which causes ETIMEDOUT if nothing
            // is listening. Appium discovers the correct WDA host:port automatically.
            options.setCapability("usePreinstalledWDA", false);
            options.setNoReset(true);
            options.setPlatformName("iOS");

            String simulatorUdid = applySimulatorTarget(options);
            Optional<String> resolvedAppPath = resolveAppPath(testngAppPath);
            if(resolvedAppPath.isPresent()) {
                options.setApp(resolvedAppPath.get());
            }
            else {
                // No .app supplied: launch the build already installed on the simulator (CI installs it via simctl).
                String bundleId = requiredBundleId();
                if(simulatorUdid != null && DeviceConfiguration.isAppInstalled(simulatorUdid, bundleId).filter(appInstalled -> !appInstalled).isPresent()) {
                    throw new IllegalStateException("No app path configured and '" + bundleId + "' is not installed on simulator "
                                                        + simulatorUdid + ". Install it (xcrun simctl install " + simulatorUdid +
                                                        " <App.app>) or set APP_PATH.");
                }
                log.info("No app path configured; launching installed app by bundle id '{}'.", bundleId);
                options.setBundleId(bundleId);
            }
        }

        URL serverAddress = buildServerUrl();
        log.info("Appium server URL: {}", serverAddress);

        driver = new IOSDriver(serverAddress, options);
        driverWait = new WebDriverWait(driver, Duration.ofSeconds(5));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(10));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        driver.setSetting("snapshotTimeout", 60);
        driver.setSetting("snapshotMaxDepth", 200);
        driver.setSetting("useJSONSource", true);

        return driver;
    }

    public static void stopDriver() {
        if(driver == null) {
            return;
        }
        String bundleId = resolveBundleId();
        try {
            if(bundleId != null) {
                driver.terminateApp(bundleId);
            }
        }
        catch(RuntimeException appTerminationException) {
            log.warn("Could not terminate app {}: {}", bundleId, appTerminationException.getMessage());
        }
        finally {
            try {
                driver.quit();
            }
            finally {
                driver = null;
            }
        }
    }

    /**
     * Simulator target precedence: explicit {@code sim.*} values → booted simulator → Appium default device. Explicit values are never
     * mixed with auto-discovered ones so the target stays unambiguous.
     */
    private static String applySimulatorTarget(XCUITestOptions options) {
        String simulatorDeviceName = PropertyUtils.resolve("sim.device", null);
        String simulatorIosVersion = PropertyUtils.resolve("sim.version", null);
        String simulatorUdid = PropertyUtils.resolve("sim.udid", null);

        if(simulatorDeviceName == null && simulatorIosVersion == null && simulatorUdid == null) {
            Optional<DeviceConfiguration.BootedSimulator> bootedSimulator = DeviceConfiguration.findBootedSimulator();
            if(bootedSimulator.isPresent()) {
                simulatorDeviceName = bootedSimulator.get().name();
                simulatorIosVersion = bootedSimulator.get().version();
                simulatorUdid = bootedSimulator.get().udid();
            }
        }
        if(simulatorDeviceName == null) {
            simulatorDeviceName = PropertyUtils.required("sim.defaultDevice");
            log.info("No simulator configured or booted; falling back to '{}' (latest installed iOS).", simulatorDeviceName);
        }

        options.setDeviceName(simulatorDeviceName);
        if(simulatorIosVersion != null) {
            options.setPlatformVersion(simulatorIosVersion);
        }
        if(simulatorUdid != null) {
            options.setUdid(simulatorUdid);
        }
        log.info(
            "Simulator target: device='{}', version='{}', udid='{}'", simulatorDeviceName,
            simulatorIosVersion == null ? "<latest>" : simulatorIosVersion, simulatorUdid == null ? "<auto>" : simulatorUdid
        );
        return simulatorUdid;
    }

    /**
     * App bundle resolution:
     * <ol>
     *   <li>{@code app.path} / {@code APP_PATH} – explicit; must exist, otherwise fail fast.</li>
     *   <li>TestNG {@code app} parameter – used when the bundle exists, otherwise ignored with a warning.</li>
     *   <li>Empty – the caller launches the app already installed on the simulator by bundle id.</li>
     * </ol>
     * Relative paths are resolved against the working directory.
     */
    static Optional<String> resolveAppPath(String testngAppPath) {
        String configuredAppPath = PropertyUtils.resolve("app.path", null);
        if(configuredAppPath != null) {
            Path configuredAppBundlePath = absolute(configuredAppPath);
            if(!Files.exists(configuredAppBundlePath)) {
                throw new IllegalStateException("App bundle not found (app.path/APP_PATH): " + configuredAppBundlePath);
            }
            return Optional.of(configuredAppBundlePath.toString());
        }
        if(testngAppPath != null && !testngAppPath.isBlank()) {
            Path testngAppBundlePath = absolute(testngAppPath.trim());
            if(Files.exists(testngAppBundlePath)) {
                return Optional.of(testngAppBundlePath.toString());
            }
            log.warn("TestNG 'app' bundle {} not found; falling back to the app installed on the simulator.", testngAppBundlePath);
        }
        return Optional.empty();
    }

    /**
     * {@code bundle.id} / {@code BUNDLE_ID}, falling back to the legacy {@code bundleid} key.
     */
    public static String resolveBundleId() {
        return PropertyUtils.resolve("bundle.id", PropertyUtils.getProperty("bundleid"));
    }

    private static String requiredBundleId() {
        String bundleId = resolveBundleId();
        if(bundleId == null) {
            throw new IllegalStateException("No app configured: set APP_PATH/-Dapp.path (or the TestNG 'app' parameter) "
                                                +
                                                "to install a build, or BUNDLE_ID/-Dbundle.id to launch an app already installed on the device.");
        }
        return bundleId;
    }

    private static Path absolute(String configuredPath) {
        Path resolvedPath = Paths.get(configuredPath);
        if(!resolvedPath.isAbsolute()) {
            resolvedPath = Paths.get(System.getProperty("user.dir"), configuredPath);
        }
        return resolvedPath.toAbsolutePath().normalize();
    }

    /**
     * Builds the Appium server URL from {@code appium.host}, {@code appium.port} and {@code appium.basePath} (env:
     * {@code APPIUM_HOST}, {@code APPIUM_PORT}, {@code APPIUM_BASEPATH}).
     */
    static URL buildServerUrl() throws MalformedURLException {
        String appiumHost = PropertyUtils.resolve("appium.host", "127.0.0.1");
        int appiumPort = PropertyUtils.resolveInt("appium.port", 4723);
        String appiumBasePath = PropertyUtils.resolve("appium.basePath", "/");
        if(!appiumBasePath.startsWith("/")) {
            appiumBasePath = "/" + appiumBasePath;
        }
        String appiumServerUrl = "http://" + appiumHost + ":" + appiumPort + appiumBasePath;
        return URI.create(appiumServerUrl).toURL();
    }

    public static IOSDriver getDriverInstance() {
        return driver;
    }

    public static String getApp() {
        return app;
    }

}
