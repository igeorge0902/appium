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

	protected static int numOfDevices;
	protected static String DeviceName;
	protected static String DeviceClass;
	protected static String UniqueDeviceID;
	protected static String ProductVersion;
	protected static int deviceCount;

	static Map<String, String> devices = new HashMap<>();
	static DeviceConfiguration deviceConf = new DeviceConfiguration();

	protected static IOSDriver driver = null;

	public static URL serverAddress;
	public static WebDriverWait driverWait;
	public static String testngXml = PropertyUtils.getProperty("testngXml");



	public TestBase(IOSDriver driver) {
		TestBase.driver = DriverManager.driver;
	}

	public TestBase() {
		try {
			devices = deviceConf.getDevices();
			deviceCount = devices.size();
		} catch (Exception e) {
			Log.info("Error getting devices", e);
		}
	}

	public TestBase(int i) {
		deviceCount = (i + 1);

		TestBase.DeviceClass = devices.get("DeviceClass" + deviceCount);
		TestBase.DeviceName = devices.get("deviceName" + deviceCount);
		TestBase.UniqueDeviceID = devices.get("UniqueDeviceID" + deviceCount);
		TestBase.ProductVersion = devices.get("ProductVersion" + deviceCount);
	}

	@BeforeClass
	public void setUp(ITestContext context) throws Exception {

		try {
			String app = context.getCurrentXmlTest().getParameter("app");
			String device = context.getCurrentXmlTest().getParameter("device");

			driver = DriverManager.startDriver(app, device, 40);
			// In Appium 2, resetApp() is removed. Use terminateApp + activateApp instead.
			String bundleId = PropertyUtils.getProperty("bundleid");
			if (bundleId != null) {
				driver.terminateApp(bundleId);
				driver.activateApp(bundleId);
			}

		} catch (Exception e) {
			Log.info("First attempt failed, retrying...", e);

			String app = context.getCurrentXmlTest().getParameter("app");
			String device = context.getCurrentXmlTest().getParameter("device");

			Log.info("{} is reconnecting!", PropertyUtils.getProperty("device"));

			driver = DriverManager.startDriver(app, device, 40);
			String bundleId = PropertyUtils.getProperty("bundleid");
			if (bundleId != null) {
				driver.terminateApp(bundleId);
				driver.activateApp(bundleId);
			}
		}
	}

	@AfterClass
	public void closeBrowser(ITestContext context) {
		DriverManager.stopDriver();
	}

	private static final Map<ITestResult, List<Throwable>> verificationFailuresMap = new HashMap<>();

	/**
	 * Retrieves verification failures from List, that will be
	 * appended to the ITestReport by TestMethodListener.
	 */
	public static List<Throwable> getVerificationFailures() {
		List<Throwable> verificationFailures = verificationFailuresMap
				.get(Reporter.getCurrentTestResult());
		return verificationFailures == null ? new ArrayList<>()
				: verificationFailures;
	}

	/**
	 * Adds verification failures to the List, that will be appended
	 * to the ITestReport by TestMethodListener.
	 */
	public static void addVerificationFailure(Throwable e) {
		List<Throwable> verificationFailures = getVerificationFailures();
		verificationFailuresMap.put(Reporter.getCurrentTestResult(),
				verificationFailures);
		verificationFailures.add(e);
	}

}
