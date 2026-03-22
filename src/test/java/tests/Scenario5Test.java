package tests;

import org.openqa.selenium.WebDriver;
import base.BaseTest;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
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
            ScreenshotHelper.takeScreenshot(driver, SCENARIO, "a_click_resources", "before");
            WebElement resourcesTab = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[self::a or self::span or self::button or self::li]" +
                            "[contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ'," +
                            "'abcdefghijklmnopqrstuvwxyz'),'resources')]")
            ));
            resourcesTab.click();
            System.out.println("  ✅ Step a: Clicked Resources tab");
            ScreenshotHelper.takeScreenshot(driver, SCENARIO, "a_click_resources", "after");
            extentTest.log(Status.PASS, "Step a: Clicked Resources tab");

            // ── Step b: Click Academics, Classes & Registration ───────────────
            ScreenshotHelper.takeScreenshot(driver, SCENARIO, "b_click_academics", "before");
            WebElement academics = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[contains(text(),'Academics, Classes')]")
            ));
            academics.click();
            Thread.sleep(1000);
            System.out.println("  ✅ Step b: Clicked Academics");
            ScreenshotHelper.takeScreenshot(driver, SCENARIO, "b_click_academics", "after");
            extentTest.log(Status.PASS, "Step b: Clicked Academics, Classes & Registration");

            // ── Step c: Click Academic Calendar link ──────────────────────────
            ScreenshotHelper.takeScreenshot(driver, SCENARIO, "c_click_calendar_link", "before");
            WebElement calLink = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//a[normalize-space(text())='Academic Calendar']")
            ));
            calLink.click();
            Thread.sleep(2000);
            System.out.println("  ✅ Step c: Clicked Academic Calendar link");
            ScreenshotHelper.takeScreenshot(driver, SCENARIO, "c_click_calendar_link", "after");
            extentTest.log(Status.PASS, "Step c: Clicked Academic Calendar link");

            // ── Step d: Switch to new tab, click Academic Calendar ────────────
            if (driver.getWindowHandles().size() > 1) {
                driver.switchTo().window(
                        driver.getWindowHandles().toArray()[driver.getWindowHandles().size() - 1].toString()
                );
                System.out.println("  ✅ Step d: Switched to new tab");
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
                System.out.println("  ℹ️ Navigating directly to calendar URL...");
                driver.get("https://registrar.northeastern.edu/article/academic-calendar/");
                Thread.sleep(2000);
            }
            System.out.println("  ✅ Step d: On Academic Calendar page");
            ScreenshotHelper.takeScreenshot(driver, SCENARIO, "d_registrar_page", "after");
            extentTest.log(Status.PASS, "Step d: Navigated to Academic Calendar page");

            // ── Step e: Scroll down to view calendars ─────────────────────────
            ScreenshotHelper.takeScreenshot(driver, SCENARIO, "e_scroll_calendars", "before");
            js.executeScript("window.scrollBy(0, 600);");
            Thread.sleep(1000);
            System.out.println("  ✅ Step e: Scrolled down");
            ScreenshotHelper.takeScreenshot(driver, SCENARIO, "e_scroll_calendars", "after");
            extentTest.log(Status.PASS, "Step e: Scrolled down to calendar section");

            // ── Step f: Uncheck QTR checkbox ──────────────────────────────────
            ScreenshotHelper.takeScreenshot(driver, SCENARIO, "f_uncheck_qtr", "before");
            js.executeScript("window.scrollBy(0, 1200);");
            Thread.sleep(3000);

            // Use JS to find and click QTR link across all iframes
            String result = (String) js.executeScript(
                    "function searchFrames(win) {" +
                            "  try {" +
                            "    var doc = win.document;" +
                            "    var links = doc.querySelectorAll('a.twCalendarListName');" +
                            "    for (var i = 0; i < links.length; i++) {" +
                            "      var t = links[i].innerText || links[i].textContent || '';" +
                            "      if (t.indexOf('QTR') > -1 || t.indexOf('Quarter') > -1) {" +
                            "        var parent = links[i].closest('li, tr, div');" +
                            "        var cb = parent ? parent.querySelector('input[type=checkbox]') : null;" +
                            "        if (cb) { cb.click(); return 'clicked checkbox: ' + t.trim(); }" +
                            "        links[i].click();" +
                            "        return 'clicked link: ' + t.trim();" +
                            "      }" +
                            "    }" +
                            "  } catch(e) {}" +
                            "  try {" +
                            "    for (var f = 0; f < win.frames.length; f++) {" +
                            "      var r = searchFrames(win.frames[f]);" +
                            "      if (r) return r;" +
                            "    }" +
                            "  } catch(e) {}" +
                            "  return null;" +
                            "}" +
                            "return searchFrames(window) || 'not found';"
            );

            System.out.println("  🔍 JS result: " + result);
            if ("not found".equals(result)) {
                throw new Exception("❌ QTR checkbox not found");
            }
            System.out.println("  ✅ Step f: Unchecked QTR — " + result);
            Thread.sleep(1000);
            ScreenshotHelper.takeScreenshot(driver, SCENARIO, "f_uncheck_qtr", "after");
            extentTest.log(Status.PASS, "Step f: Unchecked QTR checkbox — " + result);

            // ── Step g: Scroll to bottom, verify Add to My Calendar button ────
            ScreenshotHelper.takeScreenshot(driver, SCENARIO, "g_verify_button", "before");
            js.executeScript("window.scrollTo(0, document.body.scrollHeight);");
            Thread.sleep(1000);

            String expectedButton = data.get("expected_button");

            // Switch into iframe 2 for Add to My Calendar button
            WebElement iframe = driver.findElements(By.tagName("iframe")).get(2);
            driver.switchTo().frame(iframe);

            WebElement addBtn = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//*[contains(text(),'" + expectedButton + "')]")
            ));

            // Assert button is displayed
            Assert.assertTrue(addBtn.isDisplayed(),
                    "FAIL: '" + expectedButton + "' button was NOT visible");

            driver.switchTo().defaultContent();
            System.out.println("  ✅ Step g: '" + expectedButton + "' button visible — PASSED");
            ScreenshotHelper.takeScreenshot(driver, SCENARIO, "g_verify_button", "after");
            extentTest.log(Status.PASS, "Step g: '" + expectedButton + "' button is visible");

        } catch (Exception e) {
            extentTest.log(Status.FAIL, "Scenario 5 FAILED: " + e.getMessage());
            ScreenshotHelper.takeScreenshot(driver, SCENARIO, "failure", "after");
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
            System.out.println("  ⚠️  DUO 2FA: Please approve push on your phone.");
            System.out.println("      Waiting for browser to redirect...");
            System.out.println("=====================================================\n");

            // Handle Duo "Is this your device?" popup
            try {
                WebElement myDeviceBtn = new WebDriverWait(driver, Duration.ofSeconds(60))
                        .until(ExpectedConditions.elementToBeClickable(
                                By.xpath("//button[contains(text(),'Yes, this is my device')] | " +
                                        "//a[contains(text(),'Yes, this is my device')]")
                        ));
                myDeviceBtn.click();
                System.out.println("  ✅ Clicked 'Yes, this is my device'");
                Thread.sleep(2000);
            } catch (Exception e) {
                System.out.println("  ℹ️ No 'Is this your device' popup");
            }

            // Handle "Stay signed in?" popup
            try {
                WebElement staySignedIn = new WebDriverWait(driver, Duration.ofSeconds(15))
                        .until(ExpectedConditions.elementToBeClickable(
                                By.xpath("//input[@value='Yes'] | //button[contains(text(),'Yes')]")
                        ));
                staySignedIn.click();
                System.out.println("  ✅ Clicked 'Yes' on Stay signed in");
                Thread.sleep(2000);
            } catch (Exception e) {
                System.out.println("  ℹ️ No 'Stay signed in' popup");
            }

            // Wait for redirect back to student hub
            new WebDriverWait(driver, Duration.ofSeconds(60))
                    .until(ExpectedConditions.urlContains("northeastern.edu"));
            Thread.sleep(3000);
            System.out.println("  ✅ Redirected to: " + driver.getCurrentUrl());

        } catch (Exception e) {
            System.out.println("  ℹ️ Login skipped or already logged in: " + e.getMessage());
        }
    }

    @AfterClass
    public void generateReport() {
        ReportManager.flushReports();
        System.out.println("  📊 Report generated: reports/TestReport.html");
    }
}