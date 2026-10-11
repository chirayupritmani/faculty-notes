package edu.vit.notes.ui;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** Journey 4: Draft -> Under Review -> Published, across two users (faculty and reviewer). */
class ReviewWorkflowJourneyTest extends UiTestBase {

    @Test
    void noteMovesFromDraftToPublished() {
        login("mehta", "mehta123");
        String title = createDraft("Selenium Workflow Note");
        openNote(title);
        assertEquals("Draft", textOf(By.id("note-status")));

        driver.findElement(By.id("submit-button")).click();
        wait.until(d -> d.findElement(By.id("note-status")).getText().equals("Under Review"));

        signOut();
        login("reviewer", "review123");
        openNote(title);
        driver.findElement(By.id("approve-button")).click();
        wait.until(d -> d.findElement(By.id("note-status")).getText().equals("Published"));
        assertEquals("Published", textOf(By.id("note-status")));
    }

    @Test
    void returningNoteWithoutCommentIsRejected() {
        login("mehta", "mehta123");
        String title = createDraft("Selenium Return Note");
        openNote(title);
        driver.findElement(By.id("submit-button")).click();
        wait.until(d -> d.findElement(By.id("note-status")).getText().equals("Under Review"));

        signOut();
        login("reviewer", "review123");
        openNote(title);
        driver.findElement(By.id("return-button")).click();
        assertEquals("A comment is required to return a note.", textOf(By.id("flash-error")));
        assertEquals("Under Review", textOf(By.id("note-status")));
    }
}
