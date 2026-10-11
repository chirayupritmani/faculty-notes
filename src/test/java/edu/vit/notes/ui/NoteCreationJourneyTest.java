package edu.vit.notes.ui;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.jupiter.api.Assertions.assertTrue;

/** Journey 2: a faculty member creates a note (valid and invalid data). */
class NoteCreationJourneyTest extends UiTestBase {

    @Test
    void facultyCreatesDraftAndSeesItInList() {
        login("mehta", "mehta123");
        String title = createDraft("Selenium Created Note");
        assertTrue(textOf(By.id("notes-table")).contains(title));
        openNote(title);
        assertTrue(textOf(By.id("note-status")).contains("Draft"));
        assertTrue(textOf(By.id("note-author")).contains("Prof. Mehta"));
    }

    @Test
    void emptyTitleShowsValidationMessage() {
        login("mehta", "mehta123");
        driver.get(url("/notes/new"));
        type(By.id("subject"), "Software Testing");
        type(By.id("content"), "Some content");
        driver.findElement(By.id("save-button")).click();
        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.tagName("body"), "Title is required"));
        assertTrue(bodyText().contains("Title is required"));
    }
}
