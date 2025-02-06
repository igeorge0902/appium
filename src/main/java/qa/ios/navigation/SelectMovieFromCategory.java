package qa.ios.navigation;

import qa.ios.base.TestBase;
import qa.ios.page.PageObjects;

public class SelectMovieFromCategory extends TestBase {

	public static SelectMovieFromCategory movieFromCategory() throws Exception{
		
		PageObjects.movies().click();
		driver.findElementByIosClassChain("**/XCUIElementTypeSegmentedControl/XCUIElementTypeButton[2]").click();
		//driver.findElementByIosClassChain("**/XCUIElementTypeButton[`label == 'Categories'`][1]").click();
		driver.findElementByIosNsPredicate("label == 'Drama'").click();
		driver.findElementByIosNsPredicate("label == '12 Angry Men'").click();
		driver.findElementByIosNsPredicate("label == 'Cinema City Allee'").click();
		
		return new SelectMovieFromCategory();		
	}
}
