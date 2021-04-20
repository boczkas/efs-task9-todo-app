package efs.task.todoapp;

import com.sun.net.httpserver.HttpServer;
import efs.task.todoapp.util.LoggingUtils;
import efs.task.todoapp.web.WebServerFactory;

import java.util.logging.Logger;

public class ToDoApplication {
    private static final Logger LOGGER = Logger.getLogger(ToDoApplication.class.getName());

    public ToDoApplication() {
        LoggingUtils.loadProperties();
    }

    public static void main(String[] args) {
        var application = new ToDoApplication();
        var server = application.createServer();
        server.start();

        LOGGER.info("ToDoApplication's server started ...");
    }

    public HttpServer createServer() {
        var server = WebServerFactory.createServer();
        LOGGER.fine("------------- Server at " + server.getAddress() + " created --------------------------");
        return server;
    }
}
