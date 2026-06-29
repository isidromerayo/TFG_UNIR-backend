package eu.estilolibre.tfgunir.backend;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class OpenApiDocumentationIT {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void apiDocsEndpointReturns200() {
        ResponseEntity<String> response = restTemplate.getForEntity("/v3/api-docs", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void apiDocsResponseContainsOpenApiStructure() {
        ResponseEntity<String> response = restTemplate.getForEntity("/v3/api-docs", String.class);

        assertThat(response.getBody())
            .contains("\"openapi\"")
            .contains("\"info\"")
            .contains("\"paths\"");
    }

    @Test
    void swaggerUiReturnsOk() {
        ResponseEntity<String> response = restTemplate.getForEntity("/swagger-ui.html", String.class);

        assertThat(response.getStatusCode())
            .as("Body: " + response.getBody())
            .isEqualTo(HttpStatus.OK);
    }

    @Test
    void swaggerUiResourcesAccessible() {
        ResponseEntity<String> response = restTemplate.getForEntity("/swagger-ui/index.html", String.class);

        assertThat(response.getStatusCode())
            .as("Body: " + response.getBody())
            .isEqualTo(HttpStatus.OK);
    }
}
