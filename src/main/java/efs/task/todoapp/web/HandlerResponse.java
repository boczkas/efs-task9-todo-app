package efs.task.todoapp.web;

import static efs.task.todoapp.web.HttpStatus.OK;

final class HandlerResponse {
    private final HttpStatus status;
    private final String body;

    private HandlerResponse(HttpStatus status, String body) {
        this.status = status;
        this.body = body;
    }

    static HandlerResponse okResponse() {
        return new HandlerResponse(OK, null);
    }

    static HandlerResponse okResponse(String body) {
        return new HandlerResponse(OK, body);
    }

    static HandlerResponse response(HttpStatus status) {
        return new HandlerResponse(status, null);
    }

    static HandlerResponse response(HttpStatus status, String body) {
        return new HandlerResponse(status, body);
    }

    HttpStatus getStatus() {
        return status;
    }

    String getBody() {
        return body;
    }
}
