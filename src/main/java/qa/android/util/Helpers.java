package main.java.qa.android.util;


import main.java.qa.android.main.CaptureScreenshotOnFailureListener;
import main.java.qa.android.main.TestBase;
import main.java.qa.android.page.AnimationPage;
import main.java.qa.android.util.Helpers;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileBy;
import io.appium.java_client.MobileElement;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;


public abstract class Helpers extends TestBase{
	
	public static void doOCR(String string) throws FileNotFoundException, IOException{
		
		String screenShot = CaptureScreenshotOnFailureListener.captureScreenShot(string);
		String[] arguments = new String[] {screenShot}; 
		TesseractExample.main(arguments);
	}
	
    /** Verify the page has loaded **/
    public static void loaded() {
        find("Accessibility Node Provider");
    }
	
  /**
    Set implicit wait in seconds 
   */
  public static void setWait(int seconds) {
    driver.manage().timeouts().implicitlyWait(seconds, TimeUnit.SECONDS);
  }
  
  public static void getContentDetails()
  {
		System.out.println("Title: " + Helpers.getContentTitle());
		System.out.println("Category: " + Helpers.getContentCategory());
		System.out.println("Age Rating: " +Helpers.getAgeRating());
  }
  
  public static String getContentTitle()
  {
	  return elementByUISelector("lbl_ContentDetail_Title").getText();
  }
  public static String getContentCategory()
  {
	  return elementByUISelector("lbl_ContentDetail_SeriesName").getText();
  }
  
  public static String getAgeRating()
  {
	  return elementByUISelector("lbl_ContentDetail_AgeRating").getText();
  }
  
  /** Verify the page has loaded **/
  public static void loaded(String text) {
      Helpers.element(MobileBy.AccessibilityId(text));
  }
  
  public static WebElement radioButtons(String text)
  {
	  return Helpers.element(MobileBy.className(text));	  
  }
  
  /**
    Return an element by locator 
   */
  public static WebElement element(By locator) {
    return driver.findElement(locator);
  }
  
  /**
  Return an element by locator 
 */
  public static WebElement elementByUISelector(String text) 
  {
	  //System.out.println("elementFound : " + text);
	  return driver.findElementByAndroidUIAutomator("new UiSelector().resourceId(\"com.hbo.broadband:id/" + text + "\")");
  }
  
  public static WebDriver elementByUISelectorWebDriver(String text)
  {
	  return (WebDriver) driver.findElementByAndroidUIAutomator("new UiSelector().resourceId(\"com.hbo.broadband:id/" + text + "\")");
  }

  /**
    Return a list of elements by locator 
   */
  public static List<WebElement> elements(By locator) {
    return (List<WebElement>) driver.findElements(locator);
  }

  /**
    Press the back button 
   */
  public static void back() {
    driver.navigate().back();
  }

  /**
    Return a list of elements by tag name 
   */
  public static List<WebElement> tags(String tagName) {
    return elements(for_tags(tagName));
  }

  /**
    Return a tag name locator 
   */
  public static By for_tags(String tagName) {
    return By.className(tagName);
  }

  /**
    Return a static text element by xpath index 
   */
  public static WebElement s_text(int xpathIndex) {
    return element(for_text(xpathIndex));
  }

  /**
    Return a static text locator by xpath index 
   */
  public static By for_text(int xpathIndex) {
    return By.xpath("//android.widget.TextView[" + xpathIndex + "]");
  }

  /**
    Return a static text element that contains text 
   */
  public static WebElement text(String text) {
    return element(for_text(text));
  }
  
  /**
    Return a static text element that contains text 
   */
  public static WebElement button(String text) {
    return element(for_button(text));
  }

  /**
    Return a static text locator that contains text 
   */
  public static By for_text(String text) {
    return By.xpath("//android.widget.TextView[contains(@text, '" + text + "')]");
  }
  
  /**
    Return a static button locator that contains text 
   */
  public static By for_button(String text) {
    return By.xpath("//android.widget.ImageButton[contains(@text, '" + text + "')]");
  }

  /**
    Return a static text element by exact text 
   */
  public static WebElement text_exact(String text) {
    return element(for_text_exact(text));
  }

  /**
    Return a static text locator by exact text 
   */
  public static By for_text_exact(String text) {
    return By.xpath("//android.widget.TextView[@text='" + text + "']");
  }

  public static By for_find(String value) {
    return By.xpath("//*[@content-desc=\"" + value + "\" or @resource-id=\"" + value +
        "\" or @text=\"" + value + "\"] | //*[contains(translate(@content-desc,\"" + value +
        "\",\"" + value + "\"), \"" + value + "\") or contains(translate(@text,\"" + value +
        "\",\"" + value + "\"), \"" + value + "\") or @resource-id=\"" + value + "\"]");
  }

  public static WebElement find(String value) {
    return element(for_find(value));
  }

  /**
    Wait 30 seconds for locator to find an element 
   */
  public static WebElement wait(By locator) {
    return driverWait.until(ExpectedConditions.visibilityOfElementLocated(locator));
  }

  /**
    Wait 60 seconds for locator to find all elements 
   */
  public static List<WebElement> waitAll(By locator) {
    return driverWait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
  }

  /**
    Wait 60 seconds for locator to not find a visible element 
   */
  public static boolean waitInvisible(By locator) {
    return driverWait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
  }

  /**
    Return an element that contains name or text 
   */
 // public static WebElement scroll_to(String value) {
 //   return driver.scrollTo(value);
  //}

  /**
    Return an element that exactly matches name or text 
   */
  //public static WebElement scroll_to_exact(String value) {
  //  return driver.scrollToExact(value);
  //}

	public static void takeSnapShot(WebDriver webdriver,String fileWithPath) throws Exception{
		//Convert web driver object to TakeScreenshot
		TakesScreenshot scrShot =((TakesScreenshot)webdriver);
		//Call getScreenshotAs method to create image file
				File SrcFile=scrShot.getScreenshotAs(OutputType.FILE);
			//Move image file to new destination 
				File DestFile=new File(fileWithPath);
				//Copy file at destination
				FileUtils.copyFile(SrcFile, DestFile);
			
	}
	
	public static void animationClick() {
        loaded();
        find("Animation").click();
        AnimationPage.loaded();
    }
	
	public static void swipeUpMenu(AppiumDriver driver, MobileElement element, int duration){
    
		int topY = element.getLocation().getY();
		int bottomY = topY + element.getSize().getHeight();
		int centerX = element.getLocation().getX() + (element.getSize().getWidth()/2);
		driver.swipe(centerX, bottomY, centerX, topY, duration);
	
	}
	
	public static void swipeLeft()
	{
		  Dimension size = driver.manage().window().getSize();
		  Log.info("Dimension of screen is : "+size.toString());
		  int startx = (int) (size.width * 0.8);
		  Log.info("Start of X: "+Integer.toString(startx));
		  int endx = (int) (size.width * 0.2);
		  Log.info("End of X: "+Integer.toString(endx));
		  int starty = size.height / 2;
		  Log.info("Position of Y: "+Integer.toString(starty));
		  driver.swipe(startx, starty, endx, starty, 1000);
			System.out.println("Swipe Left");
	}
	public static void swipeRight()
	{
		  Dimension size = driver.manage().window().getSize();
		  Log.info("Dimension of screen is : "+size.toString());
		  int startx = (int) (size.width * 0.2);
		  Log.info("Start of X: "+Integer.toString(startx));
		  int endx = (int) (size.width * 0.8);
		  Log.info("End of X: "+Integer.toString(endx));
		  int starty = size.height / 2;
		  Log.info("Position of Y: "+Integer.toString(starty));
		  driver.swipe(startx, starty, endx, starty, 1000);
			System.out.println("Swipe Right");
	}
	public static void swipeUp()
	{
		driver.swipe(200, 900, 200, 400, 0);
	}
	public static void swipeDown()
	{
		driver.swipe(250, 400, 250, 900, 0);
	}
  
}