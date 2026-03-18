package qa.ios.page;

import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import qa.ios.base.TestBase;
import qa.ios.util.Helpers;

public class PageObjects extends TestBase {

	public static WebElement goToMenu() {
		return driver.findElement(AppiumBy.accessibilityId("Go to Menu"));
	}

	public static WebElement clickAlert() {
		return driver.findElement(AppiumBy.accessibilityId("OK"));
	}

	public static WebElement checkUser(String text) {
		return Helpers.getElement(text);
	}

	public static WebElement getElementWithText(String text) {
		return driver.findElement(AppiumBy.accessibilityId(text));
	}

	public static WebElement menu() {
		return driver.findElement(By.xpath("//XCUIElementTypeButton[@name='Menu']"));
	}

	public static WebElement movies() {
		return driver.findElement(By.xpath("//XCUIElementTypeButton[@name='Movies']"));
	}

	public static WebElement back() {
		return driver.findElement(By.xpath("//XCUIElementTypeButton[@name='Back']"));
	}

}
