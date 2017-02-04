package main.java.qa.android.test.java;

import main.java.qa.android.page.SamplePageObject;
import main.java.qa.android.util.AppiumTest;
import static main.java.qa.android.util.Helpers.back;

import org.testng.annotations.Test;

public class PageObjectPatternTest extends AppiumTest {

    @Test(alwaysRun = true)
    public void pageObject() throws Exception {
        
    	SamplePageObject spqr = new SamplePageObject();
    	spqr.test();
    	
        //back();

    }
}