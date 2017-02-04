package main.java.qa.android.util;


import main.java.qa.android.main.TestBase;

import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class AppiumTest extends TestBase {

	static {
		// Disable annoying cookie warnings.
		// WARNING: Invalid cookie header
		LogFactory.getFactory().setAttribute("org.apache.commons.logging.Log",
				"org.apache.commons.logging.impl.NoOpLog");
	}

	/** wait wraps Helpers.wait **/
	public static WebElement wait(By locator) {
		return Helpers.wait(locator);
	}

}