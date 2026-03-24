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

public class Scenario1Test extends BaseTest {

    private static final String SCENARIO = "scenario1_transcript";
    private ExtentTest extentTest;

    @Test
    public void testUpdateAcademicCalendar() {
        extentTest = ReportManager.createTest("Scenario 1: Download Transcript");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
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
            System.out.println("Current URL: " + currentUrl);
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
            ScreenshotHelper.takeScreenshot(driver, SCENARIO, "transcript_link", "before");
            WebElement unofficialTranscriptLink = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//a[normalize-space(text())='Unofficial Transcript']")
            ));
            unofficialTranscriptLink.click();
            Thread.sleep(2000);
            System.out.println("Step c: Clicked Transcript link");
            ScreenshotHelper.takeScreenshot(driver, SCENARIO, "transcript_link", "after");
            extentTest.log(Status.PASS, "Step c: Clicked Unofficial Transcript link");


            // ── Step d: Verify transcript page actually loaded ───────────────
            ScreenshotHelper.takeScreenshot(driver, SCENARIO, "transcript_page_load", "before");
            // Fail the test if the transcript page never loads (link broken/down)
            wait.until(
                ExpectedConditions.or(
                    ExpectedConditions.titleContains("Transcript"),
                    ExpectedConditions.urlContains("transcript")
                )
            );
            ScreenshotHelper.takeScreenshot(driver, SCENARIO, "transcript_page_load", "after");
            extentTest.log(Status.PASS, "Step d: Transcript page loaded successfully");

            // Handle NEU login if redirected
            Thread.sleep(3000);
            String loginURL = driver.getCurrentUrl();
            System.out.println("Current URL: " + loginURL);
            if (loginURL.contains("login") || loginURL.contains("microsoft") ||
                    loginURL.contains("microsoftonline") || loginURL.contains("auth")) {
                transcriptLogin(driver, wait, creds);
            }

                

        } catch (Exception e) {
            extentTest.log(Status.FAIL, "Scenario 1 FAILED: " + e.getMessage());
            ScreenshotHelper.takeScreenshot(driver, SCENARIO, "failure", "after");
            Assert.fail("Scenario 1 failed: " + e.getMessage());
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

            // Handle Duo "Is this your device?" popup
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

            // Handle "Stay signed in?" popup
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

            // Wait for redirect back to student hub
            new WebDriverWait(driver, Duration.ofSeconds(60))
                    .until(ExpectedConditions.urlContains("northeastern.edu"));
            Thread.sleep(3000);
            System.out.println("Redirected to: " + driver.getCurrentUrl());

        } catch (Exception e) {
            System.out.println("Login skipped or already logged in: " + e.getMessage());
        }
    }

    // ── Transcript Login Helper ──────────────────────────────────────────────────────
    private void transcriptLogin(WebDriver driver, WebDriverWait wait, Map<String, String> creds) {
        try {
            WebElement usernameField = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//input[@type='email' or @name='loginfmt' or @id='username']")
            ));
            usernameField.clear();
            String username = creds.get("username");
            if (username != null && username.contains("@")) {
                username = username.substring(0, username.indexOf("@"));
            }
            usernameField.sendKeys(username);

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

            // Handle Duo "Is this your device?" popup
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

            // Handle "Stay signed in?" popup
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

            // Wait for redirect back to student hub
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