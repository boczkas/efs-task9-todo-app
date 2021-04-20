package efs.task.todoapp.web;

import efs.task.todoapp.model.User;
import efs.task.todoapp.service.ToDoService;

import java.util.Map;
import java.util.logging.Logger;

import static efs.task.todoapp.util.StringUtils.isBlank;
import static efs.task.todoapp.web.HandlerResponse.response;
import static efs.task.todoapp.web.HttpMethod.POST;
import static efs.task.todoapp.web.HttpStatus.CONFLICT;
import static efs.task.todoapp.web.HttpStatus.CREATED;
import static efs.task.todoapp.web.HttpStatus.METHOD_NOT_ALLOWED;

public class UserHttpHandler extends TodoHttpHandler {
    private static final Logger LOGGER = Logger.getLogger(UserHttpHandler.class.getName());

    public UserHttpHandler(String contextPath, ToDoService service) {
        super(contextPath, service);
    }

    @Override
    protected Logger getLogger() {
        return LOGGER;
    }

    @Override
    protected HandlerResponse handle(HttpMethod method, String path, Map<String, String> headers, String body) {
        if (method != POST) {
            return response(METHOD_NOT_ALLOWED);
        }

        var user = decodeAndValidateUser(body);
        return saveUser(user);
    }

    private User decodeAndValidateUser(String body) {
        if (isBlank(body)) {
            throw new BadRequestException("Body is required");
        }

        var user = gson.fromJson(body, User.class);

        if (isBlank(user.getUsername())) {
            throw new BadRequestException("username is required");
        }

        if (isBlank(user.getPassword())) {
            throw new BadRequestException("password is required");
        }

        return user;
    }

    private HandlerResponse saveUser(User user) {
        var userAdded = service.saveUser(user);
        if (userAdded) {
            return response(CREATED);
        } else {
            return response(CONFLICT, String.format("User with '%s' username already exists", user.getUsername()));
        }
    }
}
