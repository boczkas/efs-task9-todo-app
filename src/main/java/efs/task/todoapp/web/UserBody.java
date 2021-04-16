package efs.task.todoapp.web;

public class UserBody {
    private final String username;
    private final String password;

    public UserBody(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}