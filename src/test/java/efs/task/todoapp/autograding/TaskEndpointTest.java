package efs.task.todoapp.autograding;

import efs.task.todoapp.util.ToDoServerExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Stream;

import static efs.task.todoapp.autograding.TestUtil.HEADER_AUTH;
import static efs.task.todoapp.autograding.TestUtil.PATH_TASK;
import static efs.task.todoapp.autograding.TestUtil.PATH_USER;
import static efs.task.todoapp.autograding.TestUtil.taskJson;
import static efs.task.todoapp.autograding.TestUtil.userJson;
import static efs.task.todoapp.web.HttpStatus.BAD_REQUEST;
import static efs.task.todoapp.web.HttpStatus.CREATED;
import static efs.task.todoapp.web.HttpStatus.FORBIDDEN;
import static efs.task.todoapp.web.HttpStatus.NOT_FOUND;
import static efs.task.todoapp.web.HttpStatus.OK;
import static efs.task.todoapp.web.HttpStatus.UNAUTHORIZED;
import static java.net.http.HttpRequest.BodyPublishers.ofString;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(ToDoServerExtension.class)
class TaskEndpointTest {

    private static final UUID UUID = java.util.UUID.randomUUID();
    private HttpClient httpClient;

    @BeforeEach
    void setUp() {
        httpClient = HttpClient.newHttpClient();
    }

    private static Stream<HttpRequest.Builder> badRequests() {
        return Stream.of(
                taskRequestBuilder()
                        .POST(ofString(taskJson("task"))),
                taskRequestBuilder()
                        .header(HEADER_AUTH, "amFuS293YWxza2k=")
                        .POST(ofString(taskJson("task"))),
                taskRequestBuilder()
                        .header(HEADER_AUTH, "dummy:YW0hc0sjMTIz")
                        .POST(ofString(taskJson("task"))),
                taskRequestBuilder()
                        .header(HEADER_AUTH, "amFuS293YWxza2k=:dummy")
                        .POST(ofString(taskJson("task"))),
                taskRequestBuilder()
                        .GET(),
                taskRequestBuilder()
                        .header(HEADER_AUTH, "amFuS293YWxza2k=")
                        .GET(),
                taskRequestBuilder()
                        .header(HEADER_AUTH, "dummy:YW0hc0sjMTIz")
                        .GET(),
                taskRequestBuilder()
                        .header(HEADER_AUTH, "amFuS293YWxza2k=:dummy")
                        .GET(),
                taskRequestBuilder(UUID)
                        .PUT(ofString(taskJson("task"))),
                taskRequestBuilder(UUID)
                        .header(HEADER_AUTH, "amFuS293YWxza2k=")
                        .PUT(ofString(taskJson("task"))),
                taskRequestBuilder(UUID)
                        .header(HEADER_AUTH, "dummy:YW0hc0sjMTIz")
                        .PUT(ofString(taskJson("task"))),
                taskRequestBuilder(UUID)
                        .header(HEADER_AUTH, "amFuS293YWxza2k=:dummy")
                        .PUT(ofString(taskJson("task"))),
                taskRequestBuilder()
                        .DELETE(),
                taskRequestBuilder()
                        .header(HEADER_AUTH, "amFuS293YWxza2k=")
                        .DELETE(),
                taskRequestBuilder()
                        .header(HEADER_AUTH, "dummy:YW0hc0sjMTIz")
                        .DELETE(),
                taskRequestBuilder()
                        .header(HEADER_AUTH, "amFuS293YWxza2k=:dummy")
                        .DELETE(),
                taskRequestBuilder()
                        .header(HEADER_AUTH, "amFuS293YWxza2k=:YW0hc0sjMTIz")
                        .POST(ofString("")),
                taskRequestBuilder()
                        .header(HEADER_AUTH, "amFuS293YWxza2k=:YW0hc0sjMTIz")
                        .POST(ofString(taskJson(null))),
                taskRequestBuilder(UUID)
                        .header(HEADER_AUTH, "amFuS293YWxza2k=:YW0hc0sjMTIz")
                        .PUT(ofString("")),
                taskRequestBuilder(UUID)
                        .header(HEADER_AUTH, "amFuS293YWxza2k=:YW0hc0sjMTIz")
                        .PUT(ofString(taskJson(null))),
                taskRequestBuilder()
                        .header(HEADER_AUTH, "amFuS293YWxza2k=:YW0hc0sjMTIz")
                        .PUT(ofString(taskJson("task"))),
                taskRequestBuilder("123")
                        .header(HEADER_AUTH, "amFuS293YWxza2k=:YW0hc0sjMTIz")
                        .PUT(ofString(taskJson("task"))),
                taskRequestBuilder()
                        .header(HEADER_AUTH, "amFuS293YWxza2k=:YW0hc0sjMTIz")
                        .DELETE(),
                taskRequestBuilder("123")
                        .header(HEADER_AUTH, "amFuS293YWxza2k=:YW0hc0sjMTIz")
                        .DELETE()
        );
    }

    @ParameterizedTest
    @MethodSource("badRequests")
    @Timeout(1)
    void shouldReturnBadRequestStatus(HttpRequest.Builder httpRequestBuilder) throws IOException, InterruptedException {
        //given

        //when
        var httpResponse = httpClient.send(httpRequestBuilder.build(), HttpResponse.BodyHandlers.ofString());

        //then
        assertThat(httpResponse.statusCode()).as("Response status code").isEqualTo(BAD_REQUEST.getCode());
    }

    private static Stream<HttpRequest.Builder> unauthorizedRequests() {
        return Stream.of(
                taskRequestBuilder()
                        .header(HEADER_AUTH, "amFuS293YWxza2k=:cGFzc3dvcmQ=")
                        .POST(ofString(taskJson("task"))),
                taskRequestBuilder()
                        .header(HEADER_AUTH, "dXNlcm5hbWU=:YW0hc0sjMTIz")
                        .POST(ofString(taskJson("task"))),
                taskRequestBuilder()
                        .header(HEADER_AUTH, "amFuS293YWxza2k=:cGFzc3dvcmQ=")
                        .GET(),
                taskRequestBuilder()
                        .header(HEADER_AUTH, "dXNlcm5hbWU=:YW0hc0sjMTIz")
                        .GET(),
                taskRequestBuilder(UUID)
                        .header(HEADER_AUTH, "amFuS293YWxza2k=:cGFzc3dvcmQ=")
                        .GET(),
                taskRequestBuilder(UUID)
                        .header(HEADER_AUTH, "dXNlcm5hbWU=:YW0hc0sjMTIz")
                        .GET(),
                taskRequestBuilder(UUID)
                        .header(HEADER_AUTH, "amFuS293YWxza2k=:cGFzc3dvcmQ=")
                        .PUT(ofString(taskJson("task"))),
                taskRequestBuilder(UUID)
                        .header(HEADER_AUTH, "dXNlcm5hbWU=:YW0hc0sjMTIz")
                        .PUT(ofString(taskJson("task"))),
                taskRequestBuilder(UUID)
                        .header(HEADER_AUTH, "amFuS293YWxza2k=:cGFzc3dvcmQ=")
                        .DELETE(),
                taskRequestBuilder(UUID)
                        .header(HEADER_AUTH, "dXNlcm5hbWU=:YW0hc0sjMTIz")
                        .DELETE()
        );
    }

    @ParameterizedTest
    @MethodSource("unauthorizedRequests")
    @Timeout(1)
    void shouldReturnUnauthorizedStatus(HttpRequest.Builder httpRequestBuilder) throws IOException, InterruptedException {
        //given
        var userRequest = userRequestBuilder()
                .POST(ofString(userJson("username", "password")))
                .build();
        httpClient.send(userRequest, HttpResponse.BodyHandlers.ofString());

        //when
        var httpResponse = httpClient.send(httpRequestBuilder.build(), HttpResponse.BodyHandlers.ofString());

        //then
        assertThat(httpResponse.statusCode()).as("Response status code").isEqualTo(UNAUTHORIZED.getCode());
    }

    @Test
    @Timeout(1)
    void shouldCreateTask() throws IOException, InterruptedException {
        //given
        var userRequest = userRequestBuilder()
                .POST(ofString(userJson("username", "password")))
                .build();
        httpClient.send(userRequest, HttpResponse.BodyHandlers.ofString());

        var postTaskRequest = taskRequestBuilder()
                .header(HEADER_AUTH, "dXNlcm5hbWU=:cGFzc3dvcmQ=")
                .POST(ofString(taskJson("buy milk", "2021-06-30")))
                .build();

        //when
        var postTaskResponse = httpClient.send(postTaskRequest, HttpResponse.BodyHandlers.ofString());

        //then
        assertThat(postTaskResponse.statusCode()).as("Response status code").isEqualTo(CREATED.getCode());
        assertThat(postTaskResponse.body()).as("Response body").contains("{", "\"id\"", ":", "}");
    }

    @Test
    @Timeout(1)
    void shouldReturnAllTasksForUser() throws IOException, InterruptedException {
        //given
        var user1Request = userRequestBuilder()
                .POST(ofString(userJson("username", "password")))
                .build();
        httpClient.send(user1Request, HttpResponse.BodyHandlers.ofString());

        var user2Request = userRequestBuilder()
                .POST(ofString(userJson("username2", "password")))
                .build();
        httpClient.send(user2Request, HttpResponse.BodyHandlers.ofString());

        var postTaskForUser1Request = taskRequestBuilder()
                .header(HEADER_AUTH, "dXNlcm5hbWU=:cGFzc3dvcmQ=")
                .POST(ofString(taskJson("buy milk", "2021-06-30")))
                .build();
        httpClient.send(postTaskForUser1Request, HttpResponse.BodyHandlers.ofString());

        postTaskForUser1Request = taskRequestBuilder()
                .header(HEADER_AUTH, "dXNlcm5hbWU=:cGFzc3dvcmQ=")
                .POST(ofString(taskJson("eat an apple")))
                .build();
        httpClient.send(postTaskForUser1Request, HttpResponse.BodyHandlers.ofString());

        var postTaskForUser2Request = taskRequestBuilder()
                .header(HEADER_AUTH, "dXNlcm5hbWUy:cGFzc3dvcmQ=")
                .POST(ofString(taskJson("buy milk", "2021-06-30")))
                .build();
        httpClient.send(postTaskForUser2Request, HttpResponse.BodyHandlers.ofString());

        var getForUser1Request = taskRequestBuilder()
                .header(HEADER_AUTH, "dXNlcm5hbWU=:cGFzc3dvcmQ=")
                .GET()
                .build();

        //when
        var getForUser1Response = httpClient.send(getForUser1Request, HttpResponse.BodyHandlers.ofString());

        //then
        assertThat(getForUser1Response.statusCode()).as("Response status code").isEqualTo(OK.getCode());
        assertThat(getForUser1Response.body()).as("Response body").contains("[", "{", "\"id\"", ":", "}", "]",
                "\"description\":\"buy milk\"",
                "\"due\":\"2021-06-30\"",
                "\"description\":\"eat an apple\""
        );
    }

    @Test
    @Timeout(1)
    void shouldReturnTask() throws IOException, InterruptedException {
        //given
        var userRequest = userRequestBuilder()
                .POST(ofString(userJson("username", "password")))
                .build();
        httpClient.send(userRequest, HttpResponse.BodyHandlers.ofString());

        var postTaskRequest = taskRequestBuilder()
                .header(HEADER_AUTH, "dXNlcm5hbWU=:cGFzc3dvcmQ=")
                .POST(ofString(taskJson("buy milk", "2021-06-30")))
                .build();
        var postTaskResponse = httpClient.send(postTaskRequest, HttpResponse.BodyHandlers.ofString());
        var taskId = getIdOfCreatedTask(postTaskResponse);

        var getRequest = taskRequestBuilder(taskId)
                .header(HEADER_AUTH, "dXNlcm5hbWU=:cGFzc3dvcmQ=")
                .GET()
                .build();

        //when
        var getResponse = httpClient.send(getRequest, HttpResponse.BodyHandlers.ofString());

        //then
        assertThat(getResponse.statusCode()).as("Response status code").isEqualTo(OK.getCode());
        assertThat(getResponse.body()).as("Response body").contains("{",
                "\"id\":\"" + taskId + "\"",
                "\"description\":\"buy milk\"",
                "\"due\":\"2021-06-30\"",
                "}"
        );
    }

    private static Stream<HttpRequest.Builder> notFoundRequests() {
        return Stream.of(
                taskRequestBuilder(UUID)
                        .header(HEADER_AUTH, "dXNlcm5hbWU=:cGFzc3dvcmQ=")
                        .GET(),
                taskRequestBuilder(UUID)
                        .header(HEADER_AUTH, "dXNlcm5hbWU=:cGFzc3dvcmQ=")
                        .PUT(ofString(taskJson("task"))),
                taskRequestBuilder(UUID)
                        .header(HEADER_AUTH, "dXNlcm5hbWU=:cGFzc3dvcmQ=")
                        .DELETE()
        );
    }

    @ParameterizedTest
    @MethodSource("notFoundRequests")
    @Timeout(1)
    void shouldReturnNotFound(HttpRequest.Builder httpRequestBuilder) throws IOException, InterruptedException {
        //given
        var userRequest = userRequestBuilder()
                .POST(ofString(userJson("username", "password")))
                .build();
        httpClient.send(userRequest, HttpResponse.BodyHandlers.ofString());

        //when
        var httpResponse = httpClient.send(httpRequestBuilder.build(), HttpResponse.BodyHandlers.ofString());

        //then
        assertThat(httpResponse.statusCode()).as("Response status code").isEqualTo(NOT_FOUND.getCode());
    }

    private static Stream<Function<String, HttpRequest.Builder>> forbiddenRequests() {
        return Stream.of(
                taskId -> taskRequestBuilder(taskId)
                        .header(HEADER_AUTH, "dXNlcm5hbWUy:cGFzc3dvcmQ=")
                        .GET(),
                taskId -> taskRequestBuilder(taskId)
                        .header(HEADER_AUTH, "dXNlcm5hbWUy:cGFzc3dvcmQ=")
                        .PUT(ofString(taskJson("eat an apple"))),
                taskId -> taskRequestBuilder(taskId)
                        .header(HEADER_AUTH, "dXNlcm5hbWUy:cGFzc3dvcmQ=")
                        .DELETE()
        );
    }

    @ParameterizedTest
    @MethodSource("forbiddenRequests")
    @Timeout(1)
    void shouldReturnForbiddenStatus(Function<String, HttpRequest.Builder> requestBuilderForUser2Function) throws IOException, InterruptedException {
        //given
        var user1Request = userRequestBuilder()
                .POST(ofString(userJson("username", "password")))
                .build();
        httpClient.send(user1Request, HttpResponse.BodyHandlers.ofString());

        var user2Request = userRequestBuilder()
                .POST(ofString(userJson("username2", "password")))
                .build();
        httpClient.send(user2Request, HttpResponse.BodyHandlers.ofString());

        var postTaskForUser1Request = taskRequestBuilder()
                .header(HEADER_AUTH, "dXNlcm5hbWU=:cGFzc3dvcmQ=")
                .POST(ofString(taskJson("buy milk", "2021-06-30")))
                .build();
        var postTaskForUser1Response = httpClient.send(postTaskForUser1Request, HttpResponse.BodyHandlers.ofString());
        var taskId = getIdOfCreatedTask(postTaskForUser1Response);

        var httpRequest = requestBuilderForUser2Function.apply(taskId).build();

        //when
        var httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        //then
        assertThat(httpResponse.statusCode()).as("Response status code").isEqualTo(FORBIDDEN.getCode());
    }

    @Test
    @Timeout(1)
    void shouldUpdateTask() throws IOException, InterruptedException {
        //given
        var userRequest = userRequestBuilder()
                .POST(ofString(userJson("username", "password")))
                .build();
        httpClient.send(userRequest, HttpResponse.BodyHandlers.ofString());

        var postTaskRequest = taskRequestBuilder()
                .header(HEADER_AUTH, "dXNlcm5hbWU=:cGFzc3dvcmQ=")
                .POST(ofString(taskJson("buy milk", "2021-06-30")))
                .build();
        var postTaskResponse = httpClient.send(postTaskRequest, HttpResponse.BodyHandlers.ofString());
        var taskId = getIdOfCreatedTask(postTaskResponse);

        var putRequest = taskRequestBuilder(taskId)
                .header(HEADER_AUTH, "dXNlcm5hbWU=:cGFzc3dvcmQ=")
                .PUT(ofString(taskJson("eat an apple")))
                .build();
        var getRequest = taskRequestBuilder(taskId)
                .header(HEADER_AUTH, "dXNlcm5hbWU=:cGFzc3dvcmQ=")
                .GET()
                .build();

        //when
        var putResponse = httpClient.send(putRequest, HttpResponse.BodyHandlers.ofString());
        var getResponse = httpClient.send(getRequest, HttpResponse.BodyHandlers.ofString());

        //then
        assertThat(putResponse.statusCode()).as("Put response status code").isEqualTo(OK.getCode());
        assertThat(putResponse.body()).as("Put response body").contains("{",
                "\"id\":\"" + taskId + "\"",
                "\"description\":\"eat an apple\"",
                "}"
        );
        assertThat(getResponse.statusCode()).as("Get response status code").isEqualTo(OK.getCode());
        assertThat(putResponse.body()).as("Get esponse body").contains("{",
                "\"id\":\"" + taskId + "\"",
                "\"description\":\"eat an apple\"",
                "}"
        );
    }

    @Test
    @Timeout(1)
    void shouldDeleteTask() throws IOException, InterruptedException {
        //given
        var userRequest = userRequestBuilder()
                .POST(ofString(userJson("username", "password")))
                .build();
        httpClient.send(userRequest, HttpResponse.BodyHandlers.ofString());

        var postTaskRequest = taskRequestBuilder()
                .header(HEADER_AUTH, "dXNlcm5hbWU=:cGFzc3dvcmQ=")
                .POST(ofString(taskJson("buy milk", "2021-06-30")))
                .build();
        var postTaskResponse = httpClient.send(postTaskRequest, HttpResponse.BodyHandlers.ofString());
        var taskId = getIdOfCreatedTask(postTaskResponse);

        var deleteRequest = taskRequestBuilder(taskId)
                .header(HEADER_AUTH, "dXNlcm5hbWU=:cGFzc3dvcmQ=")
                .DELETE()
                .build();
        var getRequest = taskRequestBuilder(taskId)
                .header(HEADER_AUTH, "dXNlcm5hbWU=:cGFzc3dvcmQ=")
                .GET()
                .build();

        //when
        var deleteResponse = httpClient.send(deleteRequest, HttpResponse.BodyHandlers.ofString());
        var getResponse = httpClient.send(getRequest, HttpResponse.BodyHandlers.ofString());

        //then
        assertThat(deleteResponse.statusCode()).as("Delete response status code").isEqualTo(OK.getCode());
        assertThat(getResponse.statusCode()).as("Get response status code").isEqualTo(NOT_FOUND.getCode());
    }

    private static HttpRequest.Builder taskRequestBuilder() {
        return HttpRequest.newBuilder(URI.create(PATH_TASK));
    }

    private static HttpRequest.Builder taskRequestBuilder(Object taskId) {
        return HttpRequest.newBuilder(URI.create(PATH_TASK + "/" + taskId.toString()));
    }

    private static HttpRequest.Builder userRequestBuilder() {
        return HttpRequest.newBuilder(URI.create(PATH_USER));
    }

    private String getIdOfCreatedTask(HttpResponse<String> taskResponse) {
        return taskResponse.body().split("\"")[3];
    }
}
