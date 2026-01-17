package eu.estilolibre.tfgunir.backend.rest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;

import eu.estilolibre.tfgunir.backend.controller.FormUser;
import io.restassured.RestAssured;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LoginControllerIT {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setup() {
        RestAssured.port = port;
    }

    @Test
    void shouldLoginSuccessfullyWithValidCredentials() {
        FormUser user = new FormUser();
        user.setEmail("admin@gmail.com");
        user.setPassword("admin");

        given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(user)
        .when()
            .post("/api/auth")
        .then()
            .statusCode(200)
            .body(containsString("\"token\":"));
    }

    @Test
    void shouldFailLoginWithInvalidCredentials() {
        FormUser user = new FormUser();
        user.setEmail("invalid@gmail.com");
        user.setPassword("invalid");

        given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(user)
        .when()
            .post("/api/auth")
        .then()
            .statusCode(401);
    }

    @Test
    void shouldFailLoginWithNonexistentUser() {
        FormUser user = new FormUser();
        user.setEmail("nonexistent@gmail.com");
        user.setPassword("password123");

        given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(user)
        .when()
            .post("/api/auth")
        .then()
            .statusCode(401)
            .body(containsString("no autorizado"));
    }

    @Test
    void shouldFailLoginWithWrongPassword() {
        FormUser user = new FormUser();
        user.setEmail("admin@gmail.com");
        user.setPassword("wrongpassword");

        given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(user)
        .when()
            .post("/api/auth")
        .then()
            .statusCode(401)
            .body(containsString("no autorizado"));
    }

    @Test
    void shouldReturnTokenWithCorrectStructure() {
        FormUser user = new FormUser();
        user.setEmail("admin@gmail.com");
        user.setPassword("admin");

        given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(user)
        .when()
            .post("/api/auth")
        .then()
            .statusCode(200)
            .body(containsString("\"token\":"))
            .body(containsString("\"username\":"))
            .body(containsString("\"fullname\":"))
            .body(containsString("\"id\":"));
    }

    @Test
    void shouldReturnCorrectUserDataOnSuccessfulLogin() {
        FormUser user = new FormUser();
        user.setEmail("admin@gmail.com");
        user.setPassword("admin");

        given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(user)
        .when()
            .post("/api/auth")
        .then()
            .statusCode(200)
            .body(containsString("admin@gmail.com"));
    }
}
