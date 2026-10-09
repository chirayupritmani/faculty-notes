package edu.vit.notes.model;

public enum NoteStatus {
    DRAFT("Draft"),
    UNDER_REVIEW("Under Review"),
    PUBLISHED("Published");

    private final String label;

    NoteStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
