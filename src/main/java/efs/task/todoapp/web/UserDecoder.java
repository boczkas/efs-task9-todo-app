package efs.task.todoapp.web;

import com.sun.net.httpserver.HttpExchange;
import efs.task.todoapp.model.User;

import java.util.Base64;

import static java.util.Objects.isNull;

public class UserDecoder {
    private static final String AUTH_HEADER = "auth";

    private final Base64.Decoder base64decoder;

    public UserDecoder() {
        base64decoder = Base64.getDecoder();
    }

    protected User getUser(HttpExchange exchange) {
        var authHeaders = exchange.getRequestHeaders().get(AUTH_HEADER);

        if (isNull(authHeaders) || authHeaders.isEmpty()) {
            throw new UserDecodingException("auth header is required");
        }

        return getUser(authHeaders.get(0));
    }

    private User getUser(String authHeader) {
        var headerParts = authHeader.split(":");

        if (headerParts.length != 2) {
            throw new UserDecodingException("auth header does not contain two parts");
        }

        return getUser(headerParts[0], headerParts[1]);
    }

    private User getUser(String usernameEncoded, String passwordEncoded) {
        String username;
        try {
            username = new String(base64decoder.decode(usernameEncoded));
        } catch (IllegalArgumentException e) {
            throw new UserDecodingException("username is not Base64 encoded", e);
        }

        String password;
        try {
            password = new String(base64decoder.decode(passwordEncoded));
        } catch (Exception e) {
            throw new UserDecodingException("password is not Base64 encoded", e);
        }

        return new User(username, password);
    }
}
