package efs.task.todoapp.service;

import efs.task.todoapp.model.Task;
import efs.task.todoapp.model.User;
import efs.task.todoapp.repository.TaskEntity;
import efs.task.todoapp.repository.TaskRepository;
import efs.task.todoapp.repository.UserRepository;

import java.util.List;
import java.util.UUID;

import static efs.task.todoapp.model.Task.taskFrom;
import static efs.task.todoapp.repository.TaskEntity.taskEntityFrom;
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toList;

public class ToDoService {
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    public ToDoService(UserRepository userRepository, TaskRepository taskRepository) {
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
    }

    public boolean saveUser(User user) {
        var usernameSaved = userRepository.save(user);
        return usernameSaved != null;
    }

    public UUID saveTask(User user, Task task) {
        checkIfUserAuthorized(user);

        var taskEntity = taskEntityFrom(user.getUsername(), task);
        return taskRepository.save(taskEntity);
    }

    public Task getTask(User user, UUID taskId) {
        checkIfUserAuthorized(user);

        var taskEntity = taskRepository.query(taskId);
        if (isNull(taskEntity)) {
            throw new NonExistingException(String.format("Task with '%s' id doesn't exist", taskId));
        }

        if (!user.getUsername().equals(taskEntity.getUsername())) {
            throw new ForbiddenException("Task belongs to another user");
        }

        return taskFrom(taskEntity);
    }

    public List<Task> getTasks(User user) {
        checkIfUserAuthorized(user);

        List<TaskEntity> tasks = taskRepository.query(taskEntity -> user.getUsername().equals(taskEntity.getUsername()));
        return tasks.stream()
                .map(Task::taskFrom)
                .collect(toList());
    }

    public Task updateTask(User user, UUID taskId, Task task) {
        checkIfUserAuthorized(user);

        var taskEntity = taskRepository.query(taskId);
        if (isNull(taskEntity)) {
            throw new NonExistingException(String.format("Task with '%s' id doesn't exist", taskId));
        }

        if (!user.getUsername().equals(taskEntity.getUsername())) {
            throw new ForbiddenException("Task belongs to another user");
        }

        taskEntity = taskEntityFrom(user.getUsername(), task);
        var updatedTaskEntity = taskRepository.update(taskId, taskEntity);
        return taskFrom(updatedTaskEntity);
    }

    public UUID removeTask(User user, UUID taskId) {
        checkIfUserAuthorized(user);

        var taskEntity = taskRepository.query(taskId);
        if (isNull(taskEntity)) {
            throw new NonExistingException(String.format("Task with '%s' id doesn't exist", taskId));
        }

        if (!user.getUsername().equals(taskEntity.getUsername())) {
            throw new ForbiddenException("Task belongs to another user");
        }

        var deleted = taskRepository.delete(taskId);
        return deleted ? taskId : null;
    }

    private void checkIfUserAuthorized(User user) {
        var username = user.getUsername();

        var userFromRepo = userRepository.query(username);
        if (isNull(userFromRepo)) {
            throw new UnathorizedException(String.format("User with '%s' username doesn't exist", username));
        }

        if (!userFromRepo.getPassword().equals(user.getPassword())) {
            throw new UnathorizedException("Wrong password");
        }
    }
}
