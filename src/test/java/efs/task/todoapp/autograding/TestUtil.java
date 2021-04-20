package efs.task.todoapp.autograding;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

public final class TestUtil {
    static final String PATH_ROOT = "http://localhost:8080/todo";
    static final String PATH_USER = PATH_ROOT + "/user";
    static final String PATH_TASK = PATH_ROOT + "/task";

    public static final String HEADER_AUTH = "auth";

    private TestUtil() {}

    static String userJson(String username, String password) {
        Map<String, String> userProperties = new HashMap<>();
        userProperties.put("username", username);
        userProperties.put("password", password);
        return toJson(userProperties);
    }

    public static String taskJson(String description) {
        return taskJson(description, null);
    }

    public static String taskJson(String description, String due) {
        Map<String, String> taskProperties = new HashMap<>();
        taskProperties.put("description", description);
        taskProperties.put("due", due);
        return toJson(taskProperties);
    }

    private static String toJson(Map<String, ?> properties) {
        return properties.entrySet().stream()
                .filter(entry -> nonNull(entry.getValue()))
                .map(entry -> '"' + entry.getKey() + "\":\"" + entry.getValue() + '"')
                .collect(Collectors.joining(",", "{", "}"));
    }
}
