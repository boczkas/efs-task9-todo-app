package efs.task.todoapp.web;

final class UserDecodingException extends RuntimeException {
    UserDecodingException(String message) {
        super(message);
    }

    UserDecodingException(String message, Throwable cause) {
        super(message, cause);
    }
}
