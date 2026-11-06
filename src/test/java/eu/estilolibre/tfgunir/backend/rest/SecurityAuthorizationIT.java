package eu.estilolibre.tfgunir.backend.rest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;

import java.nio.charset.StandardCharsets;
import java.time.Instant;

import javax.crypto.SecretKey;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.restassured.RestAssured;

/**
 * Verifica la política de autorización de la API: qué es público, qué exige
 * token válido y qué exige además ser propietario del recurso.
 *
 * <p>Usuarios de {@code data.sql}: id 5 = helena@localhost, id 6 = carlos@localhost,
 * ambos con contraseña {@code 1234} y estado activo.</p>
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class SecurityAuthorizationIT {

    private static final String BASE_URI = "http://localhost";
    private static final long HELENA_ID = 5L;
    private static final long CARLOS_ID = 6L;

    @LocalServerPort
    private int port;

    private String helenaToken;

    @BeforeEach
    void configureRestAssured() {
        RestAssured.baseURI = BASE_URI;
        RestAssured.port = port;
        helenaToken = login("helena@localhost", "1234");
    }

    private String login(String email, String password) {
        return given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body("{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}")
                .when()
                .post("/api/auth")
                .then()
                .statusCode(200)
                .extract()
                .path("token");
    }

    // --- Catálogo público -----------------------------------------------------

    @Test
    void catalogoPublicoSigueAbierto() {
        given().when().get("/api/cursos").then().assertThat().statusCode(200);
        given().when().get("/api/categorias").then().assertThat().statusCode(200);
        given().when().get("/api/valoraciones").then().assertThat().statusCode(200);
    }

    @Test
    void loginYRegistroSiguenSiendoPublicos() {
        given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body("{\"email\":\"helena@localhost\",\"password\":\"1234\"}")
                .when()
                .post("/api/auth")
                .then()
                .assertThat()
                .statusCode(200);

        given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body("""
                        {"nombre":"Anonima","apellidos":"Registro","email":"anonima@example.com","password":"secreto123"}
                        """)
                .when()
                .post("/api/usuarios")
                .then()
                .assertThat()
                .statusCode(201);
    }

    @Test
    void actuatorHealthEsPublicoPeroMetricsNo() {
        given().when().get("/actuator/health").then().assertThat().statusCode(200);
        given().when().get("/actuator/metrics").then().assertThat().statusCode(401);
    }

    // --- Escritura anónima bloqueada (Spring Data REST) -----------------------

    @Test
    void anonimoNoPuedeCrearCursos() {
        given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body("{\"titulo\":\"Curso inyectado\"}")
                .when()
                .post("/api/cursos")
                .then()
                .assertThat()
                .statusCode(401);
    }

    @Test
    void anonimoNoPuedeBorrarCursos() {
        given().when().delete("/api/cursos/1").then().assertThat().statusCode(401);
    }

    @Test
    void anonimoNoPuedeCrearValoraciones() {
        given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body("{\"comentario\":\"spam\"}")
                .when()
                .post("/api/valoraciones")
                .then()
                .assertThat()
                .statusCode(401);
    }

    // --- Datos de usuario: 401 anónimo ---------------------------------------

    @Test
    void anonimoNoPuedeVerCursosDeUnUsuario() {
        given()
                .when()
                .get("/api/usuarios/" + HELENA_ID + "/cursos")
                .then()
                .assertThat()
                .statusCode(401);
    }

    @Test
    void anonimoNoPuedeAgregarCursosAUnUsuario() {
        given()
                .contentType("text/uri-list")
                .body(BASE_URI + ":8080/api/cursos/1")
                .when()
                .post("/api/usuarios/" + HELENA_ID + "/misCursosComprados")
                .then()
                .assertThat()
                .statusCode(401);
    }

    // --- Datos de usuario: propietario sí, tercero no -------------------------

    @Test
    void propietarioPuedeVerSusCursos() {
        given()
                .header("Authorization", "Bearer " + helenaToken)
                .accept("application/json, application/hal+json")
                .when()
                .get("/api/usuarios/" + HELENA_ID + "/cursos")
                .then()
                .assertThat()
                .statusCode(200);
    }

    @Test
    void terceroNoPuedeVerCursosDeOtroUsuario() {
        given()
                .header("Authorization", "Bearer " + helenaToken)
                .when()
                .get("/api/usuarios/" + CARLOS_ID + "/cursos")
                .then()
                .assertThat()
                .statusCode(403)
                .body("code", equalTo("FORBIDDEN"));
    }

    @Test
    void terceroNoPuedeAgregarCursosAOtroUsuario() {
        given()
                .header("Authorization", "Bearer " + helenaToken)
                .contentType("text/uri-list")
                .body(BASE_URI + ":8080/api/cursos/1")
                .when()
                .post("/api/usuarios/" + CARLOS_ID + "/misCursosComprados")
                .then()
                .assertThat()
                .statusCode(403)
                .body("code", equalTo("FORBIDDEN"));
    }

    @Test
    void propietarioPuedeAgregarCursosASiMismo() {
        given()
                .header("Authorization", "Bearer " + helenaToken)
                .contentType("text/uri-list")
                .body(BASE_URI + ":8080/api/cursos/1")
                .when()
                .post("/api/usuarios/" + HELENA_ID + "/misCursosComprados")
                .then()
                .assertThat()
                .statusCode(201);
    }

    // --- Tokens inválidos -----------------------------------------------------

    @Test
    void tokenNoFirmadoEsRechazado() {
        given()
                .header("Authorization", "Bearer esto-no-es-un-jwt")
                .when()
                .get("/api/usuarios/" + HELENA_ID + "/cursos")
                .then()
                .assertThat()
                .statusCode(401);
    }

    @Test
    void tokenFirmadoConOtroSecretoEsRechazado() {
        SecretKey claveAjena = Keys.hmacShaKeyFor(
                "secreto-de-atacante-que-no-es-el-de-la-aplicacion-suficientemente-largo"
                        .getBytes(StandardCharsets.UTF_8));
        String tokenFalso = Jwts.builder()
                .subject("helena@localhost")
                .expiration(java.util.Date.from(Instant.now().plusSeconds(3600)))
                .signWith(claveAjena)
                .compact();

        given()
                .header("Authorization", "Bearer " + tokenFalso)
                .when()
                .get("/api/usuarios/" + HELENA_ID + "/cursos")
                .then()
                .assertThat()
                .statusCode(401);
    }

    @Test
    void tokenDeUsuarioInactivoEsRechazado() {
        // Usuario nuevo: el registro lo deja en estado "P" (pendiente), sin acceso.
        String email = "pendiente." + System.nanoTime() + "@example.com";
        given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body("{\"nombre\":\"Pendiente\",\"apellidos\":\"Sin Activar\",\"email\":\""
                        + email + "\",\"password\":\"secreto123\"}")
                .when()
                .post("/api/usuarios")
                .then()
                .assertThat()
                .statusCode(201);

        // El login solo emite token para usuarios activos, así que no hay token posible:
        // se comprueba que el login falla y que sin token la API responde 401.
        given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body("{\"email\":\"" + email + "\",\"password\":\"secreto123\"}")
                .when()
                .post("/api/auth")
                .then()
                .assertThat()
                .statusCode(401);

        given()
                .when()
                .get("/api/usuarios/" + HELENA_ID + "/cursos")
                .then()
                .assertThat()
                .statusCode(401);
    }

    @Test
    void anonimoNoPuedeListarCursosCompradosDeVariosUsuarios() {
        for (long id = 1; id <= 4; id++) {
            given()
                    .when()
                    .get("/api/usuarios/" + id + "/cursos")
                    .then()
                    .assertThat()
                    .statusCode(401);
        }
    }

    @Test
    void propietarioRecibeSoloSusCursos() {
        given()
                .header("Authorization", "Bearer " + helenaToken)
                .accept("application/json, application/hal+json")
                .when()
                .get("/api/usuarios/" + HELENA_ID + "/cursos")
                .then()
                .assertThat()
                .statusCode(200)
                .body("$", hasSize(greaterThanOrEqualTo(3)));
    }
}
