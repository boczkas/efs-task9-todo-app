package efs.task.todoapp.web;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import efs.task.todoapp.service.TODOService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class TODOHttpHandler implements HttpHandler {
    private final TODOService service;

    public TODOHttpHandler(TODOService service) {
        this.service = service;
    }
}
