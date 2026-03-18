package qa.ios.util;

import io.appium.java_client.AppiumBy;
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
import java.time.Duration;
import java.util.*;


public class Helpers extends TestBase implements Constants {


	public static WebElement pageIndicator() {
		return driver.findElement(By.className("XCUIElementTypePageIndicator"));
	}

	/**
	 * Get nr. of pages to swipe on the landing page.
	 * Nr. of pages are retrieved with the pattern: "[page].*[of]\\W+"
	 */
	public static int getSwipePages() {
		WebElement pageIndicator = pageIndicator();
		String pageValue = pageIndicator.getAttribute("value");

		java.util.regex.Pattern replace = java.util.regex.Pattern.compile("[page].*[of]\\W+");
		java.util.regex.Matcher matcher = replace.matcher(pageValue);
		int nrOfPages = Integer.parseInt(matcher.replaceAll(""));
		Log.info("Number of pages: {}", nrOfPages);

		return nrOfPages;
	}

	/**
	 * Set implicit wait in seconds
	 */
	public static void setWait(int seconds) {
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(seconds));
	}

	/**
	 * Get element by AccessibilityId
	 */
	public static WebElement getElement(String text) {
		return driver.findElement(AppiumBy.accessibilityId(text));
	}

	/**
	 * Return an element by locator
	 */
	public static WebElement element(By locator) {
		return driver.findElement(locator);
	}

	/**
	 * Press the back button
	 */
	public static void back() {
		driver.navigate().back();
	}

	/**
	 * Return a button element that contains the given text by AccessibilityId
	 */
	public static WebElement button(String text) {
		return element(AppiumBy.accessibilityId(text));
	}

	public static boolean appClosings() throws InterruptedException {
		Thread.sleep(2000);

		String bundleId = qa.ios.util.PropertyUtils.getProperty("bundleid");
		if (bundleId != null) {
			driver.terminateApp(bundleId);
		}
		Log.info("app is closing");

		Thread.sleep(1000);

		if (bundleId != null) {
			driver.activateApp(bundleId);
		}
		Log.info("app is launching");

		return true;
	}

	public static void takeScreenShot() {
		// take a screenshot using the normal selenium api.
		TakesScreenshot screen = (TakesScreenshot) new Augmenter().augment(driver);

		Calendar calendar = Calendar.getInstance();

		String workingDir = System.getProperty("user.dir");

		String screenShotsFolder = workingDir + File.separator
				+ "test-output" + File.separatorChar + "html"
				+ File.separator + "ScreenShots" + File.separator;

		Path screenShotsFolderPath = Paths.get(screenShotsFolder);

		if (!screenShotsFolderPath.toFile().exists()) {
			screenShotsFolderPath.toFile().mkdirs();
		}

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
		Log.info("screenshot taken: {}", ss.getAbsolutePath());
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
