package efs.task.todoapp.web;

import com.sun.net.httpserver.HttpServer;
import efs.task.todoapp.repository.TaskRepository;
import efs.task.todoapp.repository.UserRepository;
import efs.task.todoapp.service.TODOService;

public class WebServerFactory {
    public static HttpServer createServer() {
        var server = ;
        var userRepository = new UserRepository();
        var taskRepository = new TaskRepository();
        var service = new TODOService(userRepository, taskRepository);
        var httpHandler = new TODOHttpHandler(service);

        server.createContext(null, httpHandler);

        return server;
    }
}
