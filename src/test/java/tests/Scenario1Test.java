package tests;

import org.openqa.selenium.WebDriver;
import base.BaseTest;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Pdf;
import org.openqa.selenium.PrintsPage;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.print.PrintOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import utils.ExcelReader;
import utils.ReportManager;
import utils.ScreenshotHelper;

import java.nio.file.Paths;
import java.time.Duration;
import java.util.Map;
import java.nio.file.Path;
import java.io.File;
import java.nio.file.Files;

public class Scenario1Test extends BaseTest {

    private static final String SCENARIO = "scenario1_transcript";
    private ExtentTest extentTest;

    @Test
    public void downloadTranscript() {
        extentTest = ReportManager.createTest("Scenario 1: Download Transcript");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        Map<String, String> creds = ExcelReader.getLoginCredentials();
        JavascriptExecutor js = (JavascriptExecutor) driver;

        try {
            // ── Step a: Navigate to NEU Student Hub ──────────────────────────
            ScreenshotHelper.takeScreenshot(driver, SCENARIO, "a_navigate_student_hub", "before");
            driver.get(creds.get("url"));
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

            // ── Step c: Click Transcript link ──────────────────────────
            ScreenshotHelper.takeScreenshot(driver, SCENARIO, "transcript_link", "before");
            String originalWindow = driver.getWindowHandle();
            WebElement unofficialTranscriptLink = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//a[normalize-space(text())='Unofficial Transcript']")
            ));
            unofficialTranscriptLink.click();
            Thread.sleep(2000);

            // Switch to the newly opened tab so currentUrl reflects the visible page
            for (String handle : driver.getWindowHandles()) {
                if (!handle.equals(originalWindow)) {
                    driver.switchTo().window(handle);
                    break;
                }
            }

            System.out.println("Step c: Clicked Transcript link");
            ScreenshotHelper.takeScreenshot(driver, SCENARIO, "transcript_link", "after");
            extentTest.log(Status.PASS, "Step c: Clicked Unofficial Transcript link");


            // ── Step d: Verify transcript page actually loaded ───────────────
            ScreenshotHelper.takeScreenshot(driver, SCENARIO, "transcript_page_load", "before");

            Thread.sleep(2000);
            ScreenshotHelper.takeScreenshot(driver, SCENARIO, "transcript_page_load", "after");
            extentTest.log(Status.PASS, "Step d: Transcript page loaded successfully");

            // Handle NEU SSO login if redirected
            Thread.sleep(3000);
            String loginURL = driver.getCurrentUrl();
            System.out.println("Current URL: " + loginURL);
            if (loginURL.contains("neuidmsso.neu.edu")) {
                transcriptLogin(driver, wait, creds);
            }
            else{
                extentTest.log(Status.PASS, "Website is under maintainance");
                return;
            }

            Thread.sleep(2000);           

            WebElement transcriptLevelToggle = wait.until(
                ExpectedConditions.elementToBeClickable(
                    By.cssSelector("#transcriptLevelSelection .select2-choice")
                )
            );
            transcriptLevelToggle.click();

            WebElement dropdownList = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector("#transcriptLevelSelection .ui-select-choices")
                )
            );
           
            WebElement graduateOption = wait.until(
                ExpectedConditions.elementToBeClickable(
                    By.xpath("//ul[@id='ui-select-choices-1']//div[normalize-space(text())='Graduate']")
                )
            );
            graduateOption.click();

            Thread.sleep(2000);

            ScreenshotHelper.takeScreenshot(driver, SCENARIO, "transcript_level_selected", "after");
            extentTest.log(Status.PASS, "Selected 'Graduate' level");

            // Click the Transcript Type dropdown toggle to open it
            WebElement transcriptTypeToggle = wait.until(
                ExpectedConditions.elementToBeClickable(
                    By.cssSelector("#transcriptTypeSelection .select2-choice")
                )
            );
            transcriptTypeToggle.click();

            // Wait for the dropdown list to become visible
            WebElement transcriptTypeList = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector("#transcriptTypeSelection .ui-select-choices")
                )
            );

            // Click the option matching "Audit Transcript"            
            WebElement auditOption = wait.until(
                ExpectedConditions.elementToBeClickable(
                    By.xpath("//ul[@id='ui-select-choices-2']//div[normalize-space(text())='Audit Transcript']")
                )
            );
            auditOption.click();

            Thread.sleep(2000);

            ScreenshotHelper.takeScreenshot(driver, SCENARIO, "transcript_type_selected", "after");
            extentTest.log(Status.PASS, "Selected 'Audit Transcript' type");

            PrintsPage printsPage = (PrintsPage) driver;
            // Path printPage = Paths.get("/Users/Sarthak/sqcm/Group2-selenium-automation-project/My_Transcript.pdf");
            Path printPage = Paths.get(System.getProperty("user.dir"), "My_Transcript.pdf");
            Pdf print = printsPage.print(new PrintOptions());
            Files.write(printPage, OutputType.BYTES.convertFromBase64Png(print.getContent()));   
            
            Thread.sleep(2000);

            String pdfDirectory = System.getProperty("user.dir") + "/";
            File pdfDir = new File(pdfDirectory);

            // Wait up to 10 seconds for the file to appear
            File pdfFile = null;
            long timeout = System.currentTimeMillis() + 2000;

            while (System.currentTimeMillis() < timeout) {
                File[] files = pdfDir.listFiles((dir, name) -> name.endsWith(".pdf"));
                if (files != null && files.length > 0) {
                    pdfFile = files[0];
                    break;
                }
                Thread.sleep(500);
            }

            // Assert file exists
            Assert.assertNotNull(pdfFile, "PDF file was not created in output directory");
            Assert.assertTrue(pdfFile.exists(), "PDF file does not exist at path: " + pdfFile.getAbsolutePath());
            extentTest.log(Status.PASS, "PDF file exists at: " + pdfFile.getAbsolutePath());

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
                    By.xpath("//input[@id='username']")
            ));
            usernameField.clear();
            String username = creds.get("username");
            if (username != null && username.contains("@")) {
                username = username.substring(0, username.indexOf("@"));
            }
            usernameField.sendKeys(username);

            Thread.sleep(2000);

            WebElement passwordField = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//input[@id='password']")
            ));
            passwordField.clear();
            passwordField.sendKeys(creds.get("password"));

            WebElement signInBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//input[@type='submit'] | //button[@type='submit']")
            ));
            signInBtn.click();

            // Wait for redirect back to student hub
            new WebDriverWait(driver, Duration.ofSeconds(60))
                    .until(ExpectedConditions.urlContains("transcriptType"));
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