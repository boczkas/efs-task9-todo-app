package efs.task.todoapp.web;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import efs.task.todoapp.service.ToDoService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Logger;

import static efs.task.todoapp.web.HttpStatus.INTERNAL_SERVER_ERROR;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.logging.Level.SEVERE;
import static java.util.stream.Collectors.joining;

public abstract class TodoHttpHandler implements HttpHandler {
    protected final ToDoService service;
    protected final Gson gson;

    protected TodoHttpHandler(ToDoService service) {
        this.service = service;
        gson = new GsonBuilder().setDateFormat("yyyy-mm-dd").create();
    }

    @Override
    public void handle(HttpExchange exchange) {
        getLogger().info(exchange.getRequestMethod() + " " + exchange.getRequestURI());

        try {
            handleExchange(exchange);
        } catch (Exception e) {
            responseWithError(exchange, e);
        }
    }

    protected abstract Logger getLogger();

    protected abstract void handleExchange(HttpExchange exchange);

    protected <T> T getBody(HttpExchange exchange, Class<T> aClass) {
        var requestBodyStream = exchange.getRequestBody();
        var body = new BufferedReader(new InputStreamReader(requestBodyStream, UTF_8))
                .lines()
                .collect(joining());
        return gson.fromJson(body, aClass);
    }

    protected void response(HttpExchange exchange, HttpStatus status) {
        getLogger().info(status.name());

        try {
            exchange.sendResponseHeaders(status.getCode(), 0);
        } catch (IOException e) {
            getLogger().log(SEVERE, "Cannot save response", e);
        }
        exchange.close();
    }

    protected void response(HttpExchange exchange, HttpStatus status, String message) {
        getLogger().info(status + " : " + message);

        var messageBytes = message.getBytes(UTF_8);

        try {
            exchange.sendResponseHeaders(status.getCode(), messageBytes.length);
            exchange.getResponseBody().write(messageBytes);
        } catch (IOException e) {
            getLogger().log(SEVERE, "Cannot save response", e);
        }
        exchange.close();
    }

    private void responseWithError(HttpExchange exchange, Exception e) {
        var message = e.getLocalizedMessage();

        getLogger().log(SEVERE, "Exception during request handling", e);

        response(exchange, INTERNAL_SERVER_ERROR, message);
    }
}
