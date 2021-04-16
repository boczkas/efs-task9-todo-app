package efs.task.todoapp.util;

import com.sun.net.httpserver.HttpServer;
import efs.task.todoapp.TODOApplication;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;

public class TODOServerExtension implements Extension, BeforeEachCallback, AfterEachCallback {
    private HttpServer server;

    @Override
    public void beforeEach(ExtensionContext extensionContext) {
        var todoApplication = new TODOApplication();
        server = todoApplication.createServer();
        server.start();
    }

    @Override
    public void afterEach(ExtensionContext extensionContext) {
        server.stop(0);
    }
}
