package efs.task.todoapp.service;

public class UnathorizedException extends RuntimeException {

    UnathorizedException(String message) {
        super(message);
    }
}
