package tests;

import base.BaseTest;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import org.openqa.selenium.*;
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

public class Scenario4Test extends BaseTest {

    private static final String SCENARIO = "scenario4_dataset_negative";
    private ExtentTest extentTest;

    @Test
    public void testDownloadDatasetNegative() {

        extentTest = ReportManager.createTest("Scenario 4 Dataset Negative");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

        Map<String, String> data = ExcelReader.getDatasetInfo();

        String baseUrl = data.get("base_url");
        String wrongExpectedText = data.get("expected_wrong_text");

        try {

            ScreenshotHelper.takeScreenshot(driver, SCENARIO, "step1_open_onesearch", "before");
            driver.get(baseUrl);
            wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));
            ScreenshotHelper.takeScreenshot(driver, SCENARIO, "step1_open_onesearch", "after");
            extentTest.log(Status.PASS, "Opened OneSearch");

            ScreenshotHelper.takeScreenshot(driver, SCENARIO, "step2_open_repository_home", "before");
            driver.get("https://repository.library.northeastern.edu/");
            wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));
            ScreenshotHelper.takeScreenshot(driver, SCENARIO, "step2_open_repository_home", "after");
            extentTest.log(Status.PASS, "Opened Repository Home");

            ScreenshotHelper.takeScreenshot(driver, SCENARIO, "step3_open_dataset_list", "before");
            driver.get("https://repository.library.northeastern.edu/communities");
            wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));
            ScreenshotHelper.takeScreenshot(driver, SCENARIO, "step3_open_dataset_list", "after");
            extentTest.log(Status.PASS, "Opened Communities Page");

            ScreenshotHelper.takeScreenshot(driver, SCENARIO, "step4_open_any_dataset", "before");

            List<WebElement> datasetLinks = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.xpath("//a[contains(@href,'handle')]")));

            Assert.assertTrue(datasetLinks.size() > 0, "No dataset items found");

            datasetLinks.get(0).click();

            wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));

            ScreenshotHelper.takeScreenshot(driver, SCENARIO, "step4_open_any_dataset", "after");
            extentTest.log(Status.PASS, "Opened one dataset");

            ScreenshotHelper.takeScreenshot(driver, SCENARIO, "step5_negative_assert", "before");

            Assert.assertTrue(driver.getPageSource().contains(wrongExpectedText),
                    "Negative scenario expected to fail");

            ScreenshotHelper.takeScreenshot(driver, SCENARIO, "step5_negative_assert", "after");

        } catch (AssertionError ae) {

            extentTest.log(Status.FAIL, "Scenario failed as expected: " + ae.getMessage());
            ScreenshotHelper.takeScreenshot(driver, SCENARIO, "failure", "after");
            throw ae;

        } catch (Exception e) {

            extentTest.log(Status.FAIL, "Execution failure: " + e.getMessage());
            ScreenshotHelper.takeScreenshot(driver, SCENARIO, "failure", "after");
            Assert.fail("Scenario failed: " + e.getMessage());
        }
    }

    @AfterClass
    public void generateReport() {
        ReportManager.flushReports();
    }
}