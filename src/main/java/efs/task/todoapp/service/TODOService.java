package efs.task.todoapp.service;

import efs.task.todoapp.repository.TaskRepository;
import efs.task.todoapp.repository.UserRepository;

public class TODOService {
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    public TODOService(UserRepository userRepository, TaskRepository taskRepository) {
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
    }
}
