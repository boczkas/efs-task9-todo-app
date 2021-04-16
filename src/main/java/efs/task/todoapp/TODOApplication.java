package efs.task.todoapp;

import com.sun.net.httpserver.HttpServer;
import efs.task.todoapp.web.WebServerFactory;

import java.util.logging.Logger;

public class TODOApplication {
    private static final Logger LOGGER = Logger.getLogger(TODOApplication.class.getName());

    public static void main(String[] args) {
        var application = new TODOApplication();
        var server = application.createServer();
        server.start();

        LOGGER.info("TODOApplication's server started ...");
    }

    public HttpServer createServer() {
        return WebServerFactory.createServer();
    }
}
