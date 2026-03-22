package qa.ios.navigation;

import io.appium.java_client.AppiumBy;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import qa.ios.base.TestBase;

import java.time.Duration;
import java.util.Map;

public class VenueDetails extends TestBase {

	/** 5 s max, poll every 0.5 s */
	private static WebDriverWait waitFor() {
		return new WebDriverWait(driver, Duration.ofSeconds(5), Duration.ofMillis(500));
	}

	public static VenueDetails scrollOnDetails() throws Exception {
		WebElement scrollView = driver.findElement(AppiumBy.iOSClassChain(
				"**/XCUIElementTypeWindow[1]/XCUIElementTypeOther[4]/XCUIElementTypeOther[2]/XCUIElementTypeOther/XCUIElementTypeOther/XCUIElementTypeOther/XCUIElementTypeScrollView"));

		driver.executeScript("mobile: scroll", Map.of(
				"direction", "down",
				"predicateString", "Video",
				"element", ((org.openqa.selenium.remote.RemoteWebElement) scrollView).getId()
		));
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
		// Wait for the Dates button on VenuesDetailsVC and tap it
		waitFor().until(ExpectedConditions.elementToBeClickable(
				AppiumBy.iOSNsPredicateString("label == 'Dates'"))).click();

		// Wait for the picker wheel to appear
		WebElement picker = waitFor().until(ExpectedConditions.presenceOfElementLocated(
				AppiumBy.className("XCUIElementTypePickerWheel")));
		Log.info("Picker initial value: '{}'", picker.getAttribute("value"));

		// Scroll picker to next value (row 0 = "Select date", row 1 = actual date)
		// sendKeys does not work on UIPickerView — use mobile: selectPickerWheelValue
		driver.executeScript("mobile: selectPickerWheelValue", Map.of(
				"element", picker,
				"order", "next"
		));

		String selected = picker.getAttribute("value");
		Log.info("Picker value after scroll: '{}'", selected);
		Assert.assertNotEquals(selected, "Select date", "Date should have been selected");

		// Dismiss the dates popover by tapping outside it
		driver.executeScript("mobile: tap", Map.of("x", 195, "y", 100));

		return new VenueDetails();
	}

	public static VenueDetails bookChair() throws Exception {
		// Tap Book button — opens the seat popover
		waitFor().until(ExpectedConditions.elementToBeClickable(
				AppiumBy.iOSNsPredicateString("label == 'Book'"))).click();

		// Wait for seat labels to appear (they are XCUIElementTypeStaticText, not buttons)
		waitFor().until(ExpectedConditions.presenceOfElementLocated(
				AppiumBy.iOSNsPredicateString("label == 'A1'")));

		// Tap seats A1, A2, A3
		driver.findElement(AppiumBy.iOSNsPredicateString("label == 'A1'")).click();
		driver.findElement(AppiumBy.iOSNsPredicateString("label == 'A2'")).click();
		driver.findElement(AppiumBy.iOSNsPredicateString("label == 'A3'")).click();

		// Tap Basket button (the one inside the seat popover, not the nav bar one)
		// The popover has Clear (left) and Basket (right) buttons
		waitFor().until(ExpectedConditions.elementToBeClickable(
				AppiumBy.iOSClassChain("**/XCUIElementTypeButton[`label == 'Basket'`][2]"))).click();

		// Tap CheckOut on the Basket screen
		waitFor().until(ExpectedConditions.elementToBeClickable(
				AppiumBy.iOSNsPredicateString("label == 'CheckOut'"))).click();

		return new VenueDetails();
	}
}
