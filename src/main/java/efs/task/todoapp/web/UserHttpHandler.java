package efs.task.todoapp.web;

import com.sun.net.httpserver.HttpExchange;
import efs.task.todoapp.model.User;
import efs.task.todoapp.service.ToDoService;

import java.io.IOException;
import java.util.Optional;
import java.util.logging.Logger;

import static efs.task.todoapp.util.StringUtils.isBlank;
import static efs.task.todoapp.web.HttpMethod.POST;
import static efs.task.todoapp.web.HttpStatus.BAD_REQUEST;
import static efs.task.todoapp.web.HttpStatus.CONFLICT;
import static efs.task.todoapp.web.HttpStatus.CREATED;
import static efs.task.todoapp.web.HttpStatus.METHOD_NOT_ALLOWED;
import static java.util.Objects.isNull;

public class UserHttpHandler extends TodoHttpHandler {
    private static final Logger LOGGER = Logger.getLogger(UserHttpHandler.class.getName());

    public UserHttpHandler(ToDoService service) {
        super(service);
    }

    @Override
    protected Logger getLogger() {
        return LOGGER;
    }

    @Override
    protected void handleExchange(HttpExchange exchange) {
        if (isMethodAllowed(exchange)) {
            var maybeUser = getUser(exchange);
            maybeUser.ifPresent(user -> saveUser(exchange, user));
        }
    }

    private boolean isMethodAllowed(HttpExchange exchange) {
        var allowed = POST.is(exchange.getRequestMethod());
        if (!allowed) {
            response(exchange, METHOD_NOT_ALLOWED);
        }
        return allowed;
    }

    private Optional<User> getUser(HttpExchange exchange) {
        var user = getBody(exchange, User.class);
        LOGGER.fine("Request body:" + user);

        if (isNull(user)) {
            response(exchange, BAD_REQUEST, "Body is required");
            return Optional.empty();
        }

        if (isBlank(user.getUsername())) {
            response(exchange, BAD_REQUEST, "username is required");
            return Optional.empty();
        }

        if (isBlank(user.getPassword())) {
            response(exchange, BAD_REQUEST, "password is required");
            return Optional.empty();
        }

        return Optional.of(user);
    }

    private void saveUser(HttpExchange exchange, User user) {
        var userAdded = service.saveUser(user);
        if (userAdded) {
            response(exchange, CREATED);
        } else {
            response(exchange, CONFLICT, String.format("User with '%s' username already exists", user.getUsername()));
        }
    }
}
