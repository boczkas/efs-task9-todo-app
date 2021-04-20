package efs.task.todoapp.web;

public enum HttpMethod {
    POST, GET, PUT, DELETE;

    public static HttpMethod from(String requestMethod) {
        return valueOf(requestMethod.toUpperCase());
    }

    public boolean is(String method) {
        return name().equalsIgnoreCase(method);
    }
}
