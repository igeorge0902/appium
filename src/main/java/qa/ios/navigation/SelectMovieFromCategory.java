package qa.ios.navigation;

import io.appium.java_client.AppiumBy;
import qa.ios.base.TestBase;
import qa.ios.page.PageObjects;

public class SelectMovieFromCategory extends TestBase {

	public static SelectMovieFromCategory movieFromCategory() throws Exception {

		PageObjects.movies().click();
		driver.findElement(AppiumBy.iOSClassChain("**/XCUIElementTypeSegmentedControl/XCUIElementTypeButton[2]")).click();
		//driver.findElement(AppiumBy.iOSClassChain("**/XCUIElementTypeButton[`label == 'Categories'`][1]")).click();
		driver.findElement(AppiumBy.iOSNsPredicateString("label == 'Drama'")).click();
		driver.findElement(AppiumBy.iOSNsPredicateString("label == '12 Angry Men'")).click();
		driver.findElement(AppiumBy.iOSNsPredicateString("label == 'Cinema City Allee'")).click();

		return new SelectMovieFromCategory();
	}
}
