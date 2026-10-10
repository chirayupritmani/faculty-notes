package edu.vit.notes.model;

public enum Role {
    FACULTY("Faculty"),
    REVIEWER("Reviewer"),
    STUDENT("Student");

    private final String label;

    Role(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
