package utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

public class ReportManager {

    private static ExtentReports extent;
    private static ExtentTest test;

    public static ExtentReports getInstance() {
        if (extent == null) {
            ExtentSparkReporter spark = new ExtentSparkReporter("reports/TestReport.html");
            spark.config().setReportName("INFO6255 Selenium Test Report");
            spark.config().setDocumentTitle("Test Results");
            extent = new ExtentReports();
            extent.attachReporter(spark);
            extent.setSystemInfo("Project", "INFO6255 Selenium Assignment");
            extent.setSystemInfo("Tester", "Antara Deshmukh");
        }
        return extent;
    }

    public static ExtentTest createTest(String testName) {
        test = getInstance().createTest(testName);
        return test;
    }

    public static void flushReports() {
        if (extent != null) {
            extent.flush();
        }
    }
}