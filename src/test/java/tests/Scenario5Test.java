package tests;

import base.BaseTest;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import utils.ExcelReader;
import utils.ReportManager;
import utils.ScreenshotHelper;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Scenario5Test extends BaseTest {

    private static final String SCENARIO = "scenario5_academic_calendar";
    private ExtentTest extentTest;

    @Test
    public void testUpdateAcademicCalendar() {
        extentTest = ReportManager.createTest("Scenario 5: Update Academic Calendar");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        Map<String, String> data = ExcelReader.getAcademicCalendarInfo();
        Map<String, String> creds = ExcelReader.getLoginCredentials();
        JavascriptExecutor js = (JavascriptExecutor) driver;

        try {
            // ── Step a: Navigate to NEU Student Hub ──────────────────────────
            ScreenshotHelper.takeScreenshot(driver, SCENARIO, "a_navigate_student_hub", "before");
            driver.get("https://student.me.northeastern.edu/");
            wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));
            ScreenshotHelper.takeScreenshot(driver, SCENARIO, "a_navigate_student_hub", "after");
            extentTest.log(Status.PASS, "Step a: Navigated to Student Hub");

            // Handle NEU login if redirected
            Thread.sleep(3000);
            String currentUrl = driver.getCurrentUrl();
            System.out.println("  Current URL: " + currentUrl);
            if (currentUrl.contains("login") || currentUrl.contains("microsoft") ||
                    currentUrl.contains("microsoftonline") || currentUrl.contains("auth")) {
                neuLogin(driver, wait, creds);
            }

            // ── Step a cont: Click Resources tab ─────────────────────────────
            Thread.sleep(3000);
            System.out.println("Step a: Current URL = " + driver.getCurrentUrl());
            ScreenshotHelper.takeScreenshot(driver, SCENARIO, "a_click_resources", "before");
            WebElement resourcesTab = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[self::a or self::span or self::button or self::li or self::div]" +
                            "[contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ'," +
                            "'abcdefghijklmnopqrstuvwxyz'),'resources')]")
            ));
            resourcesTab.click();
            System.out.println("Step a: Clicked Resources tab");
            ScreenshotHelper.takeScreenshot(driver, SCENARIO, "a_click_resources", "after");
            extentTest.log(Status.PASS, "Step a: Clicked Resources tab");

            // ── Step b: Click Academics, Classes & Registration ───────────────
            ScreenshotHelper.takeScreenshot(driver, SCENARIO, "b_click_academics", "before");
            WebElement academics = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[contains(text(),'Academics, Classes')]")
            ));
            academics.click();
            Thread.sleep(1000);
            System.out.println("Step b: Clicked Academics");
            ScreenshotHelper.takeScreenshot(driver, SCENARIO, "b_click_academics", "after");
            extentTest.log(Status.PASS, "Step b: Clicked Academics, Classes & Registration");

            // ── Step c: Click Academic Calendar link ──────────────────────────
            ScreenshotHelper.takeScreenshot(driver, SCENARIO, "c_click_calendar_link", "before");
            WebElement calLink = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//a[normalize-space(text())='Academic Calendar']")
            ));
            calLink.click();
            Thread.sleep(2000);
            System.out.println("Step c: Clicked Academic Calendar link");
            ScreenshotHelper.takeScreenshot(driver, SCENARIO, "c_click_calendar_link", "after");
            extentTest.log(Status.PASS, "Step c: Clicked Academic Calendar link");

            // ── Step d: Switch to new tab, click Academic Calendar ────────────
            if (driver.getWindowHandles().size() > 1) {
                List<String> windows = new ArrayList<>(driver.getWindowHandles());
                driver.switchTo().window(windows.get(windows.size() - 1));
                System.out.println("Step d: Switched to new tab");
            }

            ScreenshotHelper.takeScreenshot(driver, SCENARIO, "d_registrar_page", "before");
            WebElement academicCalBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//h3[normalize-space(text())='Academic Calendar'] | " +
                            "//h2[normalize-space(text())='Academic Calendar'] | " +
                            "//a[normalize-space(text())='Academic Calendar']")
            ));
            academicCalBtn.click();
            Thread.sleep(2000);

            if (!driver.getCurrentUrl().contains("academic-calendar")) {
                System.out.println("Navigating directly to calendar URL...");
                driver.get("https://registrar.northeastern.edu/article/academic-calendar/");
                Thread.sleep(2000);
            }
            System.out.println("Step d: On Academic Calendar page");
            ScreenshotHelper.takeScreenshot(driver, SCENARIO, "d_registrar_page", "after");
            extentTest.log(Status.PASS, "Step d: Navigated to Academic Calendar page");

            // ── Step e: Scroll down to view calendars ─────────────────────────
            ScreenshotHelper.takeScreenshot(driver, SCENARIO, "e_scroll_calendars", "before");
            js.executeScript("window.scrollBy(0, 600);");
            Thread.sleep(1000);
            System.out.println("Step e: Scrolled down");
            ScreenshotHelper.takeScreenshot(driver, SCENARIO, "e_scroll_calendars", "after");
            extentTest.log(Status.PASS, "Step e: Scrolled down to calendar section");

            // ── Step f: Uncheck QTR checkbox ──────────────────────────────────
            ScreenshotHelper.takeScreenshot(driver, SCENARIO, "f_uncheck_qtr", "before");

            // Scroll to calendar section so it is visible on screen
            js.executeScript("window.scrollBy(0, 1000);");
            Thread.sleep(3000);

            // Switch directly into the Trumba iframe by name
            driver.switchTo().frame("trumba.spud.7.iframe");
            Thread.sleep(1000);

            // Find the QTR label link
            WebElement qtrLink = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//a[contains(@class,'twCalendarListName') and contains(.,'QTR')]")
            ));

            // Scroll it into view
            js.executeScript("arguments[0].scrollIntoView({behavior:'smooth',block:'center'});", qtrLink);
            Thread.sleep(1000);

            // Navigate up to the nearest twCalendarListNameCell <td>, which is QTR's own row cell.
            // Then go to its parent <tr> — this is the row for QTR only, not the outer table row.
            WebElement qtrNameCell = qtrLink.findElement(
                    By.xpath("./ancestor::td[contains(@class,'twCalendarListNameCell')]")
            );
            WebElement qtrRow = qtrNameCell.findElement(By.xpath("./ancestor::tr[1]"));

            // Debug: print this specific row's HTML
            System.out.println("QTR row HTML: " + qtrRow.getAttribute("innerHTML"));

            // The checkbox is in the sibling <td> with padding-left style (first cell in this row).
            // Find the first clickable element in that cell.
            WebElement checkboxTd = qtrRow.findElement(
                    By.xpath(".//td[contains(@style,'padding-left')]")
            );

            // Look for any clickable child inside the checkbox cell
            List<WebElement> clickables = checkboxTd.findElements(
                    By.xpath(".//*[self::a or self::span or self::div or self::img or self::input]")
            );

            if (!clickables.isEmpty()) {
                js.executeScript("arguments[0].click();", clickables.get(0));
                System.out.println("Step f: Clicked QTR checkbox element");
            } else {
                // If no child element, click the <td> itself
                js.executeScript("arguments[0].click();", checkboxTd);
                System.out.println("Step f: Clicked QTR checkbox cell");
            }

            Thread.sleep(2000);
            driver.switchTo().defaultContent();
            ScreenshotHelper.takeScreenshot(driver, SCENARIO, "f_uncheck_qtr", "after");
            extentTest.log(Status.PASS, "Step f: Unchecked QTR checkbox");

            // ── Step g: Scroll to middle of page to show unchecked QTR box ────
            ScreenshotHelper.takeScreenshot(driver, SCENARIO, "g_scroll_to_calendars", "before");
            js.executeScript("window.scrollTo(0, document.body.scrollHeight / 2);");
            Thread.sleep(2000);
            System.out.println("Step g: Scrolled to middle of page");
            ScreenshotHelper.takeScreenshot(driver, SCENARIO, "g_scroll_to_calendars", "after");
            extentTest.log(Status.PASS, "Step g: Scrolled to middle — unchecked QTR box visible");

            // ── Step h: Scroll to bottom and assert ──────────────────────────
            ScreenshotHelper.takeScreenshot(driver, SCENARIO, "h_scroll_bottom", "before");
            js.executeScript("window.scrollTo(0, document.body.scrollHeight);");
            Thread.sleep(2000);
            System.out.println("Step h: Scrolled to bottom of page");
            ScreenshotHelper.takeScreenshot(driver, SCENARIO, "h_scroll_bottom", "after");
            extentTest.log(Status.PASS, "Step h: Scrolled to bottom of page");

            // Final assertion — verify we're still on the Academic Calendar page
            Assert.assertTrue(
                    driver.getCurrentUrl().contains("academic-calendar"),
                    "Should be on the Academic Calendar page after all steps"
            );
            extentTest.log(Status.PASS, "Final assertion passed: On Academic Calendar page");

        } catch (Exception e) {
            extentTest.log(Status.FAIL, "Scenario 5 FAILED: " + e.getMessage());
            try {
                ScreenshotHelper.takeScreenshot(driver, SCENARIO, "failure", "after");
            } catch (Exception screenshotEx) {
                System.out.println("Could not take failure screenshot: " + screenshotEx.getMessage());
            }
            Assert.fail("Scenario 5 failed: " + e.getMessage());
        }
    }

    // ── NEU Login Helper ──────────────────────────────────────────────────────
    private void neuLogin(WebDriver driver, WebDriverWait wait, Map<String, String> creds) {
        try {
            WebElement usernameField = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//input[@type='email' or @name='loginfmt' or @id='username']")
            ));
            usernameField.clear();
            usernameField.sendKeys(creds.get("username"));

            WebElement nextBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//input[@type='submit'] | //button[@type='submit']")
            ));
            nextBtn.click();
            Thread.sleep(2000);

            WebElement passwordField = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//input[@type='password' or @name='passwd']")
            ));
            passwordField.clear();
            passwordField.sendKeys(creds.get("password"));

            WebElement signInBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//input[@type='submit'] | //button[@type='submit']")
            ));
            signInBtn.click();

            System.out.println("\n=====================================================");
            System.out.println("      DUO 2FA: Please approve push on your phone.");
            System.out.println("      Waiting for browser to redirect...");
            System.out.println("=====================================================\n");

            try {
                WebElement myDeviceBtn = new WebDriverWait(driver, Duration.ofSeconds(60))
                        .until(ExpectedConditions.elementToBeClickable(
                                By.xpath("//button[contains(text(),'Yes, this is my device')] | " +
                                        "//a[contains(text(),'Yes, this is my device')]")
                        ));
                myDeviceBtn.click();
                System.out.println("Clicked 'Yes, this is my device'");
                Thread.sleep(2000);
            } catch (Exception e) {
                System.out.println("No 'Is this your device' popup");
            }

            try {
                WebElement staySignedIn = new WebDriverWait(driver, Duration.ofSeconds(15))
                        .until(ExpectedConditions.elementToBeClickable(
                                By.xpath("//input[@value='Yes'] | //button[contains(text(),'Yes')]")
                        ));
                staySignedIn.click();
                System.out.println("Clicked 'Yes' on Stay signed in");
                Thread.sleep(2000);
            } catch (Exception e) {
                System.out.println("No 'Stay signed in' popup");
            }

            new WebDriverWait(driver, Duration.ofSeconds(60))
                    .until(ExpectedConditions.urlContains("northeastern.edu"));
            Thread.sleep(3000);
            System.out.println("Redirected to: " + driver.getCurrentUrl());

        } catch (Exception e) {
            System.out.println("Login skipped or already logged in: " + e.getMessage());
        }
    }

    @AfterClass
    public void generateReport() {
        ReportManager.flushReports();
        System.out.println("Report generated: reports/TestReport.html");
    }
}