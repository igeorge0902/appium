package qa.ios.base;

import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.IOSElement;
import io.appium.java_client.remote.MobileCapabilityType;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

import qa.ios.util.PropertyUtils;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.openqa.selenium.Platform;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.WebDriverWait;

public class DriverManager extends TestBase implements Constants {

	public static IOSDriver<IOSElement> driver = null;;
	private static String app = null;
	private static Logger Log = Logger.getLogger(Logger.class.getName());

	
	public DriverManager() {		
	}
	
	public static IOSDriver<IOSElement> startDriver(String app, String device, int timeout) throws MalformedURLException
	
	{
		//get devices per a parameter from deviceConf
		Log.info("Attached device: "+devices.get("deviceName")+", " + devices.get("DeviceClass")+", " + devices.get("ProductVersion"));
		
		DOMConfigurator.configure(log4jxml);
		Log.info(Platform.getCurrent());

		
	    DesiredCapabilities capabilities = new DesiredCapabilities();
	    
	    if (deviceCount > 0) {
	    
	    capabilities.setCapability(MobileCapabilityType.PLATFORM_NAME, "iOS");    
	    capabilities.setCapability(MobileCapabilityType.PLATFORM_VERSION, devices.get("ProductVersion"));
        capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, devices.get("deviceName"));
	    capabilities.setCapability("udid", devices.get("UniqueDeviceID"));	    
	    capabilities.setCapability("bundleId", PropertyUtils.getProperty("bundleid"));
		capabilities.setCapability("appium:networkConnectionEnabled", true);	    capabilities.setCapability("xcodeOrgId", "X8DL5FV4EA");
        capabilities.setCapability("xcodeSigningId", "Apple Development");
        capabilities.setCapability("updatedWDABundleId", "com.georgegaspar.SwiftLoginScreen");
		capabilities.setCapability("appium:automationName", "XCUITest");
	    
        //capabilities.setCapability(MobileCapabilityType.APP, app);

	    } else {
	    	
        capabilities.setCapability(MobileCapabilityType.AUTOMATION_NAME, "XCUITest");
        capabilities.setCapability("useNewWDA", false);
        capabilities.setCapability(MobileCapabilityType.NO_RESET, true);
        capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, "iPhone Simulator");
        capabilities.setCapability(MobileCapabilityType.PLATFORM_NAME, "iOS");
        capabilities.setCapability(MobileCapabilityType.PLATFORM_VERSION, "15.0");
	    //capabilities.setCapability(MobileCapabilityType.NEW_COMMAND_TIMEOUT, "300");
	    capabilities.setCapability(MobileCapabilityType.FORCE_MJSONWP, "true");

	    
	    
	    String userDir = System.getProperty("user.dir");
		String appPath = Paths.get(userDir, app).toAbsolutePath().toString();		
		capabilities.setCapability("app", appPath);
	    }
	    
		URL serverAddress;
		serverAddress = new URL("http://127.0.0.1:4723/");
		//serverAddress = new URL("http://127.0.0.1:4723/wd/hub");


		driver = new IOSDriver<>(serverAddress, capabilities);
	    driverWait = new WebDriverWait(driver, 30);
		driver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
		driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
		driver.setSetting("snapshotTimeout", "600");	
		driver.setSetting("snapshotMaxDepth", "200");
		driver.setSetting("useJSONSource", "true");		

		return driver;
	}
	
	
	public static void stopDriver() {
		driver.closeApp();
		driver.quit();
	}
	
	public static IOSDriver<IOSElement> getDriverInstance() {
		return driver;
	}
	
	public static String getApp() {
		return app;
	}
	
}
	
