package eu.estilolibre.tfgunir.backend.rest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import io.restassured.RestAssured;
import io.restassured.response.Response;

/**
 * Rate limiting de {@code POST /api/auth} (H-2). Usa límites reducidos y un
 * contexto propio ({@link TestPropertySource}) para no depender de esperas ni
 * afectar al resto de ITs.
 *
 * <p>El escenario es un único test porque el contador es por origen y estado
 * del contexto: partir el flujo en varios métodos haría el resultado dependiente
 * del orden de ejecución.</p>
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "auth.login.max-attempts=3",
        "auth.login.window=PT5M"
})
class LoginRateLimiterIT {

    private static final String BASE_URI = "http://localhost";

    @LocalServerPort
    private int port;

    @BeforeEach
    void configureRestAssured() {
        RestAssured.baseURI = BASE_URI;
        RestAssured.port = port;
    }

    @Test
    void elLoginCorrectoReiniciaElContadorYElLimiteBloqueaConRetryAfter() {
        // 1. Un login correcto entra y deja el contador a cero.
        login("admin@gmail.com", "admin")
                .then()
                .assertThat()
                .statusCode(200)
                .body("token", notNullValue());

        // 2. Tres fallos consecutivos agotan el límite configurado.
        for (int intento = 0; intento < 3; intento++) {
            login("admin@gmail.com", "mala" + intento)
                    .then()
                    .assertThat()
                    .statusCode(401);
        }

        // 3. Ya bloqueado: ni siquiera con credenciales correctas.
        login("admin@gmail.com", "admin")
                .then()
                .assertThat()
                .statusCode(429)
                .header("Retry-After", notNullValue())
                .body("code", equalTo("TOO_MANY_REQUESTS"));
    }

    private Response login(String email, String password) {
        return given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body("{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}")
                .when()
                .post("/api/auth");
    }
}
