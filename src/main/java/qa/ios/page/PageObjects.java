package qa.ios.page;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import io.appium.java_client.ios.IOSElement;
import qa.ios.base.TestBase;
import qa.ios.util.Helpers;

public class PageObjects extends TestBase {

	public static IOSElement goToMenu() {
		return driver.findElementByAccessibilityId("Go to Menu");
	}
	
	public static IOSElement clickAlert() {
		return driver.findElementByAccessibilityId("OK");
	}
	
	public static WebElement checkUser(String text) {
		return Helpers.getElement(text);
	}
	
	public static IOSElement getElementWithText(String text) {
		return driver.findElementByAccessibilityId(text);
	}
	
	public static IOSElement menu() {
		return driver.findElement(By.xpath("//XCUIElementTypeButton[@name='Menu']"));
	}
	
	public static IOSElement movies() {
		return driver.findElement(By.xpath("//XCUIElementTypeButton[@name='Movies']"));
	}
	
	public static IOSElement back() {
		return driver.findElement(By.xpath("//XCUIElementTypeButton[@name='Back']"));
	}
	
}
