package efs.task.todoapp.service;

public class ForbiddenException extends RuntimeException {
    ForbiddenException(String message) {
        super(message);
    }
}
