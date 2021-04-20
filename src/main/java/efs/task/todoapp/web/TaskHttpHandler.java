package efs.task.todoapp.web;

import com.sun.net.httpserver.HttpExchange;
import efs.task.todoapp.model.Task;
import efs.task.todoapp.model.TaskIdentifier;
import efs.task.todoapp.model.User;
import efs.task.todoapp.service.ForbiddenException;
import efs.task.todoapp.service.NonExisitingException;
import efs.task.todoapp.service.ToDoService;
import efs.task.todoapp.service.UnathorizedException;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import static efs.task.todoapp.util.StringUtils.isBlank;
import static efs.task.todoapp.web.HttpMethod.DELETE;
import static efs.task.todoapp.web.HttpMethod.GET;
import static efs.task.todoapp.web.HttpMethod.POST;
import static efs.task.todoapp.web.HttpMethod.PUT;
import static efs.task.todoapp.web.HttpStatus.BAD_REQUEST;
import static efs.task.todoapp.web.HttpStatus.CREATED;
import static efs.task.todoapp.web.HttpStatus.FORBIDDEN;
import static efs.task.todoapp.web.HttpStatus.METHOD_NOT_ALLOWED;
import static efs.task.todoapp.web.HttpStatus.NOT_FOUND;
import static efs.task.todoapp.web.HttpStatus.OK;
import static efs.task.todoapp.web.HttpStatus.UNAUTHORIZED;
import static java.util.Objects.isNull;

public class TaskHttpHandler extends TodoHttpHandler {
    private static final Logger LOGGER = Logger.getLogger(TaskHttpHandler.class.getName());
    private static final Set<HttpMethod> ALLOWED_METHODS = Set.of(POST, GET, PUT, DELETE);

    private final UserDecoder userDecoder;
    private final Pattern pattern;

    public TaskHttpHandler(ToDoService service, UserDecoder userDecoder, String taskPath) {
        super(service);
        this.userDecoder = userDecoder;

        var regex = "^" + taskPath + "/(?<taskId>[\\w-]+)[/\\?]?";
        pattern = Pattern.compile(regex);
    }

    @Override
    protected Logger getLogger() {
        return LOGGER;
    }

    @Override
    protected void handleExchange(HttpExchange exchange) {
        if (methodIsAllowed(exchange)) {
            try {
                User user = userDecoder.getUser(exchange);
                handleExchange(exchange, user);
            } catch (UserDecodingException e) {
                response(exchange, BAD_REQUEST, e.getLocalizedMessage());
            } catch (UnathorizedException e) {
                response(exchange, UNAUTHORIZED, e.getLocalizedMessage());
            } catch (NonExisitingException e) {
                response(exchange, NOT_FOUND, e.getLocalizedMessage());
            } catch (ForbiddenException e) {
                response(exchange, FORBIDDEN, e.getLocalizedMessage());
            }
        }
    }

    private boolean methodIsAllowed(HttpExchange exchange) {
        var requestMethod = exchange.getRequestMethod();
        var allowed = ALLOWED_METHODS.stream()
                .anyMatch(httpMethod -> httpMethod.is(requestMethod));
        if (!allowed) {
            response(exchange, METHOD_NOT_ALLOWED);
        }
        return allowed;
    }

    private void handleExchange(HttpExchange exchange, User user) {
        var requestMethod = exchange.getRequestMethod();

        if (POST.is(requestMethod) || PUT.is(requestMethod)) {
            var maybeTask = getTask(exchange);
            maybeTask.ifPresent(task -> {
                if (POST.is(requestMethod)) {
                    handlePost(exchange, user, task);
                } else {
                    handlePut(exchange, user, task);
                }
            });
        } else if (GET.is(requestMethod)) {
            handleGet(exchange, user);
        } else {
            handleDelete(exchange, user);
        }
    }

    private void handlePost(HttpExchange exchange, User user, Task task) {
        var uuid = service.saveTask(user, task);
        var idResponse = new TaskIdentifier(uuid);
        var responseBody = gson.toJson(idResponse);
        response(exchange, CREATED, responseBody);
    }

    private void handlePut(HttpExchange exchange, User user, Task task) {
        var maybeTaskId = getTaskUuid(exchange);
        if (maybeTaskId.isPresent()) {
            updateTask(exchange, user, maybeTaskId.get(), task);
        } else {
            response(exchange, BAD_REQUEST, "Valid task id in path is required");
        }
    }

    private void handleGet(HttpExchange exchange, User user) {
        var maybeTaskId = getTaskUuid(exchange);
        if (maybeTaskId.isPresent()) {
            loadTask(exchange, user, maybeTaskId.get());
        } else {
            loadTasks(exchange, user);
        }
    }

    private void handleDelete(HttpExchange exchange, User user) {
        var maybeTaskId = getTaskUuid(exchange);
        if (maybeTaskId.isPresent()) {
            deleteTask(exchange, user, maybeTaskId.get());
        } else {
            response(exchange, BAD_REQUEST, "Valid task id in path is required");
        }
    }

    private Optional<Task> getTask(HttpExchange exchange) {
        var task = getBody(exchange, Task.class);
        LOGGER.fine("Request body:" + task);

        if (isNull(task)) {
            response(exchange, BAD_REQUEST, "Body is required");
            return Optional.empty();
        }

        if (isBlank(task.getDescription())) {
            response(exchange, BAD_REQUEST, "description is required");
            return Optional.empty();
        }

        return Optional.of(task);
    }

    private Optional<UUID> getTaskUuid(HttpExchange exchange) {
        var matcher = pattern.matcher(exchange.getRequestURI().getPath());
        var matches = matcher.matches();
        if (!matches) {
            return Optional.empty();
        }

        var taskId = matcher.group("taskId");
        try {
            var uuid = UUID.fromString(taskId);
            return Optional.of(uuid);
        } catch (Exception e) {
            LOGGER.severe(e.getLocalizedMessage());
            return Optional.empty();
        }
    }

    private void loadTask(HttpExchange exchange, User user, UUID taskId) {
        var task = service.findTask(user, taskId);

        var responseBody = gson.toJson(task);
        response(exchange, OK, responseBody);
    }

    private void loadTasks(HttpExchange exchange, User user) {
        var tasks = service.findTasks(user);
        var responseBody = gson.toJson(tasks);
        response(exchange, OK, responseBody);
    }

    private void updateTask(HttpExchange exchange, User user, UUID taskId, Task task) {
        var updatedTask = service.updateTask(user, taskId, task);

        var responseBody = gson.toJson(updatedTask);
        response(exchange, OK, responseBody);
    }

    private void deleteTask(HttpExchange exchange, User user, UUID taskId) {
        var uuidOfRemovedTask = service.removeTask(user, taskId);

        response(exchange, OK);
    }
}
