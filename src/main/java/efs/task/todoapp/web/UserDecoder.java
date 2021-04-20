package efs.task.todoapp.web;

import efs.task.todoapp.model.User;

import java.util.Base64;
import java.util.Map;

import static efs.task.todoapp.util.StringUtils.isBlank;

public class UserDecoder {
    private static final String AUTH_HEADER = "auth";

    private final Base64.Decoder base64decoder;

    public UserDecoder() {
        base64decoder = Base64.getDecoder();
    }

    User decodeAndValidateUser(Map<String, String> headers) {
        var authHeader = headers.get(AUTH_HEADER);

        if (isBlank(authHeader)) {
            throw new BadRequestException("auth header is required");
        }

        var headerParts = authHeader.split(":");

        if (headerParts.length != 2) {
            throw new BadRequestException("auth header does not contain two parts");
        }

        return getUser(headerParts[0], headerParts[1]);
    }

    private User getUser(String usernameEncoded, String passwordEncoded) {
        String username;
        try {
            username = new String(base64decoder.decode(usernameEncoded));
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("username is not Base64 encoded", e);
        }

        String password;
        try {
            password = new String(base64decoder.decode(passwordEncoded));
        } catch (Exception e) {
            throw new BadRequestException("password is not Base64 encoded", e);
        }

        return new User(username, password);
    }
}
