package efs.task.todoapp.web;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import efs.task.todoapp.service.ForbiddenException;
import efs.task.todoapp.service.NonExistingException;
import efs.task.todoapp.service.ToDoService;
import efs.task.todoapp.service.UnathorizedException;
import efs.task.todoapp.util.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.logging.Logger;

import static efs.task.todoapp.web.HttpStatus.BAD_REQUEST;
import static efs.task.todoapp.web.HttpStatus.FORBIDDEN;
import static efs.task.todoapp.web.HttpStatus.INTERNAL_SERVER_ERROR;
import static efs.task.todoapp.web.HttpStatus.NOT_FOUND;
import static efs.task.todoapp.web.HttpStatus.UNAUTHORIZED;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.logging.Level.SEVERE;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toMap;

public abstract class TodoHttpHandler implements HttpHandler {
    private final String contextPath;
    protected final ToDoService service;
    protected final Gson gson;

    protected TodoHttpHandler(String contextPath, ToDoService service) {
        this.contextPath = contextPath;
        this.service = service;
        gson = new GsonBuilder().setDateFormat("yyyy-mm-dd").create();
    }

    @Override
    public void handle(HttpExchange exchange) {
        try {
            var requestMethod = exchange.getRequestMethod();
            var method = HttpMethod.from(requestMethod);

            var requestPath = exchange.getRequestURI().getPath();
            var path = requestPath.replaceFirst(contextPath, "");

            var requestHeaders = exchange.getRequestHeaders();
            var headers = requestHeaders.keySet().stream()
                    .collect(toMap(
                            String::toLowerCase,
                            requestHeaders::getFirst
                    ));

            var body = getBody(exchange);

            getLogger().info(method + " " + contextPath + (StringUtils.notBlank(path) ? path : "") + " " + body);

            var response = handle(method, path, headers, body);
            var status = response.getStatus();
            var responseBody = response.getBody();

            writeResponse(exchange, status, responseBody);
        } catch (BadRequestException e) {
            writeResponse(exchange, BAD_REQUEST, e.getLocalizedMessage());
        } catch (UnathorizedException e) {
            writeResponse(exchange, UNAUTHORIZED, e.getLocalizedMessage());
        } catch (NonExistingException e) {
            writeResponse(exchange, NOT_FOUND, e.getLocalizedMessage());
        } catch (ForbiddenException e) {
            writeResponse(exchange, FORBIDDEN, e.getLocalizedMessage());
        } catch (Exception e) {
            getLogger().log(SEVERE, "Exception during handling request", e);
            writeResponse(exchange, INTERNAL_SERVER_ERROR, e.getLocalizedMessage());
        }
    }

    protected abstract HandlerResponse handle(HttpMethod method, String path, Map<String, String> headers, String body);

    protected abstract Logger getLogger();

    private String getBody(HttpExchange exchange) {
        var requestBodyStream = exchange.getRequestBody();
        return new BufferedReader(new InputStreamReader(requestBodyStream, UTF_8))
                .lines()
                .collect(joining());
    }

    private void writeResponse(HttpExchange exchange, HttpStatus status, String responseBody) {
        getLogger().info(status + (nonNull(responseBody) ? " " + responseBody : ""));
        try {
            if (isNull(responseBody)) {
                exchange.sendResponseHeaders(status.getCode(), 0);
            } else {
                var messageBytes = responseBody.getBytes(UTF_8);
                exchange.sendResponseHeaders(status.getCode(), messageBytes.length);
                exchange.getResponseBody().write(messageBytes);
            }
        } catch (IOException e) {
            getLogger().log(SEVERE, "Cannot save response", e);
        }
        exchange.close();
    }
}
