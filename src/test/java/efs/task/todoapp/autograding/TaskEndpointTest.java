package efs.task.todoapp.autograding;

import efs.task.todoapp.util.ToDoServerExtension;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Stream;

import static efs.task.todoapp.autograding.TestUtils.HEADER_AUTH;
import static efs.task.todoapp.autograding.TestUtils.taskJson;
import static efs.task.todoapp.autograding.TestUtils.taskRequestBuilder;
import static efs.task.todoapp.autograding.TestUtils.userJson;
import static efs.task.todoapp.autograding.TestUtils.validUUID;
import static efs.task.todoapp.autograding.HttpResonseStatus.BAD_REQUEST;
import static efs.task.todoapp.autograding.HttpResonseStatus.CREATED;
import static efs.task.todoapp.autograding.HttpResonseStatus.FORBIDDEN;
import static efs.task.todoapp.autograding.HttpResonseStatus.NOT_FOUND;
import static efs.task.todoapp.autograding.HttpResonseStatus.OK;
import static efs.task.todoapp.autograding.HttpResonseStatus.UNAUTHORIZED;
import static java.net.http.HttpRequest.BodyPublishers.ofString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;
import static org.skyscreamer.jsonassert.JSONCompareMode.STRICT;

@ExtendWith(ToDoServerExtension.class)
class TaskEndpointTest {

    private static final UUID ID = UUID.randomUUID();
    private static final String BASE_64_USERNAME = "dXNlcm5hbWU=";
    private static final String BASE_64_USERNAME_2 = "dXNlcm5hbWUy";
    private static final String BASE_64_PASSWORD = "cGFzc3dvcmQ=";
    private static final String BASE_64_WRONG_PASSWORD = "d3JvbmdQYXNzd29yZA==";

    private HttpClient httpClient;

    @BeforeEach
    void setUp() {
        httpClient = HttpClient.newHttpClient();
    }

    private static Stream<HttpRequest.Builder> badRequests() {
        return Stream.of(
                // Missing or bad auth header
                taskRequestBuilder()
                        .POST(ofString(taskJson("task"))),
                taskRequestBuilder()
                        .header(HEADER_AUTH, BASE_64_USERNAME)
                        .POST(ofString(taskJson("task"))),
                taskRequestBuilder()
                        .header(HEADER_AUTH, "dummy:" + BASE_64_PASSWORD)
                        .POST(ofString(taskJson("task"))),
                taskRequestBuilder()
                        .header(HEADER_AUTH, BASE_64_USERNAME + ":dummy")
                        .POST(ofString(taskJson("task"))),
                taskRequestBuilder()
                        .GET(),
                taskRequestBuilder()
                        .header(HEADER_AUTH, BASE_64_USERNAME)
                        .GET(),
                taskRequestBuilder()
                        .header(HEADER_AUTH, "dummy:" + BASE_64_PASSWORD)
                        .GET(),
                taskRequestBuilder()
                        .header(HEADER_AUTH, BASE_64_USERNAME + ":dummy")
                        .GET(),
                taskRequestBuilder(ID)
                        .PUT(ofString(taskJson("task"))),
                taskRequestBuilder(ID)
                        .header(HEADER_AUTH, BASE_64_USERNAME)
                        .PUT(ofString(taskJson("task"))),
                taskRequestBuilder(ID)
                        .header(HEADER_AUTH, "dummy:" + BASE_64_PASSWORD)
                        .PUT(ofString(taskJson("task"))),
                taskRequestBuilder(ID)
                        .header(HEADER_AUTH, BASE_64_USERNAME + ":dummy")
                        .PUT(ofString(taskJson("task"))),
                taskRequestBuilder()
                        .DELETE(),
                taskRequestBuilder()
                        .header(HEADER_AUTH, BASE_64_USERNAME)
                        .DELETE(),
                taskRequestBuilder()
                        .header(HEADER_AUTH, "dummy:" + BASE_64_PASSWORD)
                        .DELETE(),
                taskRequestBuilder()
                        .header(HEADER_AUTH, BASE_64_USERNAME + ":dummy")
                        .DELETE(),

                // Missing or bad task body
                taskRequestBuilder()
                        .header(HEADER_AUTH, BASE_64_USERNAME + ":" + BASE_64_PASSWORD)
                        .POST(ofString("")),
                taskRequestBuilder()
                        .header(HEADER_AUTH, BASE_64_USERNAME + ":" + BASE_64_PASSWORD)
                        .POST(ofString(taskJson(null))),
                taskRequestBuilder()
                        .header(HEADER_AUTH, BASE_64_USERNAME + ":" + BASE_64_PASSWORD)
                        .POST(ofString(taskJson("buy milk", "not-a-date"))),
                taskRequestBuilder(ID)
                        .header(HEADER_AUTH, BASE_64_USERNAME + ":" + BASE_64_PASSWORD)
                        .PUT(ofString("")),
                taskRequestBuilder(ID)
                        .header(HEADER_AUTH, BASE_64_USERNAME + ":" + BASE_64_PASSWORD)
                        .PUT(ofString(taskJson(null))),
                taskRequestBuilder(ID)
                        .header(HEADER_AUTH, BASE_64_USERNAME + ":" + BASE_64_PASSWORD)
                        .PUT(ofString(taskJson("buy milk", "not-a-date"))),

                // Missing or invalid task id in path
                taskRequestBuilder("123")
                        .header(HEADER_AUTH, BASE_64_USERNAME + ":" + BASE_64_PASSWORD)
                        .GET(),
                taskRequestBuilder()
                        .header(HEADER_AUTH, BASE_64_USERNAME + ":" + BASE_64_PASSWORD)
                        .PUT(ofString(taskJson("task"))),
                taskRequestBuilder("123")
                        .header(HEADER_AUTH, BASE_64_USERNAME + ":" + BASE_64_PASSWORD)
                        .PUT(ofString(taskJson("task"))),
                taskRequestBuilder()
                        .header(HEADER_AUTH, BASE_64_USERNAME + ":" + BASE_64_PASSWORD)
                        .DELETE(),
                taskRequestBuilder("123")
                        .header(HEADER_AUTH, BASE_64_USERNAME + ":" + BASE_64_PASSWORD)
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
                // Wrong username
                taskRequestBuilder()
                        .header(HEADER_AUTH, BASE_64_USERNAME_2 + ":" + BASE_64_PASSWORD)
                        .POST(ofString(taskJson("task"))),
                taskRequestBuilder()
                        .header(HEADER_AUTH, BASE_64_USERNAME_2 + ":" + BASE_64_PASSWORD)
                        .GET(),
                taskRequestBuilder(ID)
                        .header(HEADER_AUTH, BASE_64_USERNAME_2 + ":" + BASE_64_PASSWORD)
                        .GET(),
                taskRequestBuilder(ID)
                        .header(HEADER_AUTH, BASE_64_USERNAME_2 + ":" + BASE_64_PASSWORD)
                        .PUT(ofString(taskJson("task"))),
                taskRequestBuilder(ID)
                        .header(HEADER_AUTH, BASE_64_USERNAME_2 + ":" + BASE_64_PASSWORD)
                        .DELETE(),

                //Wrong password
                taskRequestBuilder()
                        .header(HEADER_AUTH, BASE_64_USERNAME + ":" + BASE_64_WRONG_PASSWORD)
                        .POST(ofString(taskJson("task"))),
                taskRequestBuilder()
                        .header(HEADER_AUTH, BASE_64_USERNAME + ":" + BASE_64_WRONG_PASSWORD)
                        .GET(),
                taskRequestBuilder(ID)
                        .header(HEADER_AUTH, BASE_64_USERNAME + ":" + BASE_64_WRONG_PASSWORD)
                        .GET(),
                taskRequestBuilder(ID)
                        .header(HEADER_AUTH, BASE_64_USERNAME + ":" + BASE_64_WRONG_PASSWORD)
                        .PUT(ofString(taskJson("task"))),
                taskRequestBuilder(ID)
                        .header(HEADER_AUTH, BASE_64_USERNAME + ":" + BASE_64_WRONG_PASSWORD)
                        .DELETE()
        );
    }

    @ParameterizedTest
    @MethodSource("unauthorizedRequests")
    @Timeout(1)
    void shouldReturnUnauthorizedStatus(HttpRequest.Builder httpRequestBuilder) throws IOException, InterruptedException {
        //given
        var userRequest = TestUtils.userRequestBuilder()
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
    void shouldCreateTask() throws IOException, InterruptedException, JSONException {
        //given
        var userRequest = TestUtils.userRequestBuilder()
                .POST(ofString(userJson("username", "password")))
                .build();
        httpClient.send(userRequest, HttpResponse.BodyHandlers.ofString());

        var postTaskRequest = taskRequestBuilder()
                .header(HEADER_AUTH, BASE_64_USERNAME +
                        ":" +
                        BASE_64_PASSWORD)
                .POST(ofString(taskJson("buy milk", "2021-06-30")))
                .build();

        //when
        var postTaskResponse = httpClient.send(postTaskRequest, HttpResponse.BodyHandlers.ofString());
        var jsonResponse = new JSONObject(postTaskResponse.body());

        //then
        assertThat(postTaskResponse.statusCode()).as("Response status code").isEqualTo(CREATED.getCode());
        assertThat(jsonResponse.getString("id")).as("Identifier in response").is(validUUID());
    }

    @Test
    @Timeout(1)
    void shouldReturnAllTasksForUser() throws IOException, InterruptedException, JSONException {
        //given
        var user1Request = TestUtils.userRequestBuilder()
                .POST(ofString(userJson("username", "password")))
                .build();
        httpClient.send(user1Request, HttpResponse.BodyHandlers.ofString());

        var user2Request = TestUtils.userRequestBuilder()
                .POST(ofString(userJson("username2", "password")))
                .build();
        httpClient.send(user2Request, HttpResponse.BodyHandlers.ofString());

        var postTaskForUser1Request = taskRequestBuilder()
                .header(HEADER_AUTH, BASE_64_USERNAME +
                        ":" +
                        BASE_64_PASSWORD)
                .POST(ofString(taskJson("buy milk", "2021-06-30")))
                .build();
        var postTaskForUser1RequestResponse = httpClient.send(postTaskForUser1Request, HttpResponse.BodyHandlers.ofString());
        var task1Id = getIdOfCreatedTask(postTaskForUser1RequestResponse);

        postTaskForUser1Request = taskRequestBuilder()
                .header(HEADER_AUTH, BASE_64_USERNAME +
                        ":" +
                        BASE_64_PASSWORD)
                .POST(ofString(taskJson("eat an apple")))
                .build();
        postTaskForUser1RequestResponse = httpClient.send(postTaskForUser1Request, HttpResponse.BodyHandlers.ofString());
        var task2Id = getIdOfCreatedTask(postTaskForUser1RequestResponse);

        var postTaskForUser2Request = taskRequestBuilder()
                .header(HEADER_AUTH, BASE_64_USERNAME_2 + ":" + BASE_64_PASSWORD)
                .POST(ofString(taskJson("buy milk", "2021-06-30")))
                .build();
        httpClient.send(postTaskForUser2Request, HttpResponse.BodyHandlers.ofString());

        var getForUser1Request = taskRequestBuilder()
                .header(HEADER_AUTH, BASE_64_USERNAME +
                        ":" +
                        BASE_64_PASSWORD)
                .GET()
                .build();

        //when
        var getForUser1Response = httpClient.send(getForUser1Request, HttpResponse.BodyHandlers.ofString());
        var jsonTasks = new JSONArray(getForUser1Response.body());

        //then
        assertThat(getForUser1Response.statusCode()).as("Response status code").isEqualTo(OK.getCode());

        assertThat(jsonTasks.length()).as("Number of tasks").isEqualTo(2);

        var sortedTasks = sortJsonTasks(task1Id, jsonTasks);

        assertEquals("Task in response",
                "{" +
                        "\"id\" : \"" + task1Id + "\"," +
                        "\"description\" : \"buy milk\"," +
                        "\"due\" : \"2021-06-30\"" +
                        "}",
                sortedTasks[0],
                STRICT);
        assertEquals("Task in response",
                "{" +
                        "\"id\" : \"" + task2Id + "\"," +
                        "\"description\" : \"eat an apple\"" +
                        "}",
                sortedTasks[1],
                STRICT);
    }

    private JSONObject[] sortJsonTasks(String task1Id, JSONArray jsonTasks) throws JSONException {
        var jsonTask1 = (JSONObject) jsonTasks.get(1);
        var jsonTask2 = (JSONObject) jsonTasks.get(0);

        var sortedTasks = new JSONObject[2];

        if (task1Id.equals(jsonTask1.getString("id"))) {
            sortedTasks[0] = jsonTask1;
            sortedTasks[1] = jsonTask2;
        } else {
            sortedTasks[0] = jsonTask2;
            sortedTasks[1] = jsonTask1;
        }

        return sortedTasks;
    }

    @Test
    @Timeout(1)
    void shouldReturnTask() throws IOException, InterruptedException, JSONException {
        //given
        var userRequest = TestUtils.userRequestBuilder()
                .POST(ofString(userJson("username", "password")))
                .build();
        httpClient.send(userRequest, HttpResponse.BodyHandlers.ofString());

        var postTaskRequest = taskRequestBuilder()
                .header(HEADER_AUTH, BASE_64_USERNAME + ":" + BASE_64_PASSWORD)
                .POST(ofString(taskJson("buy milk", "2021-06-30")))
                .build();
        var postTaskResponse = httpClient.send(postTaskRequest, HttpResponse.BodyHandlers.ofString());
        var taskId = getIdOfCreatedTask(postTaskResponse);

        var getRequest = taskRequestBuilder(taskId)
                .header(HEADER_AUTH, BASE_64_USERNAME + ":" + BASE_64_PASSWORD)
                .GET()
                .build();

        //when
        var getResponse = httpClient.send(getRequest, HttpResponse.BodyHandlers.ofString());
        var jsonTask = new JSONObject(getResponse.body());

        //then
        assertThat(getResponse.statusCode()).as("Response status code").isEqualTo(OK.getCode());

        assertEquals("Task in response",
                "{" +
                        "\"id\" : \"" + taskId + "\"," +
                        "\"description\" : \"buy milk\"," +
                        "\"due\" : \"2021-06-30\"" +
                        "}",
                jsonTask,
                STRICT);
    }

    private static Stream<HttpRequest.Builder> notFoundRequests() {
        return Stream.of(
                taskRequestBuilder(ID)
                        .header(HEADER_AUTH, BASE_64_USERNAME + ":" + BASE_64_PASSWORD)
                        .GET(),
                taskRequestBuilder(ID)
                        .header(HEADER_AUTH, BASE_64_USERNAME + ":" + BASE_64_PASSWORD)
                        .PUT(ofString(taskJson("task"))),
                taskRequestBuilder(ID)
                        .header(HEADER_AUTH, BASE_64_USERNAME + ":" + BASE_64_PASSWORD)
                        .DELETE()
        );
    }

    @ParameterizedTest
    @MethodSource("notFoundRequests")
    @Timeout(1)
    void shouldReturnNotFound(HttpRequest.Builder httpRequestBuilder) throws IOException, InterruptedException {
        //given
        var userRequest = TestUtils.userRequestBuilder()
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
                        .header(HEADER_AUTH, BASE_64_USERNAME_2 + ":" + BASE_64_PASSWORD)
                        .GET(),
                taskId -> taskRequestBuilder(taskId)
                        .header(HEADER_AUTH, BASE_64_USERNAME_2 + ":" + BASE_64_PASSWORD)
                        .PUT(ofString(taskJson("eat an apple"))),
                taskId -> taskRequestBuilder(taskId)
                        .header(HEADER_AUTH, BASE_64_USERNAME_2 + ":" + BASE_64_PASSWORD)
                        .DELETE()
        );
    }

    @ParameterizedTest
    @MethodSource("forbiddenRequests")
    @Timeout(1)
    void shouldReturnForbiddenStatus(Function<String, HttpRequest.Builder> requestBuilderForUser2Function) throws IOException, InterruptedException {
        //given
        var user1Request = TestUtils.userRequestBuilder()
                .POST(ofString(userJson("username", "password")))
                .build();
        httpClient.send(user1Request, HttpResponse.BodyHandlers.ofString());

        var user2Request = TestUtils.userRequestBuilder()
                .POST(ofString(userJson("username2", "password")))
                .build();
        httpClient.send(user2Request, HttpResponse.BodyHandlers.ofString());

        var postTaskForUser1Request = taskRequestBuilder()
                .header(HEADER_AUTH, BASE_64_USERNAME + ":" + BASE_64_PASSWORD)
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
    void shouldUpdateTask() throws IOException, InterruptedException, JSONException {
        //given
        var userRequest = TestUtils.userRequestBuilder()
                .POST(ofString(userJson("username", "password")))
                .build();
        httpClient.send(userRequest, HttpResponse.BodyHandlers.ofString());

        var postTaskRequest = taskRequestBuilder()
                .header(HEADER_AUTH, BASE_64_USERNAME + ":" + BASE_64_PASSWORD)
                .POST(ofString(taskJson("buy milk", "2021-06-30")))
                .build();
        var postTaskResponse = httpClient.send(postTaskRequest, HttpResponse.BodyHandlers.ofString());
        var taskId = getIdOfCreatedTask(postTaskResponse);

        var putRequest = taskRequestBuilder(taskId)
                .header(HEADER_AUTH, BASE_64_USERNAME + ":" + BASE_64_PASSWORD)
                .PUT(ofString(taskJson("eat an apple")))
                .build();
        var getRequest = taskRequestBuilder(taskId)
                .header(HEADER_AUTH, BASE_64_USERNAME + ":" + BASE_64_PASSWORD)
                .GET()
                .build();

        //when
        var putResponse = httpClient.send(putRequest, HttpResponse.BodyHandlers.ofString());
        var getResponse = httpClient.send(getRequest, HttpResponse.BodyHandlers.ofString());

        //then
        assertThat(putResponse.statusCode()).as("Put response status code").isEqualTo(OK.getCode());
        assertEquals("Put response body",
                "{" +
                        "\"id\" : \"" + taskId + "\"," +
                        "\"description\" : \"eat an apple\"" +
                        "}",
                putResponse.body(),
                STRICT);
        assertThat(getResponse.statusCode()).as("Get response status code").isEqualTo(OK.getCode());
        assertEquals("Get response body",
                "{" +
                        "\"id\" : \"" + taskId + "\"," +
                        "\"description\" : \"eat an apple\"" +
                        "}",
                putResponse.body(),
                STRICT);
    }

    @Test
    @Timeout(1)
    void shouldDeleteTask() throws IOException, InterruptedException {
        //given
        var userRequest = TestUtils.userRequestBuilder()
                .POST(ofString(userJson("username", "password")))
                .build();
        httpClient.send(userRequest, HttpResponse.BodyHandlers.ofString());

        var postTaskRequest = taskRequestBuilder()
                .header(HEADER_AUTH, BASE_64_USERNAME + ":" + BASE_64_PASSWORD)
                .POST(ofString(taskJson("buy milk", "2021-06-30")))
                .build();
        var postTaskResponse = httpClient.send(postTaskRequest, HttpResponse.BodyHandlers.ofString());
        var taskId = getIdOfCreatedTask(postTaskResponse);

        var deleteRequest = taskRequestBuilder(taskId)
                .header(HEADER_AUTH, BASE_64_USERNAME + ":" + BASE_64_PASSWORD)
                .DELETE()
                .build();
        var getRequest = taskRequestBuilder(taskId)
                .header(HEADER_AUTH, BASE_64_USERNAME + ":" + BASE_64_PASSWORD)
                .GET()
                .build();

        //when
        var deleteResponse = httpClient.send(deleteRequest, HttpResponse.BodyHandlers.ofString());
        var getResponse = httpClient.send(getRequest, HttpResponse.BodyHandlers.ofString());

        //then
        assertThat(deleteResponse.statusCode()).as("Delete response status code").isEqualTo(OK.getCode());
        assertThat(getResponse.statusCode()).as("Get response status code").isEqualTo(NOT_FOUND.getCode());
    }

    private String getIdOfCreatedTask(HttpResponse<String> taskResponse) {
        return taskResponse.body().split("\"")[3];
    }
}
