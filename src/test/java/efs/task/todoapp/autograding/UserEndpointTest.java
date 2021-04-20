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
import java.util.stream.Stream;

import static efs.task.todoapp.autograding.TestUtil.PATH_USER;
import static efs.task.todoapp.autograding.TestUtil.userJson;
import static efs.task.todoapp.web.HttpStatus.BAD_REQUEST;
import static efs.task.todoapp.web.HttpStatus.CONFLICT;
import static efs.task.todoapp.web.HttpStatus.CREATED;
import static java.net.http.HttpRequest.BodyPublishers.ofString;
import static java.net.http.HttpResponse.BodyHandlers.ofString;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(ToDoServerExtension.class)
class UserEndpointTest {

    private HttpClient httpClient;

    @BeforeEach
    void setUp() {
        httpClient = HttpClient.newHttpClient();
    }

    private static Stream<String> badRequestBody() {
        return Stream.of("",
                userJson("username", null),
                userJson("username", ""),
                userJson(null, "password"),
                userJson("", "password")
        );
    }

    @ParameterizedTest
    @MethodSource("badRequestBody")
    @Timeout(1)
    void shouldReturnBadRequestStatus(String body) throws IOException, InterruptedException {
        //given
        var httpRequest = userRequestBuilder()
                .POST(ofString(body))
                .build();

        //when
        var httpResponse = httpClient.send(httpRequest, ofString());

        //then
        assertThat(httpResponse.statusCode()).as("Response status code").isEqualTo(BAD_REQUEST.getCode());
    }

    @Test
    @Timeout(1)
    void shouldReturnCreatedStatus() throws IOException, InterruptedException {
        //given
        var httpRequest = userRequestBuilder()
                .POST(ofString(userJson("username", "password")))
                .build();

        //when
        var httpResponse = httpClient.send(httpRequest, ofString());

        //then
        assertThat(httpResponse.statusCode()).as("Response status code").isEqualTo(CREATED.getCode());
    }

    @Test
    @Timeout(1)
    void shouldReturnConflictStatus() throws IOException, InterruptedException {
        //given
        var httpRequest = userRequestBuilder()
                .POST(ofString(userJson("username", "password")))
                .build();

        httpClient.send(httpRequest, ofString());

        //when
        var httpResponse = httpClient.send(httpRequest, ofString());

        //then
        assertThat(httpResponse.statusCode()).as("Response status code").isEqualTo(CONFLICT.getCode());
    }

    private HttpRequest.Builder userRequestBuilder() {
        return HttpRequest.newBuilder(URI.create(PATH_USER));
    }
}