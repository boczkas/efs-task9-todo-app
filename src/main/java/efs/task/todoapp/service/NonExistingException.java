package efs.task.todoapp.service;

public class NonExistingException extends RuntimeException {
    NonExistingException(String message) {
        super(message);
    }
}
