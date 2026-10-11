package edu.vit.notes.ui;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.openqa.selenium.By;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Deliberately failing test used only to show the failure screenshot mechanism.
 * It is skipped unless started with -DdemoFailure=true.
 */
@EnabledIfSystemProperty(named = "demoFailure", matches = "true")
class FailureDemoTest extends UiTestBase {

    @Test
    void dashboardHeadingCheckFailsOnPurpose() {
        login("mehta", "mehta123");
        assertEquals("This heading does not exist", textOf(By.tagName("h2")));
    }
}
