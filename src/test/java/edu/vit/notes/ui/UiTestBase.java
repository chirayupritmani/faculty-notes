package edu.vit.notes.ui;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Base class for the Selenium journeys. Starts the application on a random port, opens Chrome
 * (headless by default; add -Dheadless=false to watch the browser) and provides small helpers.
 * Test data: demo accounts from UserDataLoader and sample notes from DataLoader.
 */
@Tag("selenium")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class UiTestBase {

    @LocalServerPort
    protected int port;

    protected WebDriver driver;
    protected WebDriverWait wait;

    @RegisterExtension
    final ScreenshotOnFailure screenshots = new ScreenshotOnFailure(() -> driver);

    @BeforeEach
    void startBrowser() {
        ChromeOptions options = new ChromeOptions();
        if (!"false".equalsIgnoreCase(System.getProperty("headless"))) {
            options.addArguments("--headless=new");
        }
        options.addArguments("--window-size=1280,900", "--no-sandbox", "--disable-dev-shm-usage",
                "--disable-notifications", "--disable-infobars",
                "--disable-features=PasswordLeakDetection,PasswordManagerOnboarding,AutofillServerCommunication");
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("credentials_enable_service", false);
        prefs.put("profile.password_manager_enabled", false);
        prefs.put("profile.password_manager_leak_detection", false);
        options.setExperimentalOption("prefs", prefs);
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterEach
    void stopBrowser() {
        if (driver != null) {
            driver.quit();
        }
    }

    protected String url(String path) {
        return "http://localhost:" + port + path;
    }

    /** Signs in through the login page and waits until the server has answered. */
    protected void login(String username, String password) {
        driver.get(url("/login"));
        type(By.id("username"), username);
        type(By.id("password"), password);
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        wait.until(d -> d.getCurrentUrl().contains("error") || !d.getCurrentUrl().contains("/login"));
    }

    /** Ends the session without the logout page, so the next login starts clean. */
    protected void signOut() {
        driver.manage().deleteAllCookies();
    }

    /** Types into a field, checks the value really arrived, and sets it by script if it did not. */
    protected void type(By locator, String text) {
        WebElement field = wait.until(ExpectedConditions.elementToBeClickable(locator));
        field.clear();
        field.sendKeys(text);
        if (!text.equals(field.getDomProperty("value"))) {
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].value = arguments[1];"
                    + "arguments[0].dispatchEvent(new Event('input', {bubbles: true}));", field, text);
        }
        if (!text.equals(field.getDomProperty("value"))) {
            throw new AssertionError("Could not type into " + locator);
        }
    }

    protected String textOf(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator)).getText();
    }

    protected String bodyText() {
        return driver.findElement(By.tagName("body")).getText();
    }

    /** Faculty creates a draft with a unique title and returns that title. */
    protected String createDraft(String prefix) {
        String title = prefix + " " + System.currentTimeMillis();
        driver.get(url("/notes/new"));
        type(By.id("title"), title);
        type(By.id("subject"), "Software Testing");
        type(By.id("content"), "Automated test data for the Selenium suite.");
        driver.findElement(By.id("save-button")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("notes-table")));
        return title;
    }

    protected void openNote(String title) {
        driver.get(url("/notes"));
        wait.until(ExpectedConditions.elementToBeClickable(By.linkText(title))).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("note-title")));
    }
}
