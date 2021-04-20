package efs.task.todoapp.web;

import com.sun.net.httpserver.HttpServer;
import efs.task.todoapp.repository.TaskRepository;
import efs.task.todoapp.repository.UserRepository;
import efs.task.todoapp.service.ToDoService;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.logging.Logger;

import static java.util.logging.Level.SEVERE;

public final class WebServerFactory {
    private static final Logger LOGGER = Logger.getLogger(WebServerFactory.class.getName());

    private static final String HOST_NAME = "localhost";
    private static final int PORT = 8080;
    private static final String CONTEXT_PATH = "/todo";
    private static final String TASK_PATH = CONTEXT_PATH + "/task";
    private static final String USER_PATH = CONTEXT_PATH + "/user";

    private WebServerFactory() {}

    public static HttpServer createServer() {
        HttpServer server = null;
        try {
            var address = new InetSocketAddress(HOST_NAME, PORT);
            server = HttpServer.create(address, 0);
            return initContext(server);
        } catch (IOException e) {
            LOGGER.log(SEVERE, "Cannot create server", e);
        }
        return server;
    }

    private static HttpServer initContext(HttpServer server) {
        var userRepository = new UserRepository();
        var taskRepository = new TaskRepository();
        var service = new ToDoService(userRepository, taskRepository);
        var userDecoder = new UserDecoder();

        var userHttpHandler = new UserHttpHandler(USER_PATH, service);
        var taskHttpHandler = new TaskHttpHandler(TASK_PATH, service, userDecoder);

        server.createContext(USER_PATH, userHttpHandler);
        server.createContext(TASK_PATH, taskHttpHandler);

        return server;
    }
}
