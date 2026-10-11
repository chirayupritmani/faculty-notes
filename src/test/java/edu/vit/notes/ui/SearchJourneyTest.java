package edu.vit.notes.ui;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Journey 3: searching notes by subject, title or keyword. */
class SearchJourneyTest extends UiTestBase {

    @Test
    void searchBySubjectShowsOnlyMatchingNotes() {
        login("student", "student123");
        driver.get(url("/notes"));
        type(By.id("search-box"), "Database");
        driver.findElement(By.id("search-button")).click();
        wait.until(d -> d.getCurrentUrl().contains("q=Database"));
        String table = textOf(By.id("notes-table"));
        assertTrue(table.contains("Normalisation in DBMS"));
        assertFalse(table.contains("Introduction to DevOps"));
    }

    @Test
    void searchWithNoMatchShowsEmptyMessage() {
        login("student", "student123");
        driver.get(url("/notes"));
        type(By.id("search-box"), "zzzxyzqq");
        driver.findElement(By.id("search-button")).click();
        assertEquals("No notes found", textOf(By.id("no-results")));
    }
}
