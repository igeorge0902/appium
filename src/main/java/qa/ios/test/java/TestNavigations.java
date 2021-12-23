package main.java.qa.ios.test.java;

import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import main.java.qa.ios.features.WithoutSignedIn;
import main.java.qa.ios.main.CaptureScreenshotOnFailureListener;
import main.java.qa.ios.main.TestBase;
import main.java.qa.ios.navigation.HomeScreen;
import main.java.qa.ios.navigation.Menu;
import main.java.qa.ios.navigation.SelectMovieFromCategory;
import main.java.qa.ios.navigation.VenueDetails;
import main.java.qa.ios.testng.LoggingListener;
import main.java.qa.ios.testng.TestListeners;
import main.java.qa.ios.testng.TestMethodListener;

@Listeners({TestListeners.class, CaptureScreenshotOnFailureListener.class, TestMethodListener.class, LoggingListener.class})
public class TestNavigations extends TestBase{

    @Test(alwaysRun = true)
    public void pageObject() throws Exception {
    	
    	HomeScreen.swipe();
    	//Menu.withoutSignedIn();
    	//Menu.logIn();
		SelectMovieFromCategory.movieFromCategory();
		//VenueDetails.scrollOnDetails();
		//VenueDetails.playGarnierMovie();
		VenueDetails.selectPickerDate();
		VenueDetails.bookChair();

    }
}
