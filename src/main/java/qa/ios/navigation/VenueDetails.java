package qa.ios.navigation;

import io.appium.java_client.AppiumBy;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import qa.ios.base.TestBase;

import java.util.HashMap;
import java.util.Map;

public class VenueDetails extends TestBase {

	public static VenueDetails scrollOnDetails() throws Exception {

		WebElement scrollView = driver.findElement(AppiumBy.iOSClassChain("**/XCUIElementTypeWindow[1]/XCUIElementTypeOther[4]/XCUIElementTypeOther[2]/XCUIElementTypeOther/XCUIElementTypeOther/XCUIElementTypeOther/XCUIElementTypeScrollView"));

		// scroll down then up
		Map<String, Object> args = new HashMap<>();
		args.put("direction", "down");
		args.put("predicateString", "Video");
		args.put("element", ((org.openqa.selenium.remote.RemoteWebElement) scrollView).getId());
		driver.executeScript("mobile: scroll", args);
		return new VenueDetails();
	}

	public static VenueDetails playGarnierMovie() throws Exception {

		driver.findElement(AppiumBy.iOSNsPredicateString("label == 'Play'")).click();
		Thread.sleep(10000);
		driver.findElement(AppiumBy.iOSNsPredicateString("label == 'Video'")).click();
		String elapsedTime = driver.findElement(AppiumBy.iOSNsPredicateString("label == 'Time Elapsed'")).getAttribute("value");
		Log.info("Elapsed time: {}", elapsedTime);
		Thread.sleep(10000);
		driver.findElement(AppiumBy.iOSNsPredicateString("label == 'Pause'")).click();

		return new VenueDetails();
	}

	public static VenueDetails selectPickerDate() throws Exception {

		driver.findElement(AppiumBy.iOSNsPredicateString("label == 'Dates'")).click();
		String pickerDate = "Sep 12, 2021 at 9:00 PM";
		WebElement el = driver.findElement(AppiumBy.className("XCUIElementTypePickerWheel"));
		el.sendKeys(pickerDate);
		driver.findElement(AppiumBy.iOSNsPredicateString("label == 'dismiss popup'")).click();
		return new VenueDetails();
	}

	public static VenueDetails bookChair() throws Exception {

		driver.findElement(AppiumBy.iOSNsPredicateString("label == 'Book'")).click();

		driver.findElement(AppiumBy.iOSNsPredicateString("label == 'A1'")).click();
		driver.findElement(AppiumBy.iOSNsPredicateString("label == 'A2'")).click();
		driver.findElement(AppiumBy.iOSNsPredicateString("label == 'A3'")).click();

		WebElement secondRow = driver.findElement(AppiumBy.iOSClassChain("**/XCUIElementTypeWindow[1]/XCUIElementTypeOther[6]/XCUIElementTypePopover/XCUIElementTypeOther[1]/XCUIElementTypeOther/XCUIElementTypeTable/XCUIElementTypeCell[2]"));

		// scroll right
		Map<String, Object> args = new HashMap<>();
		args.put("direction", "right");
		args.put("predicateString", "B5");
		args.put("element", ((org.openqa.selenium.remote.RemoteWebElement) secondRow).getId());
		driver.executeScript("mobile: scroll", args);

		driver.findElement(AppiumBy.iOSClassChain("**/XCUIElementTypeButton[`label == 'Basket'`][2]")).click();
		driver.findElement(AppiumBy.iOSNsPredicateString("label == 'B5'")).click();

		driver.findElement(AppiumBy.iOSNsPredicateString("label == 'CheckOut'")).click();

		String errormsg = driver.findElement(AppiumBy.iOSNsPredicateString("label == 'HTTP response was 502'")).getAttribute("value");
		Assert.assertEquals("HTTP response was 502", errormsg);

		return new VenueDetails();
	}

}
