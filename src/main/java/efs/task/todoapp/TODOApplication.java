package efs.task.todoapp;

import com.sun.net.httpserver.HttpServer;
import efs.task.todoapp.util.LoggingUtils;
import efs.task.todoapp.web.TODOHttpHandler;
import efs.task.todoapp.web.WebServerSupplier;

import java.util.logging.Logger;

public class TODOApplication {
    private static final Logger LOGGER = Logger.getLogger(TODOApplication.class.getName());

    //Do not change the constructor
    public TODOApplication() {
        LoggingUtils.loadProperties();
    }

    //Do not change implementation of the main method
    public static void main(String[] args) {
        var application = new TODOApplication();
        var server = application.createServer();
        server.start();

        LOGGER.info("TODOApplication's server started ...");
    }

    public HttpServer createServer() {
        WebServerSupplier webServerSupplier;

        //Do not change this line
        return webServerSupplier.get();
    }
}
