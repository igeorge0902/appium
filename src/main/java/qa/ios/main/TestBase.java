package main.java.qa.ios.main;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.appium.java_client.ios.*;
import main.java.qa.ios.testng.Verify;
import main.java.qa.ios.util.PropertyUtils;

import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.openqa.selenium.By;
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
	protected static String DeviceName;
	protected static String DeviceClass;
	protected static String UniqueDeviceID;
	protected static String ProductVersion;
	protected static int deviceCount;

	static Map<String, String> devices = new HashMap<String, String>();
	static DeviceConfiguration deviceConf = new DeviceConfiguration();
	
	protected static IOSDriver<IOSElement> driver = null;
	
	public static URL serverAddress;
	public static WebDriverWait driverWait;
	public static String testngXml = PropertyUtils.getProperty("testngXml");



	public TestBase(IOSDriver<IOSElement> driver) {
		TestBase.driver = DriverManager.driver;
	}
	
	public TestBase() {
		DOMConfigurator.configure(log4jxml);
		
		try {
			devices = deviceConf.getDevices();
			deviceCount = devices.size();
		}catch (Exception e) {
			Log.info(e.getStackTrace());
		}
		
	}
	
	public TestBase(int i) {
		deviceCount = (i+1);
		
		TestBase.DeviceClass = devices.get("DeviceClass"+deviceCount);
		TestBase.DeviceName = devices.get("deviceName"+deviceCount);
		TestBase.UniqueDeviceID = devices.get("UniqueDeviceID"+deviceCount);
		TestBase.ProductVersion = devices.get("ProductVersion"+deviceCount);


	}	
		
	@BeforeClass
	public void setUp(ITestContext context) throws Exception {

		try {

			String app = context.getCurrentXmlTest().getParameter("app");
			String device = context.getCurrentXmlTest().getParameter("device");

			driver = DriverManager.startDriver(app, device, 40);
	    	driver.resetApp();

		} catch (Exception e) {

			Log.info(e);

			String app = context.getCurrentXmlTest().getParameter("app");
			String device = context.getCurrentXmlTest().getParameter("device");

			Log.info(PropertyUtils.getProperty("device") + " is reconnecting!");

			driver = DriverManager.startDriver(app, device, 40);
	    	driver.resetApp();

		}

	}

	@AfterClass
	public void closeBrowser(ITestContext context) {
		DriverManager.stopDriver();
	//	devices.clear();

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
