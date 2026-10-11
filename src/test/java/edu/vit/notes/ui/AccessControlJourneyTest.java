package edu.vit.notes.ui;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Journey 5: role-based access (a student may read notes but not create them). */
class AccessControlJourneyTest extends UiTestBase {

    @Test
    void studentCanReadNotesList() {
        login("student", "student123");
        driver.get(url("/notes"));
        assertTrue(textOf(By.id("notes-table")).contains("Introduction to DevOps"));
    }

    @Test
    void studentCannotOpenNewNoteForm() {
        login("student", "student123");
        driver.get(url("/notes/new"));
        assertTrue(driver.getPageSource().contains("403"));
        assertFalse(driver.getPageSource().contains("Save as draft"));
    }
}
