package edu.vit.notes.ui;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Journey 1: signing in and out of the system. */
class LoginJourneyTest extends UiTestBase {

    @Test
    void validLoginOpensDashboard() {
        login("mehta", "mehta123");
        assertEquals("Faculty Notes Overview", textOf(By.tagName("h2")));
    }

    @Test
    void wrongPasswordShowsErrorAndStaysOnLogin() {
        login("mehta", "wrong-password");
        assertTrue(driver.getCurrentUrl().contains("/login?error"));
        assertTrue(bodyText().toLowerCase().contains("invalid"));
    }

    @Test
    void visitingNotesWithoutLoginRedirectsToLogin() {
        driver.get(url("/notes"));
        assertTrue(driver.getCurrentUrl().contains("/login"));
        assertEquals("Please sign in", textOf(By.tagName("h2")));
    }
}

