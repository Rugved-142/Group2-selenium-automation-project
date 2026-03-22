package utils;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class ScreenshotHelper {

    public static void takeScreenshot(WebDriver driver, String scenarioName, String step, String timing) {
        try {
            String folder = "screenshots/" + scenarioName;
            Files.createDirectories(Paths.get(folder));

            String ts = LocalTime.now().format(DateTimeFormatter.ofPattern("HHmmss"));
            String filename = timing + "_" + ts + "_" + step + ".png";
            Path dest = Paths.get(folder, filename);

            File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Files.copy(src.toPath(), dest);
            System.out.println("  📸 Screenshot: " + dest);

        } catch (IOException e) {
            System.out.println("  ⚠️ Screenshot failed: " + e.getMessage());
        }
    }
}