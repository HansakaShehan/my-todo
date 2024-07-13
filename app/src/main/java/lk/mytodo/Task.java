package lk.mytodo;

import java.util.UUID;

public class Task {
    private String id; // Firestore document ID
    private String title;
    private String description;
    private String dueDateTime;
    private String reminder;
    private String priority;
    private boolean completeWithinADay;
    private String email;
    private boolean complete;

    public Task() {
        // Default constructor required for calls to DataSnapshot.getValue(Task.class)
    }

    public Task(String title, String description, String dueDateTime, String reminder, String priority, boolean completeWithinADay, String email) {
        this.title = title;
        this.description = description;
        this.dueDateTime = dueDateTime;
        this.reminder = reminder;
        this.priority = priority;
        this.completeWithinADay = completeWithinADay;
        this.email = email;
        this.complete = false; // Default to incomplete
    }

    // Getters and setters for each field

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDueDateTime() {
        return dueDateTime;
    }

    public void setDueDateTime(String dueDateTime) {
        this.dueDateTime = dueDateTime;
    }

    public String getReminder() {
        return reminder;
    }

    public void setReminder(String reminder) {
        this.reminder = reminder;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public boolean isCompleteWithinADay() {
        return completeWithinADay;
    }

    public void setCompleteWithinADay(boolean completeWithinADay) {
        this.completeWithinADay = completeWithinADay;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }
}
