package qa.ios.util;

import io.appium.java_client.MobileBy;
import io.appium.java_client.MobileElement;
import io.appium.java_client.ios.IOSElement;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.Augmenter;
import qa.ios.base.Constants;
import qa.ios.base.TestBase;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Helpers extends TestBase implements Constants {
	
	
	public static WebElement pageIndicator() {
		  return driver.findElement(By.className("UIAPageIndicator"));

	}
	
	/**
	 * Get nr. of of pages to swipe on the landing page.
	 * Nr. of pages are retrieved with the patters:  "[page].*[of]\\W+"
	 * 
	 */
	public static int getSwipePages() {
        
		WebElement pageIndicator = pageIndicator();
        String pageValue = pageIndicator.getAttribute("value");
    
		Pattern replace = Pattern.compile("[page].*[of]\\W+");
		Matcher matcher = replace.matcher(pageValue);
		int nrOfPages = Integer.parseInt(matcher.replaceAll(""));
		Log.info(nrOfPages);
		
		return nrOfPages;
	}

	/**
    Set implicit wait in seconds 
    */
  public static void setWait(int seconds) {
    driver.manage().timeouts().implicitlyWait(seconds, TimeUnit.SECONDS);
  }

   /**
   * Wrap WebElement in MobileElement *
   */
  private static MobileElement mobileElement(WebElement element) {
    return new IOSElement();
  }
  
  /** Get element by AccessibilityId **/
  public static WebElement getElement(String text) {
      return Helpers.element(MobileBy.AccessibilityId(text));
  }

  /**
   * Wrap WebElement in MobileElement *
   */
  public static List<MobileElement> elements(List<WebElement> elements) {
    List<MobileElement> list = new ArrayList<MobileElement>();
    for (WebElement element : elements) {
      list.add(mobileElement(element));
    }

    return list;
  }

  /**
   * Return an element by locator *
   */
  public static MobileElement element(By locator) {
    return mobileElement(driver.findElement(locator));
  }

  /**
   * Press the back button *
   */
  public static void back() {
    driver.navigate().back();
  }
  
  
  /**
   * Return a static button element that contains the given text by AccessibilityId*
   */
  public static MobileElement button(String text){
  return element(MobileBy.AccessibilityId(text));
  }

  
  public static boolean appClosings() throws InterruptedException{
	        

      Thread.sleep(2000);
      
      driver.closeApp();
      Log.info("app is closing");

      Thread.sleep(1000);
      
      driver.launchApp();
      Log.info("app is launching");
      
	return true;
  }
  
  public static void takeScreenShot(){
	   
	  		// take a screenshot using the normal selenium api.
	  		TakesScreenshot screen =(TakesScreenshot)new Augmenter().augment(driver);
   
   			// screenshot
			Calendar calendar = Calendar.getInstance();

			// Get the users home path and append the screen shots folder
			// destination
			String workingDir = System.getProperty("user.dir");

			String screenShotsFolder = workingDir + File.separator
					+ "test-output" + File.separatorChar + "html"
					+ File.separator + "ScreenShots" + File.separator;

			Path screenShotsFolderPath = Paths.get(screenShotsFolder);

			if (screenShotsFolderPath.toFile().isDirectory()) {
				if (!screenShotsFolderPath.toFile().exists()) {
					screenShotsFolderPath.toFile().mkdir();
				}

			}

			// Create the filename for the screen shots
			String filename = screenShotsFolder 
					+ "-" + calendar.get(Calendar.YEAR) + "-"
					+ calendar.get(Calendar.MONTH) + "-"
					+ calendar.get(Calendar.DAY_OF_MONTH) + "-"
					+ calendar.get(Calendar.HOUR_OF_DAY) + "-"
					+ calendar.get(Calendar.MINUTE) + "-"
					+ calendar.get(Calendar.SECOND) + "-"
					+ calendar.get(Calendar.MILLISECOND) + ".png";
   
   File ss = new File(filename);
   screen.getScreenshotAs(OutputType.FILE).renameTo(ss);
   System.out.println("screenshot take :"+ss.getAbsolutePath());
  }
  
  public static void swipeScreen() {
	
	  Map<String, Object> args = new HashMap<>();
		args.put("duration", 1.5);
		args.put("fromX", 100);
		args.put("fromY", 100);
		args.put("toX", 300);
		args.put("toY", 600);
		driver.executeScript("mobile: dragFromToForDuration", args);	  
  }
  
}
      
  
