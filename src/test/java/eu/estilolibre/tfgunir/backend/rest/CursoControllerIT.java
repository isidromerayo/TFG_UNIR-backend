package eu.estilolibre.tfgunir.backend.rest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import io.restassured.RestAssured;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class CursoControllerIT {

    private static final String BASE_URI = "http://localhost";
 
    @LocalServerPort
    private int port;
 
    @BeforeEach
    public void configureRestAssured() {
        RestAssured.baseURI = BASE_URI;
        RestAssured.port = port;
    }

    @Test
    public void cursosAPIStatus() {
        given().
                when().
                get("/api/cursos").
                then().
                log().all().
                assertThat().
                statusCode(200).
                body("_embedded.cursos.size()", equalTo(6));
    }
}