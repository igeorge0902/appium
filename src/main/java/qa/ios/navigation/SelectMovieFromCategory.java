package main.java.qa.ios.navigation;

import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.By;

import io.appium.java_client.MobileElement;
import main.java.qa.ios.main.TestBase;
import main.java.qa.ios.page.PageObjects;

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
