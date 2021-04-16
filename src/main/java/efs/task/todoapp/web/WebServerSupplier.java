package efs.task.todoapp.web;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.function.Supplier;

import static java.util.logging.Level.SEVERE;

public class WebServerSupplier implements Supplier<HttpServer> {

    private final TODOHttpHandler httpHandler;

    public WebServerSupplier(TODOHttpHandler httpHandler) {
        this.httpHandler = httpHandler;
    }
}
