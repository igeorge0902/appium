import io.appium.java_client.AppiumBy;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import qa.ios.base.TestBase;
import qa.ios.navigation.HomeScreen;
import qa.ios.page.PageObjects;
import qa.ios.testng.LoggingListener;
import qa.ios.testng.TestListeners;
import qa.ios.testng.TestMethodListener;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Inspection test — dumps the page source XML at each navigation step
 * so we can see the actual element types, names, and hierarchy on iOS 26.
 *
 * Run with:
 *   mvn clean test -Dtest=InspectUI -Dsim.device="iPhone 16e" -Dsim.version="26.1" -Dsim.udid="..."
 *
 * Output: target/page-source-*.xml files
 */
@Listeners({TestListeners.class, LoggingListener.class})
public class InspectUI extends TestBase {

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
            WebElement el = elements.get(i);
            String label = el.getAttribute("label");
            String name = el.getAttribute("name");
            String value = el.getAttribute("value");
            String visible = el.getAttribute("visible");
            Log.info("  [{}] label='{}' name='{}' value='{}' visible={}", i, label, name, value, visible);
        }
    }

    @Test(priority = 1)
    public void step1_appLaunch() {
        Log.info("====== STEP 1: App launched ======");
        dumpPageSource("01-app-launch.xml");
        logElements("XCUIElementTypeButton");
    }

    @Test(priority = 2, dependsOnMethods = "step1_appLaunch")
    public void step2_afterSwipe() throws Exception {
        Log.info("====== STEP 2: After swipe ======");
        HomeScreen.swipe();
        Thread.sleep(2000);
        dumpPageSource("02-after-swipe.xml");
        logElements("XCUIElementTypeButton");
    }

    @Test(priority = 3, dependsOnMethods = "step2_afterSwipe")
    public void step3_moviesScreen() throws Exception {
        Log.info("====== STEP 3: Movies screen ======");

        // Try to tap Movies button
        try {
            PageObjects.movies().click();
            Thread.sleep(3000);
        } catch (Exception e) {
            Log.error("Could not tap Movies button: {}", e.getMessage());
        }

        dumpPageSource("03-movies-screen.xml");

        // Log all buttons
        logElements("XCUIElementTypeButton");

        // Look for segmented controls
        Log.info("--- Segmented controls ---");
        logElements("XCUIElementTypeSegmentedControl");

        // Look for segments (iOS 26 may use this instead of buttons inside segmented controls)
        Log.info("--- Segments ---");
        List<WebElement> segments = driver.findElements(AppiumBy.iOSClassChain("**/XCUIElementTypeSegmentedControl/*"));
        for (int i = 0; i < segments.size(); i++) {
            WebElement el = segments.get(i);
            Log.info("  Segment[{}] type='{}' label='{}' name='{}'",
                    i, el.getAttribute("type"), el.getAttribute("label"), el.getAttribute("name"));
        }
    }

    @Test(priority = 4, dependsOnMethods = "step3_moviesScreen")
    public void step4_afterCategories() throws Exception {
        Log.info("====== STEP 4: After tapping Categories ======");

        // Try multiple strategies to tap Categories
        boolean found = false;

        // Strategy 1: predicate string
        try {
            driver.findElement(AppiumBy.iOSNsPredicateString("label == 'Categories'")).click();
            found = true;
            Log.info("Found 'Categories' via predicate");
        } catch (Exception e) {
            Log.info("Predicate 'Categories' not found: {}", e.getMessage());
        }

        // Strategy 2: class chain with button
        if (!found) {
            try {
                driver.findElement(AppiumBy.iOSClassChain("**/XCUIElementTypeSegmentedControl/XCUIElementTypeButton[2]")).click();
                found = true;
                Log.info("Found Categories via class chain (Button[2])");
            } catch (Exception e) {
                Log.info("Class chain Button[2] not found: {}", e.getMessage());
            }
        }

        // Strategy 3: class chain with segment
        if (!found) {
            try {
                driver.findElement(AppiumBy.iOSClassChain("**/XCUIElementTypeSegmentedControl/XCUIElementTypeSegment[2]")).click();
                found = true;
                Log.info("Found Categories via class chain (Segment[2])");
            } catch (Exception e) {
                Log.info("Class chain Segment[2] not found: {}", e.getMessage());
            }
        }

        // Strategy 4: accessibility ID
        if (!found) {
            try {
                driver.findElement(AppiumBy.accessibilityId("Categories")).click();
                found = true;
                Log.info("Found Categories via accessibilityId");
            } catch (Exception e) {
                Log.info("AccessibilityId 'Categories' not found: {}", e.getMessage());
            }
        }

        Thread.sleep(2000);
        dumpPageSource("04-after-categories.xml");

        if (found) {
            // Log what's in the category list
            Log.info("--- Looking for category items (Drama, Action, etc.) ---");
            for (String cat : new String[]{"Action", "Drama", "Crime", "Romance", "Troll"}) {
                try {
                    WebElement el = driver.findElement(AppiumBy.iOSNsPredicateString("label == '" + cat + "'"));
                    Log.info("  Found category: {} (visible={})", cat, el.getAttribute("visible"));
                } catch (Exception e) {
                    Log.info("  Category NOT found: {}", cat);
                }
            }
        } else {
            Log.error("Could not find Categories button with any strategy!");
        }
    }
}

