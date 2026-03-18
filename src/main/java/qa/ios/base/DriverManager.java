package qa.ios.base;

import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.options.XCUITestOptions;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openqa.selenium.support.ui.WebDriverWait;
import qa.ios.util.PropertyUtils;

public class DriverManager extends TestBase implements Constants {

	private static final Logger log = LoggerFactory.getLogger(DriverManager.class);

	public static IOSDriver driver = null;
	private static String app = null;

	public DriverManager() {
	}

	public static IOSDriver startDriver(String app, String device, int timeout) throws MalformedURLException {
		// get devices per a parameter from deviceConf
		log.info("Attached device: {}, {}, {}", devices.get("deviceName"), devices.get("DeviceClass"), devices.get("ProductVersion"));

		XCUITestOptions options = new XCUITestOptions();

		if (deviceCount > 0) {
			// Real device configuration
			options.setPlatformName("iOS");
			options.setPlatformVersion(devices.get("ProductVersion"));
			options.setDeviceName(devices.get("deviceName"));
			options.setUdid(devices.get("UniqueDeviceID"));
			options.setApp(PropertyUtils.getProperty("bundleid"));
			options.setAutomationName("XCUITest");
			options.setCapability("xcodeOrgId", "X8DL5FV4EA");
			options.setCapability("xcodeSigningId", "Apple Development");
			options.setCapability("updatedWDABundleId", "com.georgegaspar.SwiftLoginScreen");
		} else {
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

			String simDevice = System.getProperty("sim.device", "iPhone 16 Pro");
			String simVersion = System.getProperty("sim.version", "26.1");
			String simUdid = System.getProperty("sim.udid", "");

			options.setDeviceName(simDevice);
			options.setPlatformName("iOS");
			options.setPlatformVersion(simVersion);
			if (!simUdid.isEmpty()) {
				options.setUdid(simUdid);
			}

			String userDir = System.getProperty("user.dir");
			String appPath = Paths.get(userDir, app).toAbsolutePath().toString();
			options.setApp(appPath);
		}

		String appiumHost = System.getProperty("appium.host",
				PropertyUtils.getProperty("appium.host") != null ? PropertyUtils.getProperty("appium.host") : "127.0.0.1");
		String appiumPort = System.getProperty("appium.port",
				PropertyUtils.getProperty("appium.port") != null ? PropertyUtils.getProperty("appium.port") : "4723");
		String appiumBasePath = System.getProperty("appium.basePath",
				PropertyUtils.getProperty("appium.basePath") != null ? PropertyUtils.getProperty("appium.basePath") : "/");

		String serverUrl = "http://" + appiumHost + ":" + appiumPort + appiumBasePath;
		log.info("Appium server URL: {}", serverUrl);
		URL serverAddress = new URL(serverUrl);

		driver = new IOSDriver(serverAddress, options);
		driverWait = new WebDriverWait(driver, Duration.ofSeconds(30));
		driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(60));
		driver.setSetting("snapshotTimeout", 600);
		driver.setSetting("snapshotMaxDepth", 200);
		driver.setSetting("useJSONSource", true);

		return driver;
	}

	public static void stopDriver() {
		String bundleId = PropertyUtils.getProperty("bundleid");
		if (bundleId != null) {
			driver.terminateApp(bundleId);
		}
		driver.quit();
	}

	public static IOSDriver getDriverInstance() {
		return driver;
	}

	public static String getApp() {
		return app;
	}

}
