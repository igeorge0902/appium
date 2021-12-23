package main.java.qa.ios.navigation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.Assert;

import io.appium.java_client.MobileBy;
import io.appium.java_client.MobileElement;
import io.appium.java_client.ios.IOSElement;
import main.java.qa.ios.main.TestBase;

public class VenueDetails extends TestBase {

	public static VenueDetails scrollOnDetails() throws Exception{

		MobileElement scrollView = (MobileElement) driver.findElementByIosClassChain("**/XCUIElementTypeWindow[1]/XCUIElementTypeOther[4]/XCUIElementTypeOther[2]/XCUIElementTypeOther/XCUIElementTypeOther/XCUIElementTypeOther/XCUIElementTypeScrollView");

		// scroll down then up
		Map<String, Object> args = new HashMap<>();
		args.put("direction", "down");
		args.put("predicateString", "Video");
		args.put("element", scrollView.getId());
		driver.executeScript("mobile: scroll", args);
		return new VenueDetails();		
		
	}
	
	public static VenueDetails playGarnierMovie() throws Exception{
		
		driver.findElementByIosNsPredicate("label == 'Play'").click();
		Thread.sleep(10000);
		driver.findElementByIosNsPredicate("label == 'Video'").click();
		String elapsedTime = driver.findElementByIosNsPredicate("label == 'Time Elapsed'").getAttribute("value");
		Log.info("Elapsed time: " + elapsedTime);
		Thread.sleep(10000);
		driver.findElementByIosNsPredicate("label == 'Pause'").click();

		return new VenueDetails();	
	}
	
	public static VenueDetails selectPickerDate() throws Exception{
		
		driver.findElementByIosNsPredicate("label == 'Dates'").click();
		String pickerDate = "Sep 12, 2021 at 9:00 PM";
		MobileElement el = (MobileElement) driver.findElement(MobileBy.className("XCUIElementTypePickerWheel"));
		el.setValue(pickerDate);
		driver.findElementByIosNsPredicate("label == 'dismiss popup'").click();
		return new VenueDetails();	
	}
	
	public static VenueDetails bookChair() throws Exception{
		

		driver.findElementByIosNsPredicate("label == 'Book'").click();
	//	System.out.println(driver.getPageSource());

		driver.findElementByIosNsPredicate("label == 'A1'").click();		
		driver.findElementByIosNsPredicate("label == 'A2'").click();
		driver.findElementByIosNsPredicate("label == 'A3'").click();
		 
		MobileElement secondRow = (MobileElement) driver.findElementByIosClassChain("**/XCUIElementTypeWindow[1]/XCUIElementTypeOther[6]/XCUIElementTypePopover/XCUIElementTypeOther[1]/XCUIElementTypeOther/XCUIElementTypeTable/XCUIElementTypeCell[2]");
		
		// scroll right
		Map<String, Object> args = new HashMap<>();
		args.put("direction", "right");
		args.put("predicateString", "B5");
		args.put("element", secondRow.getId());
		driver.executeScript("mobile: scroll", args);
		
		driver.findElementByIosClassChain("**/XCUIElementTypeButton[`label == 'Basket'`][2]").click();
		driver.findElementByIosNsPredicate("label == 'B5'").click();

		//List<IOSElement> tickets = driver.findElements(MobileBy.iOSClassChain("**/XCUIElementTypeCollectionView/XCUIElementTypeCell"));
		//List<IOSElement> tickets = driver.findElements(MobileBy.className("SwiftLoginScreen.FeedCells"));
		//Log.info("Nr. tickets: " + tickets.size());
		
		driver.findElementByIosNsPredicate("label == 'CheckOut'").click();

		String errormsg = driver.findElementByIosNsPredicate("label == 'HTTP response was 502'").getAttribute("value");
		Assert.assertEquals("HTTP response was 502", errormsg);

		return new VenueDetails();	
	}	
	
}
