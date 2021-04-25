package efs.task.todoapp.autograding;

import org.assertj.core.api.Condition;

import java.net.URI;
import java.net.http.HttpRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

public final class TestUtils {
    static final String PATH_ROOT = "http://localhost:8080/todo";
    static final String PATH_USER = PATH_ROOT + "/user";
    static final String PATH_TASK = PATH_ROOT + "/task";

    static final String HEADER_AUTH = "auth";

    private TestUtils() {}

    static String userJson(String username, String password) {
        Map<String, String> userProperties = new HashMap<>();
        userProperties.put("username", username);
        userProperties.put("password", password);
        return toJson(userProperties);
    }

    static String taskJson(String description) {
        return taskJson(description, null);
    }

    static String taskJson(String description, String due) {
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

    static HttpRequest.Builder userRequestBuilder() {
        return HttpRequest.newBuilder(URI.create(PATH_USER));
    }

    static HttpRequest.Builder taskRequestBuilder() {
        return HttpRequest.newBuilder(URI.create(PATH_TASK));
    }

    static HttpRequest.Builder taskRequestBuilder(Object taskId) {
        return HttpRequest.newBuilder(URI.create(PATH_TASK + "/" + taskId.toString()));
    }

    static Condition<String> validUUID() {
        return new Condition<>(id -> {
            try {
                UUID.fromString(id);
                return true;
            } catch (IllegalArgumentException e) {
                return false;
            }
        }, "Valid UUID");
    }
}
