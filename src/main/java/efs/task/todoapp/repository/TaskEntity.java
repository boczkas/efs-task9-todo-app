package efs.task.todoapp.repository;

import efs.task.todoapp.model.Task;

import java.util.Date;
import java.util.UUID;

public class TaskEntity {
    private String username;
    private UUID id;
    private final String description;
    private final Date due;

    public TaskEntity(String username, UUID id, String description, Date due) {
        this.username = username;
        this.id = id;
        this.description = description;
        this.due = due;
    }

    public static TaskEntity taskEntityFrom(String username, Task task) {
        return new TaskEntity(username, null, task.getDescription(), task.getDue());
    }

    public static TaskEntity taskEntityFrom(UUID uuid, TaskEntity other) {
        return new TaskEntity(other.username, uuid, other.description, other.due);
    }

    public String getUsername() {
        return username;
    }

    public UUID getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public Date getDue() {
        return due;
    }
}
