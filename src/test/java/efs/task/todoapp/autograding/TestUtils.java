package efs.task.todoapp.autograding;

import org.assertj.core.api.Condition;
import org.junit.jupiter.params.provider.Arguments;

import java.net.URI;
import java.net.http.HttpRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.net.http.HttpRequest.BodyPublishers.ofString;
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

    static Arguments taskPostArguments(String authHeader, String body) {
        var requestBuilder = taskRequestBuilder();
        if (nonNull(authHeader)) {
            requestBuilder.header(HEADER_AUTH, authHeader);
        }

        return Arguments.of(HEADER_AUTH + " = " + authHeader + " , body = " + body,
                requestBuilder.POST(ofString(body)).build());
    }

    static Arguments taskGetArguments(String authHeader) {
        return taskGetArguments(authHeader, taskRequestBuilder());
    }

    static Arguments taskGetArguments(Object taskId, String authHeader) {
        return taskGetArguments(authHeader, taskRequestBuilder(taskId));
    }

    static Arguments taskGetArguments(String authHeader, HttpRequest.Builder requestBuilder) {
        if (nonNull(authHeader)) {
            requestBuilder.header(HEADER_AUTH, authHeader);
        }

        return Arguments.of(HEADER_AUTH + " = " + authHeader,
                requestBuilder.GET().build());
    }

    static Arguments taskPutArguments(String authHeader, String body) {
        return taskPutArguments(authHeader, body, taskRequestBuilder());
    }

    static Arguments taskPutArguments(Object taskId, String authHeader, String body) {
        return taskPutArguments(authHeader, body, taskRequestBuilder(taskId));
    }

    static Arguments taskPutArguments(String authHeader, String body, HttpRequest.Builder requestBuilder) {
        if (nonNull(authHeader)) {
            requestBuilder.header(HEADER_AUTH, authHeader);
        }

        return Arguments.of(HEADER_AUTH + " = " + authHeader + " , body = " + body,
                requestBuilder.PUT(ofString(body)).build());
    }

    static Arguments taskDeleteArguments(String authHeader) {
        return taskDeleteArguments(authHeader, taskRequestBuilder());
    }

    static Arguments taskDeleteArguments(Object taskId, String authHeader) {
        return taskDeleteArguments(authHeader, taskRequestBuilder(taskId));
    }

    static Arguments taskDeleteArguments(String authHeader, HttpRequest.Builder requestBuilder) {
        if (nonNull(authHeader)) {
            requestBuilder.header(HEADER_AUTH, authHeader);
        }

        return Arguments.of(HEADER_AUTH + " = " + authHeader,
                requestBuilder.DELETE().build());
    }

    static Condition<String> validUUID() {
        return new Condition<>(id -> {
            try {
                //noinspection ResultOfMethodCallIgnored
                UUID.fromString(id);
                return true;
            } catch (IllegalArgumentException e) {
                return false;
            }
        }, "Valid UUID");
    }

    static String wrongCodeMessage(HttpRequest request) {
        var stringBuilder = new StringBuilder()
                .append("Wrong HTTP status code for ")
                .append(request.method())
                .append(" ")
                .append(request.uri())
                .append(" endpoint");

        return stringBuilder.toString();
    }
}
