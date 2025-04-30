package com.example.lance;

public class Project {
    private String title;
    private String dueDate;
    private String description;

    public Project(String title, String dueDate, String description) {
        this.title = title;
        this.dueDate = dueDate;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public String getDueDate() {
        return dueDate;
    }

    public String getDescription() {
        return description;
    }
}
