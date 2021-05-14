package efs.task.todoapp.autograding;

import efs.task.todoapp.util.ToDoServerExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.net.http.HttpClient;
import java.util.stream.Stream;

import static efs.task.todoapp.autograding.TestUtils.userJson;
import static efs.task.todoapp.autograding.TestUtils.wrongCodeMessage;
import static efs.task.todoapp.autograding.HttpResonseStatus.BAD_REQUEST;
import static efs.task.todoapp.autograding.HttpResonseStatus.CONFLICT;
import static efs.task.todoapp.autograding.HttpResonseStatus.CREATED;
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

    @ParameterizedTest(name = "Request body = {0}")
    @MethodSource("badRequestBody")
    @Timeout(1)
    void shouldReturnBadRequestStatus(String body) throws IOException, InterruptedException {
        //given
        var httpRequest = TestUtils.userRequestBuilder()
                .POST(ofString(body))
                .build();

        //when
        var httpResponse = httpClient.send(httpRequest, ofString());

        //then
        assertThat(httpResponse.statusCode()).as(() -> wrongCodeMessage(httpRequest)).isEqualTo(BAD_REQUEST.getCode());
    }

    @Test
    @Timeout(1)
    void shouldReturnCreatedStatus() throws IOException, InterruptedException {
        //given
        var httpRequest = TestUtils.userRequestBuilder()
                .POST(ofString(userJson("username", "password")))
                .build();

        //when
        var httpResponse = httpClient.send(httpRequest, ofString());

        //then
        assertThat(httpResponse.statusCode()).as(() -> wrongCodeMessage(httpRequest)).isEqualTo(CREATED.getCode());
    }

    @Test
    @Timeout(1)
    void shouldReturnConflictStatus() throws IOException, InterruptedException {
        //given
        var httpRequest = TestUtils.userRequestBuilder()
                .POST(ofString(userJson("username", "password")))
                .build();

        httpClient.send(httpRequest, ofString());

        //when
        var httpResponse = httpClient.send(httpRequest, ofString());

        //then
        assertThat(httpResponse.statusCode())
                .as(() -> "Creation of a user with the existing username. " + wrongCodeMessage(httpRequest))
                .isEqualTo(CONFLICT.getCode());
    }
}