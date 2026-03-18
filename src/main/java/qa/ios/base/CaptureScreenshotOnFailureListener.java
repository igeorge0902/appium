package qa.ios.base;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
 * Implementation of a listener to take screen shots when using web
 * driver for tests that fail. Screen shots are saved to a screen shots folder
 * under test-output/html/ScreenShots/.
 *
 * Dependencies: Requires an instance of the IOSDriver from DriverManager.
 */
public class CaptureScreenshotOnFailureListener extends TestListenerAdapter
		implements Constants {
	private static final String ESCAPE_PROPERTY = "org.uncommons.reportng.escape-output";
	private static final Logger log = LoggerFactory.getLogger(CaptureScreenshotOnFailureListener.class);

	@Override
	public void onTestFailure(ITestResult testResult) {

		// call the superclass
		super.onTestFailure(testResult);

		// Get a driver instance from the web driver manager object
		IOSDriver driver = DriverManager.getDriverInstance();

		if (driver != null) {
			Calendar calendar = Calendar.getInstance();

			String workingDir = System.getProperty("user.dir");

			String screenShotsFolder = workingDir + File.separator
					+ "test-output" + File.separator + "html"
					+ File.separator + "ScreenShots" + File.separator;

			Path screenShotsFolderPath = Paths.get(screenShotsFolder);

			if (!screenShotsFolderPath.toFile().exists()) {
				screenShotsFolderPath.toFile().mkdirs();
			}

			// Create the filename for the screen shots
			String filename = screenShotsFolder + DriverManager.getApp()
					+ testResult.getMethod().getMethodName() + "-"
					+ "-" + calendar.get(Calendar.YEAR) + "-"
					+ calendar.get(Calendar.MONTH) + "-"
					+ calendar.get(Calendar.DAY_OF_MONTH) + "-"
					+ calendar.get(Calendar.HOUR_OF_DAY) + "-"
					+ calendar.get(Calendar.MINUTE) + "-"
					+ calendar.get(Calendar.SECOND) + "-"
					+ calendar.get(Calendar.MILLISECOND) + ".png";

			// Take the screen shot and then copy the file to the screen shot folder
			File scrFile = ((TakesScreenshot) driver)
					.getScreenshotAs(OutputType.FILE);

			try {
				FileUtils.copyFile(scrFile, new File(filename));
			} catch (IOException e) {
				log.error("Failed to save screenshot", e);
			}

			log.info(" *** Capture files are created in {}", screenShotsFolder);

			File file = new File(filename);
			System.setProperty(ESCAPE_PROPERTY, "false");

			String absolute = file.getAbsolutePath();

			Path pathAbsolute = Paths.get(absolute);
			Path pathBase = Paths.get(screenShotsFolder);
			Path pathRelative = pathBase.relativize(pathAbsolute);
			log.info("Screenshot relative path: {}", pathRelative);

			Reporter.log("<a href=\"" + pathRelative
					+ "\"><p align=\"left\">Error screenshot at " + new Date()
					+ "</p>");

			Reporter.log("<p><img width=\"50%\" src=\""
					+ file.getAbsoluteFile() + "\" alt=\"screenshot at "
					+ new Date() + "\"/></p></a><br />");

		} // end of if

	} // end of onTestFailure

}
