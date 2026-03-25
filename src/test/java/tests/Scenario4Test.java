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

            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
            Thread.sleep(2000);

            ScreenshotHelper.takeScreenshot(driver, SCENARIO, "step2_click_repository", "before");

            boolean repositoryClicked = false;
            String originalWindow = driver.getWindowHandle();
            try {

                List<WebElement> links = driver.findElements(By.tagName("a"));

                for (WebElement link : links) {
                    String text = link.getText();
                    if (text != null && text.toLowerCase().contains("repository")) {
                        js.executeScript("arguments[0].scrollIntoView(true);", link);
                        Thread.sleep(1000);
                        link.click();
                        repositoryClicked = true;
                        break;
                    }
                }

            } catch (Exception ignored) {
            }

            if (!repositoryClicked) {
                driver.get("https://repository.library.northeastern.edu/");
            }
            Thread.sleep(2000);
            wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));
            ScreenshotHelper.takeScreenshot(driver, SCENARIO, "step2_click_repository", "after");
            extentTest.log(Status.PASS, "Repository page opened");

            // Switch to the newly opened tab so currentUrl reflects the visible page
            for (String handle : driver.getWindowHandles()) {
                if (!handle.equals(originalWindow)) {
                    driver.switchTo().window(handle);
                    break;
                }
            }

            js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
            Thread.sleep(2000);

            ScreenshotHelper.takeScreenshot(driver, SCENARIO, "step3_click_dataset_section", "before");

            boolean datasetSectionClicked = false;

            try {

                List<WebElement> links = driver.findElements(By.tagName("a"));

                for (WebElement link : links) {
                    String text = link.getText();
                    if (text != null && text.toLowerCase().contains("dataset")) {
                        js.executeScript("arguments[0].scrollIntoView(true);", link);
                        Thread.sleep(1000);
                        link.click();
                        datasetSectionClicked = true;
                        break;
                    }
                }

            } catch (Exception ignored) {
            }

            if (!datasetSectionClicked) {
                driver.get("https://repository.library.northeastern.edu/communities");
            }
            Thread.sleep(2000);
            wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));
            ScreenshotHelper.takeScreenshot(driver, SCENARIO, "step3_click_dataset_section", "after");
            extentTest.log(Status.PASS, "Dataset section opened");

            ScreenshotHelper.takeScreenshot(driver, SCENARIO, "step4_open_dataset_item", "before");

            List<WebElement> datasetLinks = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.xpath("//a[contains(@href,'handle')]")));

            Assert.assertTrue(datasetLinks.size() > 0, "No dataset items found");

            datasetLinks.get(0).click();
            Thread.sleep(2000);
            wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));

            ScreenshotHelper.takeScreenshot(driver, SCENARIO, "step4_open_dataset_item", "after");
            extentTest.log(Status.PASS, "Opened dataset item");

            ScreenshotHelper.takeScreenshot(driver, SCENARIO, "step5_negative_assert", "before");

            Assert.assertTrue(driver.getPageSource().contains(wrongExpectedText),
                    "Negative scenario expected to fail");
            Thread.sleep(2000);
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