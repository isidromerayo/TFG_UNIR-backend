package eu.estilolibre.tfgunir.backend.rest;


import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.Disabled;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;


import io.restassured.RestAssured;

import static io.restassured.RestAssured.given;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CategoriaControllerIT {

    private final static String BASE_URI = "http://localhost";
 
    @LocalServerPort
    private int port;
 
    @Before(value = "")
    public void configureRestAssured() {
        RestAssured.baseURI = BASE_URI;
        RestAssured.port = port;
    }

    @Disabled("Pendiente de encontrar ejemplo con Spring Boot 3")
    public void categoriasAPIStatus() {
        given().
                when().
                get("/api/categorias").
                then().
                log().all().
                assertThat().
                statusCode(200);
    }
}
