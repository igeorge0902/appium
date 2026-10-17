package qa.ios.base;

import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import qa.ios.testng.Verify;
import qa.ios.util.PropertyUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TestBase extends Verify implements Constants {

    public static final Logger Log = LoggerFactory.getLogger(TestBase.class);
    private static final Map<ITestResult, List<Throwable>> verificationFailuresMap = new HashMap<>();
    public static URL serverAddress;
    public static WebDriverWait driverWait;
    public static String testngXml = PropertyUtils.getProperty("testngXml");
    protected static int numOfDevices;
    protected static String DeviceName;
    protected static String DeviceClass;
    protected static String UniqueDeviceID;
    protected static String ProductVersion;
    protected static int deviceCount;
    protected static IOSDriver driver = null;
    static Map<String, String> devices = new HashMap<>();
    private static boolean devicesDiscovered;

    public TestBase(IOSDriver driver) {
        TestBase.driver = DriverManager.driver;
    }

    public TestBase() {
        discoverDevicesOnce();
    }

    public TestBase(int i) {
        deviceCount = (i + 1);

        TestBase.DeviceClass = devices.get("DeviceClass" + deviceCount);
        TestBase.DeviceName = devices.get("deviceName" + deviceCount);
        TestBase.UniqueDeviceID = devices.get("UniqueDeviceID" + deviceCount);
        TestBase.ProductVersion = devices.get("ProductVersion" + deviceCount);
    }

    /**
     * Runs USB device discovery once per JVM; later page/listener instances reuse the result.
     */
    static synchronized void discoverDevicesOnce() {
        if(devicesDiscovered) {
            return;
        }
        devicesDiscovered = true;
        try {
            devices = new DeviceConfiguration().getDevices();
            deviceCount = devices.size();
        }
        catch(Exception e) {
            Log.warn("Error getting devices; falling back to simulator mode", e);
            devices = new HashMap<>();
            deviceCount = 0;
        }
    }

    /**
     * Retrieves verification failures from List, that will be appended to the ITestReport by TestMethodListener.
     */
    public static List<Throwable> getVerificationFailures() {
        List<Throwable> verificationFailures = verificationFailuresMap
                                                   .get(Reporter.getCurrentTestResult());
        return verificationFailures == null ? new ArrayList<>()
                   : verificationFailures;
    }

    /**
     * Adds verification failures to the List, that will be appended to the ITestReport by TestMethodListener.
     */
    public static void addVerificationFailure(Throwable e) {
        List<Throwable> verificationFailures = getVerificationFailures();
        verificationFailuresMap.put(
            Reporter.getCurrentTestResult(),
            verificationFailures
        );
        verificationFailures.add(e);
    }

    @BeforeClass
    public void setUp(ITestContext context) throws Exception {

        String app = context.getCurrentXmlTest().getParameter("app");
        String device = context.getCurrentXmlTest().getParameter("device");

        int maxAttempts = 12;       // 12 × 5s = 60s maximum
        int pollIntervalMs = 5000;  // 5 seconds between retries

        for(int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                Log.info("Starting driver (attempt {}/{})", attempt, maxAttempts);
                driver = DriverManager.startDriver(app, device, 40);
                // In Appium 2, resetApp() is removed. Use terminateApp + activateApp instead.
                String bundleId = DriverManager.resolveBundleId();
                if(bundleId != null) {
                    driver.terminateApp(bundleId);
                    driver.activateApp(bundleId);
                }
                Log.info("Driver started successfully on attempt {}", attempt);
                break; // success — exit loop
            }
            catch(Exception e) {
                Log.warn("Attempt {}/{} failed: {}", attempt, maxAttempts, e.getMessage());
                if(attempt == maxAttempts) {
                    throw new RuntimeException(
                        "Could not start driver after " + maxAttempts
                            + " attempts (" + (maxAttempts * pollIntervalMs / 1000) + "s)", e
                    );
                }
                try {
                    Thread.sleep(pollIntervalMs);
                }
                catch(InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Interrupted while waiting for driver startup", ie);
                }
            }
        }
    }

    @AfterClass
    public void closeBrowser(ITestContext context) {
        DriverManager.stopDriver();
    }

}
