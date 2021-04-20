package efs.task.todoapp.web;

import com.google.gson.JsonSyntaxException;
import efs.task.todoapp.model.Task;
import efs.task.todoapp.model.TaskIdentifier;
import efs.task.todoapp.model.User;
import efs.task.todoapp.service.ToDoService;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import static efs.task.todoapp.util.StringUtils.isBlank;
import static efs.task.todoapp.web.HandlerResponse.okResponse;
import static efs.task.todoapp.web.HandlerResponse.response;
import static efs.task.todoapp.web.HttpStatus.CREATED;
import static efs.task.todoapp.web.HttpStatus.METHOD_NOT_ALLOWED;

public class TaskHttpHandler extends TodoHttpHandler {
    private static final Logger LOGGER = Logger.getLogger(TaskHttpHandler.class.getName());
    private static final String TASK_ID_GROUP = "taskId";
    private static final String TASK_ID_REGEX = String.format("^/(?<%s>[\\w-]+)[/\\?]?", TASK_ID_GROUP);

    private final UserDecoder userDecoder;
    private final Pattern pattern;

    public TaskHttpHandler(String contextPath, ToDoService service, UserDecoder userDecoder) {
        super(contextPath, service);
        this.userDecoder = userDecoder;
        pattern = Pattern.compile(TASK_ID_REGEX);
    }

    @Override
    protected Logger getLogger() {
        return LOGGER;
    }

    @Override
    protected HandlerResponse handle(HttpMethod method, String path, Map<String, String> headers, String body) {
        User user = userDecoder.decodeAndValidateUser(headers);

        switch (method) {
            case POST:
                return handlePost(body, user);
            case GET:
                return handleGet(path, user);
            case PUT:
                return handlePut(path, body, user);
            case DELETE:
                return handleDelete(path, user);
            default:
                return response(METHOD_NOT_ALLOWED);
        }
    }

    private HandlerResponse handlePost(String body, User user) {
        var task = decodeAndValidateTask(body);
        var uuid = service.saveTask(user, task);

        var idResponse = new TaskIdentifier(uuid);
        var responseJson = gson.toJson(idResponse);

        return response(CREATED, responseJson);
    }

    private HandlerResponse handleGet(String path, User user) {
        var maybeTaskId = getTaskId(path);

        if (maybeTaskId.isPresent()) {
            var task = service.getTask(user, maybeTaskId.get());

            var taskJson = gson.toJson(task);
            return okResponse(taskJson);
        } else {
            var tasks = service.getTasks(user);
            var tasksJson = gson.toJson(tasks);
            return okResponse(tasksJson);
        }
    }

    private HandlerResponse handlePut(String path, String body, User user) {
        var maybeTaskId = getTaskId(path);

        if (maybeTaskId.isEmpty()) {
            throw new BadRequestException("Valid task id in path is required");
        }

        var task = decodeAndValidateTask(body);
        var updatedTask = service.updateTask(user, maybeTaskId.get(), task);

        var taskJson = gson.toJson(updatedTask);
        return okResponse(taskJson);
    }

    private HandlerResponse handleDelete(String path, User user) {
        var maybeTaskId = getTaskId(path);

        if (maybeTaskId.isEmpty()) {
            throw new BadRequestException("Valid task id in path is required");
        }

        service.removeTask(user, maybeTaskId.get());
        return okResponse();
    }

    private Task decodeAndValidateTask(String body) {
        if (isBlank(body)) {
            throw new BadRequestException("Body is required");
        }

        Task task = null;
        try {
            task = gson.fromJson(body, Task.class);
        } catch (JsonSyntaxException e) {
            throw new BadRequestException("Invalid task in body", e);
        }

        if (isBlank(task.getDescription())) {
            throw new BadRequestException("description is required");
        }

        return task;
    }

    private Optional<UUID> getTaskId(String path) {
        var matcher = pattern.matcher(path);
        var matches = matcher.matches();
        if (!matches) {
            return Optional.empty();
        }

        var taskId = matcher.group(TASK_ID_GROUP);
        try {
            var uuid = UUID.fromString(taskId);
            return Optional.of(uuid);
        } catch (Exception e) {
            throw new BadRequestException(String.format("'%s' is not valid UUID", taskId), e);
        }
    }
}
