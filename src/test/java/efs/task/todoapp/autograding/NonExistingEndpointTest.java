package efs.task.todoapp.autograding;

import efs.task.todoapp.util.ToDoServerExtension;
import efs.task.todoapp.web.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;

import static efs.task.todoapp.autograding.TestUtil.PATH_ROOT;
import static efs.task.todoapp.web.HttpStatus.NOT_FOUND;
import static java.net.http.HttpResponse.BodyHandlers.ofString;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(ToDoServerExtension.class)
class NonExistingEndpointTest {

    private HttpClient httpClient;

    @BeforeEach
    void setUp() {
        httpClient = HttpClient.newHttpClient();
    }

    @Test
    @Timeout(1)
    void shouldReturnNotFoundStatusForUnhandledPaths() throws IOException, InterruptedException {
        //given
        var httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(PATH_ROOT + "non/exisiting/endpoint"))
                .GET()
                .build();

        //when
        var httpResponse = httpClient.send(httpRequest, ofString());

        //then
        assertThat(httpResponse.statusCode()).as("Response status code").isEqualTo(NOT_FOUND.getCode());
    }
}