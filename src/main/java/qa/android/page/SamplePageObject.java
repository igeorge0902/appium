package main.java.qa.android.page;

import static main.java.qa.android.util.Helpers.find;

import io.appium.java_client.MobileBy;
import main.java.qa.android.main.TestBase;
import main.java.qa.android.util.Helpers;
import main.java.qa.android.util.WaitTool;

/** Page object for the accessibility page **/
public class SamplePageObject extends TestBase {
	
	public SamplePageObject test() throws Exception{
		
    	Helpers.elementByUISelector("header");
        System.out.println("Screen Loaded");
        
        WaitTool.waitForElementPresent(driver, MobileBy.AccessibilityId("Accessibility"), 3);        
        find("Accessibility").click();
        Helpers.loaded();
    	
		return new SamplePageObject();
		
	}

}