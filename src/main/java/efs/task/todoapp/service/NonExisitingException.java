package efs.task.todoapp.service;

public class NonExisitingException extends RuntimeException {
    public NonExisitingException(String message) {
        super(message);
    }
}
