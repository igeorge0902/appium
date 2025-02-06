
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import qa.ios.base.CaptureScreenshotOnFailureListener;
import qa.ios.base.TestBase;
import qa.ios.navigation.HomeScreen;
import qa.ios.navigation.SelectMovieFromCategory;
import qa.ios.navigation.VenueDetails;
import qa.ios.testng.LoggingListener;
import qa.ios.testng.TestListeners;
import qa.ios.testng.TestMethodListener;

@Listeners({TestListeners.class, CaptureScreenshotOnFailureListener.class, TestMethodListener.class, LoggingListener.class})
public class TestNavigations extends TestBase {

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
