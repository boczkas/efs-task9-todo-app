package efs.task.todoapp.util;

import com.sun.net.httpserver.HttpServer;
import efs.task.todoapp.TODOApplication;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;

public class TODOServerExtension implements Extension, BeforeEachCallback, BeforeAllCallback, AfterEachCallback {
    private TODOApplication todoApplication;
    private HttpServer server;

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        todoApplication = new TODOApplication();
    }

    @Override
    public void beforeEach(ExtensionContext extensionContext) throws Exception {
        server = todoApplication.createServer();
        server.start();
    }

    @Override
    public void afterEach(ExtensionContext extensionContext) throws Exception {
        server.stop(0);
    }
}
