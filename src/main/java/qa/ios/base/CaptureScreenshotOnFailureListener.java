package qa.ios.base;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.TestListenerAdapter;
import io.appium.java_client.ios.IOSDriver;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.Date;

/**
 * Author: Date: 13/01/2012
 *
 * Summary: Implementation of a listener to take screen shots when using web
 * driver for tests that fail. Screen shots are saved to a screen shots folder
 * on the users Desktop in the folder /Desktop/Test-Failure-Screenshots/, must
 * have admin rights so the folder is created and saved etc.
 *
 * Dependencies: Requires an instance of the browsers web driver as this class
 * calls the WebDriverManager to get the browsers web driver instance.
 *
 */

public class CaptureScreenshotOnFailureListener extends TestListenerAdapter
		implements Constants {
	private static final String ESCAPE_PROPERTY = "org.uncommons.reportng.escape-output";
	private static Logger Log = Logger.getLogger(Logger.class.getName());

	@Override
	public void onTestFailure(ITestResult testResult) {

		// call the superclass
		super.onTestFailure(testResult);

		// Get a driver instance from the web driver manager object
		IOSDriver driver = DriverManager.getDriverInstance();

		/*
		 * We can only take screen shots for those browsers that support screen
		 * shot capture, html unit does not support screenshots as part of its
		 * capabilities.
		 */
		if (driver instanceof IOSDriver) {
			// Create a calendar object so we can create a date and time for the
			// screenshot
			Calendar calendar = Calendar.getInstance();

			// Get the users home path and append the screen shots folder
			// destination
			String workingDir = System.getProperty("user.dir");

			String screenShotsFolder = workingDir + File.separator
					+ "test-output" + File.separator + "html"
					+ File.separator + "ScreenShots" + File.separator;

			Path screenShotsFolderPath = Paths.get(screenShotsFolder);

			if (screenShotsFolderPath.toFile().isDirectory()) {
				if (!screenShotsFolderPath.toFile().exists()) {
					screenShotsFolderPath.toFile().mkdir();
				}

			}

			// Create the filename for the screen shots
			String filename = screenShotsFolder + DriverManager.getApp()
					+ testResult.getMethod().getMethodName() +"-"
					+ "-" + calendar.get(Calendar.YEAR) + "-"
					+ calendar.get(Calendar.MONTH) + "-"
					+ calendar.get(Calendar.DAY_OF_MONTH) + "-"
					+ calendar.get(Calendar.HOUR_OF_DAY) + "-"
					+ calendar.get(Calendar.MINUTE) + "-"
					+ calendar.get(Calendar.SECOND) + "-"
					+ calendar.get(Calendar.MILLISECOND) + ".png";

			// TODO: pictures to be copied into the user.dir to get them
			// relative path

			// Take the screen shot and then copy the file to the screen shot
			// folder
			File scrFile = ((TakesScreenshot) driver)
					.getScreenshotAs(OutputType.FILE);

			try {
				FileUtils.copyFile(scrFile, new File(filename));
			} catch (IOException e) {
				e.printStackTrace();
			}

			System.out.println(" *** Capture files are created in "
					+ screenShotsFolder + File.separator);
			Log.info(" *** Capture files are created in " + screenShotsFolder
					+ File.separator);

			File file = new File(filename);
			System.setProperty(ESCAPE_PROPERTY, "false");

			String absolute = file.getAbsolutePath();
			// int beginIndex = absolute.indexOf(".");
			// String relative = absolute.substring(beginIndex).replace(".\\",
			// File.separator);
			// String screenShot = relative.replace('\\', '/');

			Path pathAbsolute = Paths.get(absolute);
			Path pathBase = Paths.get(screenShotsFolder);
			Path pathRelative = pathBase.relativize(pathAbsolute);
			Log.info(pathRelative);

			Reporter.log("<a href=\"" + pathRelative
					+ "\"><p align=\"left\">Error screenshot at " + new Date()
					+ "</p>");

			Reporter.log("<p><img width=\"50%\" src=\""
					+ file.getAbsoluteFile() + "\" alt=\"screenshot at "
					+ new Date() + "\"/></p></a><br />");

		} // end of if

	} // end of onTestFailure

} // enf of class

