package efs.task.todoapp.service;

public class UnathorizedException extends RuntimeException {

    public UnathorizedException(String message) {
        super(message);
    }
}
