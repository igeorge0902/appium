package main.java.qa.android.page;

import io.appium.java_client.MobileElement;
import io.appium.java_client.ios.IOSElement;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.iOSFindBy;

import java.util.List;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.FindBy;

import io.appium.java_client.pagefactory.AndroidFindBys;
import io.appium.java_client.pagefactory.iOSFindAll;;

/**
 * Here is the common sample shows how to use
 * Appium annotations against iOS. It is perfectly similar
 * as {@link ApiDemosListViewScreenSimple}, {@link ApiDemosListViewScreenChaided}
 * and {@link ApiDemosListViewScreenByAllPossible}
 * 
 * About Page Object design pattern read here:
 * https://code.google.com/p/selenium/wiki/PageObjects
 *
 */
public class PageAnnotations {

	/**
	 * Page Object best practice is to describe interactions with target 
	 * elements by methods. This methods describe business logic of the page/screen.
	 * Here lazy instantiated elements are public.
	 * It was done so just for obviousness
	 */

	@iOSFindBy(uiAutomator = ".elements()[0]")
	@AndroidFindBy(className = "android.widget.TextView")
	public List<WebElement> androidOriOsTextViews;

	@iOSFindBy(uiAutomator = ".elements()[0]")
	public List<MobileElement> iosMobileButtons;

	@FindBy(className = "UIAButton")
	public List<MobileElement> mobiletFindBy_Buttons;

	@iOSFindBy(uiAutomator = ".elements()[0]")
	public List<RemoteWebElement> iosRemoteElementViews;

	@AndroidFindBys({
		@AndroidFindBy(uiAutomator = "new UiSelector().resourceId(\"android:id/list\")"),
		@AndroidFindBy(className = "android.widget.TextView")
		})
	public List<WebElement> chainElementViews;

	@iOSFindBy(uiAutomator = ".elements()[0]")
	public WebElement iosUIAutomatorButton;
	
	@FindBy(className = "UIAButton")
	public WebElement iosUIButton;

	@FindBy(className = "UIAButton")
	public List<WebElement> iosUIButtons;

	@iOSFindBy(uiAutomator = ".elements()[0]")
	public List<WebElement> iosUIAutomatorButtons;
	
	@iOSFindBy(uiAutomator = ".buttons()[0]")
	public IOSElement iosButton;
	
	@iOSFindBy(uiAutomator = ".buttons()[0]")
	public List<IOSElement> iosButtons;
	
	@iOSFindBy(uiAutomator = ".scrollViews()[0]")
	public IOSElement iosScrollView;
	
	@iOSFindBy(uiAutomator = ".pageIndicators()[0]")
	public IOSElement iosPageIndicator;

	@AndroidFindBy(className = "android.widget.TextView")
	@iOSFindBy(uiAutomator = ".elements()[0]")
	public WebElement androidOriOsTextView;

	@AndroidFindBy (className = "android.widget.ImageButton")
	public WebElement androidImageButton;
	
	@AndroidFindBy (className = "android.widget.Button")
	public WebElement androidUIButton;
	
	@AndroidFindBy(uiAutomator = "new UiSelector().resourceId(\"android:id/text1\")")
	public WebElement androidUIAutomatorView;
	
	@AndroidFindBy(uiAutomator = "new UiSelector().resourceId(\"android:id/text1\")")
	public List<WebElement> androidUIAutomatorViews;


	
	@iOSFindBy(uiAutomator = ".elements()[0]")
	public MobileElement mobileButton;

	@FindBy(className = "UIAButton")
	public MobileElement mobiletFindBy_Button;
		
	@FindBy(className = "UIAImage")
	public MobileElement mobiletFindBy_Image;

	@iOSFindBy(uiAutomator = ".elements()[0]")
	public RemoteWebElement remotetextVieW;

	@AndroidFindBy (className = "android.widget.TableRow.LayaoutParams")
	public List<WebElement> androidTableRow;
	
	@AndroidFindBys({
		@AndroidFindBy(uiAutomator = "new UiSelector().resourceId(\"android:id/list\")"),
		@AndroidFindBy(className = "android.widget.TextView")
		})
	public WebElement chainElementView;

	@iOSFindAll({
		@iOSFindBy(xpath = "ComputeSumButton_Test"),	
		@iOSFindBy(tagName = "ComputeSumButton")	//it is real locator
	})
	public WebElement findAllElement;
	
	@iOSFindAll({
		@iOSFindBy(xpath = "ComputeSumButton_Test"),	
		@iOSFindBy(tagName = "ComputeSumButton")	//it is real locator
	})
	public List<WebElement> findAllElements;

}
