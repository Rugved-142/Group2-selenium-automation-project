package tests;

import base.BaseTest;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import utils.ExcelReader;
import utils.ReportManager;
import utils.ScreenshotHelper;

import java.time.Duration;
import java.util.Map;

public class Scenario3Test extends BaseTest {

    private static final String SCENARIO = "scenario3_snell_library";
    private ExtentTest extentTest;

    @Test
    public void reserveSnellLibrarySpot() {
        extentTest = ReportManager.createTest("Scenario 3: Reserve Snell Library Study Room");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        JavascriptExecutor js = (JavascriptExecutor) driver;

        // Read URL (and optional scenario name) from Excel to preserve original data-driven behavior
        Map<String, String> creds = ExcelReader.getLoginCredentials();   
        
        String libraryUrl = creds.get("libraryURL");

        try {
            // Open Library URL
            ScreenshotHelper.takeScreenshot(driver, SCENARIO, "a_library_home", "before");
            driver.get(libraryUrl);
            wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));

            // Handle cookie/consent banner if present
            try {
                WebDriverWait bannerWait = new WebDriverWait(driver, Duration.ofSeconds(5));
                WebElement closeBannerBtn = bannerWait.until(
                        ExpectedConditions.elementToBeClickable(By.id("reject-all"))
                );
                closeBannerBtn.click();
                extentTest.log(Status.INFO, "Reject consent bannner");
            } catch (TimeoutException | NoSuchElementException ignored) {
                // Banner not present or already closed; continue
            }

            ScreenshotHelper.takeScreenshot(driver, SCENARIO, "a_library_home", "after");
            extentTest.log(Status.PASS, "Step a: Opened Library URL from Excel");

            Thread.sleep(2000);
            // Select 'Reserve A Study Room'
            clickAndCapture(By.linkText("Reserve A Study Room"), wait, js, "b_reserve_room_clicked");
            extentTest.log(Status.PASS, "Step b: Clicked 'Reserve A Study Room'");

            Thread.sleep(2000);
            // Select 'Boston'
            js.executeScript("window.scrollBy(0, 800);");
            clickAndCapture(By.xpath("//img[contains(@alt, 'Boston')]/ancestor::a"), wait, js, "c_boston_selected");
            extentTest.log(Status.PASS, "Step c: Selected 'Boston' campus");

            // Click 'Book a Room'
            js.executeScript("window.scrollBy(0, 500);");
            clickAndCapture(By.linkText("Book a Room"), wait, js, "d_book_a_room_clicked");
            extentTest.log(Status.PASS, "Step d: Clicked 'Book a Room'");

            // Select 'Individual Study' from Seat Style
            WebElement seatStyleDropdown = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//select[@id='gid']")
                )
            );
            Thread.sleep(2000);
            Select seatSelect = new Select(seatStyleDropdown);
            seatSelect.selectByVisibleText("Individual Study");
            ScreenshotHelper.takeScreenshot(driver, SCENARIO, "e1_seat_style_selected", "after");
            extentTest.log(Status.PASS, "Step e1: Selected 'Individual Study' seat style");

            Thread.sleep(2000);
            // Select 'Space for 1-4 people' from Capacity
            WebElement capacityDropdown = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//select[@id='capacity']")
                )
            );

            Select capacitySelect = new Select(capacityDropdown);
            capacitySelect.selectByVisibleText("Space For 1-4 people");
            ScreenshotHelper.takeScreenshot(driver, SCENARIO, "e2_capacity_selected", "after");
            extentTest.log(Status.PASS, "Step e2: Selected 'Space for 1-4 people' capacity");

            Thread.sleep(2000);
            // Final Assertion: Verify we are on the correct scheduling page (LibCal)
            boolean isSuccess = wait.until(ExpectedConditions.urlContains("libcal"));
            Assert.assertTrue(isSuccess, "Failed to reach the LibCal booking system.");
            ScreenshotHelper.takeScreenshot(driver, SCENARIO, "z_libcal_reached", "after");
            extentTest.log(Status.PASS, "Scenario 3: LibCal booking system reached successfully");
            System.out.println("Scenario 3 Completed Successfully");

        } catch (Exception e) {
            ScreenshotHelper.takeScreenshot(driver, SCENARIO, "failure", "after");
            extentTest.log(Status.FAIL, "Scenario 3 FAILED: " + e.getMessage());
            Assert.fail("Scenario 3 failed: " + e.getMessage());
        }
    }

    private void clickAndCapture(By locator, WebDriverWait wait, JavascriptExecutor js, String step) {
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
        js.executeScript("arguments[0].scrollIntoView({block: 'center'});", element);
        ScreenshotHelper.takeScreenshot(driver, SCENARIO, step, "before");
        try {
            element.click();
        } catch (ElementClickInterceptedException ex) {
            // Fallback to JS click if another element momentarily intercepts
            js.executeScript("arguments[0].click();", element);
        }
        ScreenshotHelper.takeScreenshot(driver, SCENARIO, step, "after");
    }

    @AfterClass
    public void generateReport() {
        ReportManager.flushReports();
        System.out.println("Report generated: reports/TestReport.html");
    }
}
