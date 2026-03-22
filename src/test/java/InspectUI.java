import io.appium.java_client.AppiumBy;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import qa.ios.base.TestBase;
import qa.ios.navigation.HomeScreen;
import qa.ios.page.PageObjects;
import qa.ios.testng.LoggingListener;
import qa.ios.testng.TestListeners;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.Map;

/**
 * Inspection test — dumps the page source XML at each navigation step
 * so we can see the actual element types, names, and hierarchy on iOS 26.
 *
 * Run with:
 *   mvn clean test -f appium/pom.xml -DtestngXml=testng-inspect.xml \
 *       -Dsim.device="iPhone 16e" -Dsim.version="26.1" -Dsim.udid="..."
 *
 * Output: appium/target/page-sources/*.xml files
 */
@Listeners({TestListeners.class, LoggingListener.class})
public class InspectUI extends TestBase {

    /** 5 s max, poll every 0.5 s */
    private WebDriverWait waitFor() {
        return new WebDriverWait(driver, Duration.ofSeconds(5), Duration.ofMillis(500));
    }

    private WebElement waitAndFind(String predicateOrClassName, boolean isPredicate) {
        if (isPredicate) {
            return waitFor().until(ExpectedConditions.presenceOfElementLocated(
                    AppiumBy.iOSNsPredicateString(predicateOrClassName)));
        }
        return waitFor().until(ExpectedConditions.presenceOfElementLocated(
                AppiumBy.className(predicateOrClassName)));
    }

    private void dumpPageSource(String filename) {
        try {
            String source = driver.getPageSource();
            Path outDir = Path.of("target", "page-sources");
            Files.createDirectories(outDir);
            Path outFile = outDir.resolve(filename);
            Files.writeString(outFile, source);
            Log.info("Page source written to: {}", outFile.toAbsolutePath());
        } catch (IOException e) {
            Log.error("Failed to write page source: {}", filename, e);
        }
    }

    private void logElements(String type) {
        List<WebElement> elements = driver.findElements(AppiumBy.className(type));
        Log.info("=== {} elements ({}) ===", type, elements.size());
        for (int i = 0; i < elements.size(); i++) {
            try {
                WebElement el = elements.get(i);
                Log.info("  [{}] label='{}' name='{}' value='{}' visible={}",
                        i, el.getAttribute("label"), el.getAttribute("name"),
                        el.getAttribute("value"), el.getAttribute("visible"));
            } catch (Exception e) {
                Log.info("  [{}] <stale>", i);
            }
        }
    }

    @Test(priority = 1)
    public void step1_appLaunch() {
        Log.info("====== STEP 1: App launched ======");
        dumpPageSource("01-app-launch.xml");
        logElements("XCUIElementTypeButton");
    }

    @Test(priority = 2, dependsOnMethods = "step1_appLaunch")
    public void step2_afterSwipe() {
        Log.info("====== STEP 2: After swipe ======");
        HomeScreen.swipe();
        waitFor().until(ExpectedConditions.presenceOfElementLocated(
                AppiumBy.iOSNsPredicateString("label == 'Movies' AND visible == true")));
        dumpPageSource("02-after-swipe.xml");
    }

    @Test(priority = 3, dependsOnMethods = "step2_afterSwipe")
    public void step3_moviesScreen() {
        Log.info("====== STEP 3: Tap Movies, wait for data ======");
        PageObjects.movies().click();

        // First confirm we're on the Movies screen by waiting for the search field
        waitFor().until(ExpectedConditions.presenceOfElementLocated(
                AppiumBy.className("XCUIElementTypeSearchField")));
        Log.info("Search field found — on Movies screen");

        // Dump immediately to see the screen state
        dumpPageSource("03a-movies-before-data.xml");

        // Now wait for the table to populate (cells appear when API data loads).
        // This is a network call to the backend — allow 10 s.
        new WebDriverWait(driver, Duration.ofSeconds(10), Duration.ofMillis(500))
                .until(ExpectedConditions.presenceOfElementLocated(
                        AppiumBy.iOSNsPredicateString("type == 'XCUIElementTypeCell' AND visible == true")));
        Log.info("Table cells found — data loaded");

        dumpPageSource("03-movies-screen.xml");
        logElements("XCUIElementTypeSegmentedControl");
    }

    @Test(priority = 4, dependsOnMethods = "step3_moviesScreen")
    public void step4_tapCategories() {
        Log.info("====== STEP 4: Tap Categories ======");
        // The segmented control is in the table section header with buttons: Reset, Categories, NA
        // Use class chain to target the 2nd button inside the segmented control
        WebElement categories = waitFor().until(ExpectedConditions.elementToBeClickable(
                AppiumBy.iOSClassChain("**/XCUIElementTypeSegmentedControl/XCUIElementTypeButton[`label == 'Categories'`]")));
        categories.click();
        // Wait for the category list to appear
        waitAndFind("label == 'Drama'", true);
        dumpPageSource("04-after-categories.xml");
    }

    @Test(priority = 5, dependsOnMethods = "step4_tapCategories")
    public void step5_tapDrama() {
        Log.info("====== STEP 5: Tap Drama ======");
        waitAndFind("label == 'Drama'", true).click();
        // Wait for drama movies to load
        waitAndFind("label == '12 Angry Men'", true);
        dumpPageSource("05-drama.xml");
    }

    @Test(priority = 6, dependsOnMethods = "step5_tapDrama")
    public void step6_tapMovie() {
        Log.info("====== STEP 6: Tap '12 Angry Men' ======");
        waitAndFind("label == '12 Angry Men'", true).click();
        // Wait for venues to load
        waitAndFind("label == 'Cinema City Allee'", true);
        dumpPageSource("06-venues.xml");
        logElements("XCUIElementTypeCell");
    }

    @Test(priority = 7, dependsOnMethods = "step6_tapMovie")
    public void step7_tapVenue() {
        Log.info("====== STEP 7: Tap 'Cinema City Allee' ======");
        waitAndFind("label == 'Cinema City Allee'", true).click();
        // Wait for venue details screen — the "Dates" button appears
        waitAndFind("label == 'Dates'", true);
        dumpPageSource("07-venue-details.xml");
        logElements("XCUIElementTypeButton");
    }

    @Test(priority = 8, dependsOnMethods = "step7_tapVenue")
    public void step8_tapDatesAndSelect() {
        Log.info("====== STEP 8: Tap Dates, select date via picker scroll ======");
        waitAndFind("label == 'Dates'", true).click();

        // Wait for picker wheel to appear
        WebElement picker = waitAndFind("XCUIElementTypePickerWheel", false);
        Log.info("Picker initial value: '{}'", picker.getAttribute("value"));
        dumpPageSource("08-dates-popover.xml");

        // Scroll picker to next value (row 0 = "Select date", row 1 = actual date)
        driver.executeScript("mobile: selectPickerWheelValue", Map.of(
                "element", picker,
                "order", "next"
        ));

        String selected = picker.getAttribute("value");
        Log.info("Picker value after scroll: '{}'", selected);
        dumpPageSource("09-date-selected.xml");
    }

    @Test(priority = 9, dependsOnMethods = "step8_tapDatesAndSelect")
    public void step9_dismissAndBook() {
        Log.info("====== STEP 9: Dismiss popover, tap Book ======");
        // Dismiss popover by tapping outside
        driver.executeScript("mobile: tap", Map.of("x", 195, "y", 100));

        // Wait for the Book button to be visible again
        waitAndFind("label == 'Book'", true).click();

        // Should now get a seat popover (not the "Select dates first!" alert)
        dumpPageSource("10-after-book.xml");
        logElements("XCUIElementTypeButton");
        logElements("XCUIElementTypeCell");
        logElements("XCUIElementTypeCollectionView");

        // Look for seat labels
        for (String seat : new String[]{"A1", "A2", "A3", "B1", "B5"}) {
            try {
                WebElement el = driver.findElement(AppiumBy.iOSNsPredicateString("label == '" + seat + "'"));
                Log.info("  Seat {} found, visible={}", seat, el.getAttribute("visible"));
            } catch (Exception e) {
                Log.info("  Seat {} NOT found", seat);
            }
        }
    }
}
