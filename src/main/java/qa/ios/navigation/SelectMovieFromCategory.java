package qa.ios.navigation;

import io.appium.java_client.AppiumBy;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import qa.ios.base.TestBase;
import qa.ios.page.PageObjects;

import java.time.Duration;

public class SelectMovieFromCategory extends TestBase {

	/** 5 s max, poll every 0.5 s */
	private static WebDriverWait waitFor() {
		return new WebDriverWait(driver, Duration.ofSeconds(5), Duration.ofMillis(500));
	}

	public static SelectMovieFromCategory movieFromCategory() throws Exception {

		PageObjects.movies().click();

		// Confirm we navigated to MoviesVC (search field is always present)
		waitFor().until(ExpectedConditions.presenceOfElementLocated(
				AppiumBy.className("XCUIElementTypeSearchField")));

		// Wait for the Movies table to load data from the API (network-bound, allow 15 s)
		new WebDriverWait(driver, Duration.ofSeconds(15), Duration.ofMillis(500))
				.until(ExpectedConditions.presenceOfElementLocated(
						AppiumBy.iOSNsPredicateString("type == 'XCUIElementTypeCell' AND visible == true")));

		// Tap Categories in the segmented control (items: Reset, Categories, NA)
		waitFor().until(ExpectedConditions.elementToBeClickable(
				AppiumBy.iOSClassChain("**/XCUIElementTypeSegmentedControl/XCUIElementTypeButton[`label == 'Categories'`]"))).click();

		// Wait for category list, then tap Drama
		waitFor().until(ExpectedConditions.elementToBeClickable(
				AppiumBy.iOSNsPredicateString("label == 'Drama'"))).click();

		// Wait for drama movies, then tap 12 Angry Men
		waitFor().until(ExpectedConditions.elementToBeClickable(
				AppiumBy.iOSNsPredicateString("label == '12 Angry Men'"))).click();

		// Wait for venues to load, then tap Cinema City Allee
		waitFor().until(ExpectedConditions.elementToBeClickable(
				AppiumBy.iOSNsPredicateString("label == 'Cinema City Allee'"))).click();

		return new SelectMovieFromCategory();
	}
}
