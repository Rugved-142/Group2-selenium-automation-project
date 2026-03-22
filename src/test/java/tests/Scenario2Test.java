package tests;

import base.BaseTest;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
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
import java.util.List;
import java.util.Map;

public class Scenario2Test extends BaseTest {

    private static final String SCENARIO = "scenario2_canvas_events";
    private ExtentTest extentTest;

    @Test
    public void testAddTwoCalendarEvents() {

        extentTest = ReportManager.createTest("Scenario 2: Add Two Event Tasks on Canvas Calendar");
        WebDriverWait  wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        JavascriptExecutor js = (JavascriptExecutor) driver;

        // All data from Excel — no hard-coded values
        Map<String, String>       creds  = ExcelReader.getLoginCredentials();
        List<Map<String, String>> events = ExcelReader.getCanvasEvents();

        Assert.assertEquals(events.size(), 2,
                "Excel 'CanvasEvents' sheet must have exactly 2 rows");

        try {

            // ── Step a: Navigate to canvas.northeastern.edu and log in ────────
            ScreenshotHelper.takeScreenshot(driver, SCENARIO, "a_navigate_canvas", "before");
            driver.get("https://canvas.northeastern.edu");
            wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));
            Thread.sleep(2000);
            ScreenshotHelper.takeScreenshot(driver, SCENARIO, "a_landing_page", "after");
            extentTest.log(Status.PASS, "Step a: Navigated to canvas.northeastern.edu");

            // Click "Log in to Canvas" — href from outerHTML
            ScreenshotHelper.takeScreenshot(driver, SCENARIO, "a_click_login_btn", "before");
            wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("a[href='https://northeastern.instructure.com/']"))).click();
            Thread.sleep(3000);
            ScreenshotHelper.takeScreenshot(driver, SCENARIO, "a_click_login_btn", "after");
            extentTest.log(Status.PASS, "Step a: Clicked Log in to Canvas");

            // Handle Microsoft SSO + Duo 2FA
            canvasLogin(wait, creds);

            wait.until(ExpectedConditions.urlContains("northeastern.instructure.com"));
            Thread.sleep(2000);
            System.out.println("  ✅ Step a: Logged in — " + driver.getCurrentUrl());
            ScreenshotHelper.takeScreenshot(driver, SCENARIO, "a_logged_in", "after");
            extentTest.log(Status.PASS, "Step a: Logged into Canvas successfully");

            // ── Step b: Open Calendar from left nav ──────────────────────────
            ScreenshotHelper.takeScreenshot(driver, SCENARIO, "b_open_calendar", "before");
            wait.until(ExpectedConditions.elementToBeClickable(
                    By.id("global_nav_calendar_link"))).click();
            wait.until(ExpectedConditions.urlContains("calendar"));
            Thread.sleep(2000);
            System.out.println("  ✅ Step b: Opened Canvas Calendar");
            ScreenshotHelper.takeScreenshot(driver, SCENARIO, "b_calendar_opened", "after");
            extentTest.log(Status.PASS, "Step b: Canvas Calendar opened");

            // ── Loop: create each event ───────────────────────────────────────
            // Order: Date → Start Time → End Time → Location → Title → Submit
            for (int i = 0; i < events.size(); i++) {

                Map<String, String> event    = events.get(i);
                int                 eventNum = i + 1;

                String title    = event.get("title");
                String date     = event.get("date");      // "30 March 2026"
                String time     = event.get("time");      // "14:00"
                String endTime  = event.get("end_time");  // "15:00"
                String location = event.get("location");

                // Null guard — fail with clear message if Excel values missing
                if (title    == null) throw new Exception("'title' is null in Excel row " + eventNum);
                if (date     == null) throw new Exception("'date' is null in Excel row " + eventNum);
                if (time     == null) throw new Exception("'time' is null in Excel row " + eventNum);
                if (endTime  == null) throw new Exception("'end_time' is null in Excel row " + eventNum);
                if (location == null) throw new Exception("'location' is null in Excel row " + eventNum);

                System.out.println("\n  ── Creating Event " + eventNum + ": " + title + " ──");
                extentTest.log(Status.INFO, "Event " + eventNum + " — title: " + title
                        + " | date: " + date + " | start: " + time
                        + " | end: " + endTime + " | location: " + location);

                // ── Step c: Click + Create New Event ─────────────────────────
                ScreenshotHelper.takeScreenshot(driver, SCENARIO,
                        "c_click_create_" + eventNum, "before");
                wait.until(ExpectedConditions.elementToBeClickable(
                        By.id("create_new_event_link"))).click();
                wait.until(ExpectedConditions.visibilityOfElementLocated(
                        By.id("edit_event_tabs")));
                Thread.sleep(1000);
                System.out.println("  ✅ Step c: Edit Event dialog opened");
                ScreenshotHelper.takeScreenshot(driver, SCENARIO,
                        "c_dialog_opened_" + eventNum, "after");
                extentTest.log(Status.PASS, "Step c: Dialog opened for Event " + eventNum);

                // ── Step d: Ensure Event tab is active ────────────────────────
                wait.until(ExpectedConditions.elementToBeClickable(
                        By.cssSelector("a[href='#edit_calendar_event_form_holder']"))).click();
                Thread.sleep(500);
                extentTest.log(Status.PASS, "Step d: Event tab active");

                // ── Step e: Select Date via calendar picker ───────────────────
                // date from Excel is already "30 March 2026" — matches picker format
                ScreenshotHelper.takeScreenshot(driver, SCENARIO,
                        "e_date_" + eventNum, "before");
                String pickerDate = date.trim();
                System.out.println("  📅 Picker date: " + pickerDate);

                // Open date picker by clicking "Choose a date" button
                wait.until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//button[.//span[text()='Choose a date']]"))).click();
                Thread.sleep(1000);

                // Click the exact day button
                wait.until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//button[.//span[contains(@class,'screenReaderContent')" +
                                " and normalize-space(text())='" + pickerDate + "']]"))).click();
                Thread.sleep(1000);

                System.out.println("  ✅ Step e: Date selected — " + pickerDate);
                ScreenshotHelper.takeScreenshot(driver, SCENARIO,
                        "e_date_" + eventNum, "after");
                extentTest.log(Status.PASS, "Step e: Date selected — " + pickerDate);

                // ── Step f: Enter Start Time ──────────────────────────────────
                // Type the time to filter options, then use arrow keys + Enter
                // to select without triggering form submission
                ScreenshotHelper.takeScreenshot(driver, SCENARIO,
                        "f_start_time_" + eventNum, "before");

                WebElement startField = wait.until(ExpectedConditions.elementToBeClickable(
                        By.cssSelector("[data-testid='event-form-start-time']")));
                startField.click();
                Thread.sleep(500);
                startField.sendKeys(time);
                Thread.sleep(800);
                // Arrow down to highlight the first matching option, then Enter to select
                startField.sendKeys(Keys.ARROW_DOWN);
                Thread.sleep(300);
                startField.sendKeys(Keys.ENTER);
                Thread.sleep(800);

                System.out.println("  ✅ Step f: Start time entered — " + time);
                ScreenshotHelper.takeScreenshot(driver, SCENARIO,
                        "f_start_time_" + eventNum, "after");
                extentTest.log(Status.PASS, "Step f: Start time entered — " + time);

                // ── Step g: Clear default end time and enter our end time ──────
                // After start time is set, Canvas auto-fills end time to 23:59
                // We must clear it and enter our value from Excel
                ScreenshotHelper.takeScreenshot(driver, SCENARIO,
                        "g_end_time_" + eventNum, "before");
                Thread.sleep(500);

                WebElement endField = wait.until(ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector("[data-testid='event-form-end-time']")));

                Thread.sleep(500);
                js.executeScript("arguments[0].click();", endField);    // field.click()
                Thread.sleep(300);
                js.executeScript("arguments[0].select();", endField);   // field.select()
                Thread.sleep(300);
                js.executeScript("document.execCommand('delete');");    // execCommand('delete') → true, value = ""
                Thread.sleep(300);
                endField.sendKeys(endTime);                             // type "15:00"
                Thread.sleep(300);
                endField.sendKeys(Keys.ARROW_DOWN);                     // highlight option
                Thread.sleep(300);
                endField.sendKeys(Keys.ENTER);

                System.out.println("  ✅ Step g: End time entered — " + endTime);
                ScreenshotHelper.takeScreenshot(driver, SCENARIO,
                        "g_end_time_" + eventNum, "after");
                extentTest.log(Status.PASS, "Step g: End time entered — " + endTime);

                // ── Step h: Enter Location ────────────────────────────────────
                ScreenshotHelper.takeScreenshot(driver, SCENARIO,
                        "h_location_" + eventNum, "before");
                WebElement locationField = wait.until(ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector("[data-testid='edit-calendar-event-form-location']")));
                locationField.clear();
                locationField.sendKeys(location);
                Thread.sleep(500);
                System.out.println("  ✅ Step h: Location entered — " + location);
                ScreenshotHelper.takeScreenshot(driver, SCENARIO,
                        "h_location_" + eventNum, "after");
                extentTest.log(Status.PASS, "Step h: Location entered — " + location);

                // ── Step i: Enter Title LAST ──────────────────────────────────
                // Title is entered last so Submit button becomes enabled only now
                ScreenshotHelper.takeScreenshot(driver, SCENARIO,
                        "i_title_" + eventNum, "before");
                WebElement titleField = wait.until(ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector("[data-testid='edit-calendar-event-form-title']")));
                titleField.clear();
                titleField.sendKeys(title);
                Thread.sleep(500);
                System.out.println("  ✅ Step i: Title entered — " + title);
                ScreenshotHelper.takeScreenshot(driver, SCENARIO,
                        "i_title_" + eventNum, "after");
                extentTest.log(Status.PASS, "Step i: Title entered — " + title);

                // ── Step j: Click Submit ──────────────────────────────────────
                // id="edit-calendar-event-submit-button" — enabled once Title is filled
                ScreenshotHelper.takeScreenshot(driver, SCENARIO,
                        "j_submit_" + eventNum, "before");
                WebElement submitBtn = wait.until(ExpectedConditions.elementToBeClickable(
                        By.id("edit-calendar-event-submit-button")));
                submitBtn.click();

                // Dialog closing confirms Canvas saved the event
                wait.until(ExpectedConditions.invisibilityOfElementLocated(
                        By.id("edit_event_tabs")));
                Thread.sleep(1500);
                System.out.println("  ✅ Step j: Event " + eventNum + " submitted — " + title);
                ScreenshotHelper.takeScreenshot(driver, SCENARIO,
                        "j_submitted_" + eventNum, "after");
                extentTest.log(Status.PASS,
                        "Step j: Event " + eventNum + " submitted — " + title);

                // ── Step k: Assert event visible on calendar grid ─────────────
                ScreenshotHelper.takeScreenshot(driver, SCENARIO,
                        "k_verify_" + eventNum, "before");
                Thread.sleep(1000);

                boolean eventVisible = !driver.findElements(
                                By.xpath("//*[contains(@class,'event') and contains(.,'" + title + "')]"))
                        .isEmpty();

                Assert.assertTrue(eventVisible,
                        "FAIL: Event '" + title + "' NOT visible on calendar after submission");

                System.out.println("  ✅ Step k: Event " + eventNum + " visible on calendar — PASSED");
                ScreenshotHelper.takeScreenshot(driver, SCENARIO,
                        "k_verified_" + eventNum, "after");
                extentTest.log(Status.PASS,
                        "Step k: Event " + eventNum + " '" + title + "' visible on calendar");

            } // end loop

            extentTest.log(Status.PASS,
                    "Scenario 2 PASSED: Both events created and verified on Canvas Calendar");
            System.out.println("\n  🎉 Scenario 2 PASSED: Both events added to Canvas Calendar");

        } catch (Exception e) {
            extentTest.log(Status.FAIL, "Scenario 2 FAILED: " + e.getMessage());
            ScreenshotHelper.takeScreenshot(driver, SCENARIO, "failure", "after");
            Assert.fail("Scenario 2 failed: " + e.getMessage());
        }
    }

    // ── Microsoft SSO + Duo 2FA login helper ─────────────────────────────────
    private void canvasLogin(WebDriverWait wait, Map<String, String> creds) {
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(
                            By.xpath("//input[@type='email' or @name='loginfmt' or @id='username']")))
                    .sendKeys(creds.get("username"));
            wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//input[@type='submit'] | //button[@type='submit']"))).click();
            Thread.sleep(2000);

            wait.until(ExpectedConditions.presenceOfElementLocated(
                            By.xpath("//input[@type='password' or @name='passwd']")))
                    .sendKeys(creds.get("password"));
            wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//input[@type='submit'] | //button[@type='submit']"))).click();

            System.out.println("\n=====================================================");
            System.out.println("  ⚠️  DUO 2FA: Please approve the push on your phone.");
            System.out.println("      Waiting up to 60 s for browser to redirect...");
            System.out.println("=====================================================\n");

            try {
                new WebDriverWait(driver, Duration.ofSeconds(60))
                        .until(ExpectedConditions.elementToBeClickable(By.xpath(
                                "//button[contains(text(),'Yes, this is my device')] | "
                                        + "//a[contains(text(),'Yes, this is my device')]"))).click();
                System.out.println("  ✅ Clicked 'Yes, this is my device'");
                Thread.sleep(2000);
            } catch (Exception e) {
                System.out.println("  ℹ️ No 'Is this your device' popup");
            }

            try {
                new WebDriverWait(driver, Duration.ofSeconds(15))
                        .until(ExpectedConditions.elementToBeClickable(By.xpath(
                                "//input[@value='Yes'] | //button[contains(text(),'Yes')]"))).click();
                System.out.println("  ✅ Clicked 'Yes' on Stay signed in");
                Thread.sleep(2000);
            } catch (Exception e) {
                System.out.println("  ℹ️ No 'Stay signed in' popup");
            }

            new WebDriverWait(driver, Duration.ofSeconds(60))
                    .until(ExpectedConditions.urlContains("northeastern.instructure.com"));
            Thread.sleep(3000);
            System.out.println("  ✅ Redirected to Canvas: " + driver.getCurrentUrl());

        } catch (Exception e) {
            System.out.println("  ℹ️ canvasLogin finished: " + e.getMessage());
        }
    }

    @AfterClass
    public void generateReport() {
        ReportManager.flushReports();
        System.out.println("  📊 Report generated: reports/TestReport.html");
    }
}