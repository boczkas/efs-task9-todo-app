package efs.task.todoapp.web;

public enum HttpMethod {
    POST, GET, PUT, DELETE;

    public boolean is(String method) {
        return name().equalsIgnoreCase(method);
    }
}
