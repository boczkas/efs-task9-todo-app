package efs.task.todoapp;

import com.google.gson.Gson;
import efs.task.todoapp.web.UserBody;
import efs.task.todoapp.util.TODOServerExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;

import static java.net.http.HttpRequest.BodyPublishers.ofString;
import static java.net.http.HttpResponse.BodyHandlers.ofString;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(TODOServerExtension.class)
class UserEndpointTest {

    private static final int CREATED = 201;

    private final Gson gson = new Gson();

    private HttpClient httpClient;

    @BeforeEach
    void setUp() {
        httpClient = HttpClient.newHttpClient();
    }

    @Test
    @Timeout(1)
    void shouldReturnBadRequestErrorWhenMissingBody() throws IOException, InterruptedException {
        //given
        var user = new UserBody("janKowalski", "am!sK#123");

        var httpRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/todo/user"))
                .POST(ofString(gson.toJson(user)))
                .build();

        //when
        var httpResponse = httpClient.send(httpRequest, ofString());

        //then
        assertThat(httpResponse.statusCode()).as("Response status code").isEqualTo(CREATED);
    }
}