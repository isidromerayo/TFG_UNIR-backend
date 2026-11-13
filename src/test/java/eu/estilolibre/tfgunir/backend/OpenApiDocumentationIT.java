package eu.estilolibre.tfgunir.backend;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
@ActiveProfiles("test")
class OpenApiDocumentationIT {

    private static final String DOCS_USER = "swagger";
    private static final String DOCS_PASSWORD = "swagger-test";

    @Autowired
    private TestRestTemplate restTemplate;

    private TestRestTemplate docsRestTemplate() {
        return restTemplate.withBasicAuth(DOCS_USER, DOCS_PASSWORD);
    }

    @Test
    void apiDocsRequiresAuthentication() {
        ResponseEntity<String> response = restTemplate.getForEntity("/v3/api-docs", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void apiDocsRejectsWrongCredentials() {
        ResponseEntity<String> response = restTemplate
                .withBasicAuth(DOCS_USER, "contraseña-incorrecta")
                .getForEntity("/v3/api-docs", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void apiDocsEndpointReturns200() {
        ResponseEntity<String> response = docsRestTemplate().getForEntity("/v3/api-docs", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void apiDocsResponseContainsOpenApiStructure() {
        ResponseEntity<String> response = docsRestTemplate().getForEntity("/v3/api-docs", String.class);

        assertThat(response.getBody())
            .contains("\"openapi\"")
            .contains("\"info\"")
            .contains("\"paths\"");
    }

    @Test
    void apiDocsContainsApiInfoTitle() {
        ResponseEntity<String> response = docsRestTemplate().getForEntity("/v3/api-docs", String.class);

        assertThat(response.getBody()).contains("TFG UNIR Backend API");
    }

    @Test
    void swaggerUiReturnsOk() {
        ResponseEntity<String> response = docsRestTemplate().getForEntity("/swagger-ui.html", String.class);

        assertThat(response.getStatusCode())
            .as("Body: " + response.getBody())
            .isEqualTo(HttpStatus.OK);
    }

    @Test
    void swaggerUiResourcesAccessible() {
        ResponseEntity<String> response = docsRestTemplate().getForEntity("/swagger-ui/index.html", String.class);

        assertThat(response.getStatusCode())
            .as("Body: " + response.getBody())
            .isEqualTo(HttpStatus.OK);
    }
}
