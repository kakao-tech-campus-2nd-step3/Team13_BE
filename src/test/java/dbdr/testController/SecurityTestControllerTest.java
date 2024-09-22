package dbdr.testController;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.client.RestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SecurityTestControllerTest {

    @LocalServerPort
    private int port;

    @Test
    void loginSuccess() {

        RestClient restClient = RestClient.create();

        ResponseEntity<String> response = restClient.get()
            .uri("http://localhost:" + port + "/loginSuccess"+"?username=guardian")
            .retrieve()

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}