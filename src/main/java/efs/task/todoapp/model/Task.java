package efs.task.todoapp.model;

import efs.task.todoapp.repository.TaskEntity;

import java.util.Date;
import java.util.UUID;

public class Task {
    private UUID id;
    private final String description;
    private final Date due;

    public Task(UUID id, String description, Date due) {
        this.id = id;
        this.description = description;
        this.due = due;
    }

    public static Task taskFrom(TaskEntity taskEntity) {
        return new Task(taskEntity.getId(), taskEntity.getDescription(), taskEntity.getDue());
    }

    public String getDescription() {
        return description;
    }

    public Date getDue() {
        return due;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", due=" + due +
                '}';
    }
}
