package main.java.qa.android.main;

import io.appium.java_client.android.AndroidDriver;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import main.java.qa.android.testng.Verify;
import main.java.qa.android.util.PropertyUtils;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

public class TestBase extends Verify implements Constants {
	
	public static Logger Log = Logger.getLogger(Logger.class.getName());

	
	protected static int numOfDevices;
	protected static String deviceId;
	protected static String deviceName;
	protected volatile static String characteristics;
	protected static String osVersion;
	protected String port;
	protected static int deviceCount;
	
	/**
	 * Get device parameters by the number of attached devices, where the number is the order of that.
	 * 
	 * Example: "deviceName"+1"
	 * 
	 * @Params deviceName
	 * @Params osVersion
	 * @Params deviceId
	 * 
	 * @Params deviceNumber
	 */
	static Map<String, String> devices = new HashMap<String, String>();
	static DeviceConfiguration deviceConf = new DeviceConfiguration();
	
	protected static WebElement element = null;
	protected static AndroidDriver<?> driver;
	
	  public static URL serverAddress;
	  public static WebDriverWait driverWait;
	  public static String testngXml = PropertyUtils.getProperty("testngXml");
	  public static String adbPath = PropertyUtils.getProperty("adb");


	  /**
	    Initialize the webdriver. Must be called before using any helper methods. 
	   */
	  public static void init(AndroidDriver<?> webDriver, URL driverServerAddress) {
	    driver = webDriver;
	    serverAddress = driverServerAddress;
	    int timeoutInSeconds = 60;
	    // must wait at least 60 seconds for running on Sauce.
	    // waiting for 30 seconds works locally however it fails on Sauce.
	    driverWait = new WebDriverWait(webDriver, timeoutInSeconds);
	  }

	public TestBase(WebDriver driver) {
		TestBase.driver = DriverManager.driver;
	}
	
	public TestBase(){
		DOMConfigurator.configure(log4jxml);
		
		try {
			devices = deviceConf.getDivces();
			deviceCount = devices.size()/4;
		}catch (Exception e) {
			e.printStackTrace();
		}
	}	


	public TestBase(int i){
		
		deviceCount = (i+1);
		
		TestBase.deviceId = devices.get("deviceID"+deviceCount);
		TestBase.deviceName = devices.get("deviceName"+deviceCount);
		TestBase.osVersion = devices.get("osVersion"+deviceCount);
		TestBase.characteristics = devices.get("characteristics"+deviceCount);
	}
	
	public static String characteristics() {
		if (characteristics == null) {
			TestBase.characteristics = devices.get("characteristics"+deviceCount);
			
		}		
		return characteristics;
	}
	
	@BeforeClass
	public void setUp(ITestContext context) throws Exception {
		TestBase.verifyNotNull(devices, "No devices are connected!");
		
		try {

			String app = context.getCurrentXmlTest().getParameter("app");
			String device = context.getCurrentXmlTest().getParameter("device");

			driver = DriverManager.startDriver(app, device, 40);

		} catch (Exception e) {

			Log.info(e);
			// get the web driver parameters from the testng xml file

			String app = context.getCurrentXmlTest().getParameter("app");
			String device = context.getCurrentXmlTest().getParameter("device");

			Log.info(app + " is reconnecting!");

			driver = DriverManager.startDriver(app, device, 40);
		}

	}

	@AfterClass
	public void closeBrowser(ITestContext context) {
		DriverManager.stopDriver();
		devices.clear();
		
	}

	public static void currentPlatform() {
		Platform.getCurrent();
		return;
	}

	public static boolean isSupportedPlatformMac(boolean condition) {
		Platform current = Platform.getCurrent();
		return Platform.MAC.is(current);
	}


	private static Map<ITestResult, List<Throwable>> verificationFailuresMap = new HashMap<ITestResult, List<Throwable>>();

	/**
	 * Retrieves verficationFailures' to from List<Throwable>, that will be
	 * appended to the ITestReport by {@link TestMethodListener.class}.
	 * 
	 */

	public static List<Throwable> getVerificationFailures() {
		List<Throwable> verificationFailures = verificationFailuresMap
				.get(Reporter.getCurrentTestResult());
		return verificationFailures == null ? new ArrayList<Throwable>()
				: verificationFailures;
	}

	/**
	 * Adds verficationFailures' to the List<Throwable>, that will be appended
	 * to the ITestReport by {@link TestMethodListener.class}.
	 * 
	 * @param e
	 */

	public static void addVerificationFailure(Throwable e) {
		List<Throwable> verificationFailures = getVerificationFailures();
		verificationFailuresMap.put(Reporter.getCurrentTestResult(),
				verificationFailures);
		verificationFailures.add(e);
	}
	
}
