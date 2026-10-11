package edu.vit.notes.ui;

import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Supplier;

/**
 * Failure screenshot mechanism: when a test fails, the current browser window is saved as a PNG
 * in target/screenshots (file name = class, method and time). Runs before the browser is closed.
 */
public class ScreenshotOnFailure implements AfterTestExecutionCallback {

    private final Supplier<WebDriver> driver;

    public ScreenshotOnFailure(Supplier<WebDriver> driver) {
        this.driver = driver;
    }

    @Override
    public void afterTestExecution(ExtensionContext context) throws Exception {
        if (context.getExecutionException().isEmpty()) {
            return;
        }
        WebDriver d = driver.get();
        if (!(d instanceof TakesScreenshot shooter)) {
            return;
        }
        Path folder = Path.of("target", "screenshots");
        Files.createDirectories(folder);
        String stamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
        String name = context.getRequiredTestClass().getSimpleName() + "_"
                + context.getRequiredTestMethod().getName() + "_" + stamp + ".png";
        Path target = folder.resolve(name);
        Files.copy(shooter.getScreenshotAs(OutputType.FILE).toPath(), target, StandardCopyOption.REPLACE_EXISTING);
        System.out.println("FAILURE SCREENSHOT SAVED: " + target.toAbsolutePath());
    }
}
