package eu.estilolibre.tfgunir.backend.rest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;

import io.restassured.RestAssured;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class UsuarioControllerIT {

    private static final String BASE_URI = "http://localhost";

    @LocalServerPort
    private int port;

    @BeforeEach
    void configureRestAssured() {
        RestAssured.baseURI = BASE_URI;
        RestAssured.port = port;
    }

    @Test
    void usuariosApiIsNotExposed_returns404() {
        given().
                when().
                get("/api/usuarios").
                then().
                assertThat().
                statusCode(404);
    }

    @Test
    void registrarUsuario_devuelve201_conUsuarioResponse() {
        String body = """
                {
                  "nombre": "Nueva",
                  "apellidos": "Usuaria",
                  "email": "nueva@example.com",
                  "password": "secreto"
                }
                """;

        given().
                contentType(MediaType.APPLICATION_JSON_VALUE).
                body(body).
        when().
                post("/api/usuarios").
        then().
                assertThat().
                statusCode(201).
                body("id", notNullValue()).
                body("nombre", org.hamcrest.Matchers.equalTo("Nueva")).
                body("email", org.hamcrest.Matchers.equalTo("nueva@example.com")).
                body("estado", org.hamcrest.Matchers.equalTo("P"));
    }

    @Test
    void registrarUsuario_conEmailDuplicado_devuelve409() {
        String body = """
                {
                  "nombre": "Otra",
                  "apellidos": "Usuaria",
                  "email": "maria@localhost",
                  "password": "secreto"
                }
                """;

        given().
                contentType(MediaType.APPLICATION_JSON_VALUE).
                body(body).
        when().
                post("/api/usuarios").
        then().
                assertThat().
                statusCode(409).
                body("code", org.hamcrest.Matchers.equalTo("EMAIL_ALREADY_EXISTS"));
    }

    @Test
    void registrarUsuario_conDatosInvalidos_devuelve400() {
        String body = """
                {
                  "nombre": "",
                  "apellidos": "Usuaria",
                  "email": "email-no-valido",
                  "password": "12"
                }
                """;

        given().
                contentType(MediaType.APPLICATION_JSON_VALUE).
                body(body).
        when().
                post("/api/usuarios").
        then().
                assertThat().
                statusCode(400).
                body("code", org.hamcrest.Matchers.equalTo("VALIDATION_ERROR"));
    }

    @Test
    void obtenerCursosDeUsuario_devuelve200_conCursos() {
        given().
                accept("application/json, application/hal+json").
        when().
                get("/api/usuarios/5/cursos").
        then().
                assertThat().
                statusCode(200).
                body("$", hasSize(3));
    }

    @Test
    void obtenerCursosDeUsuarioInexistente_devuelve404() {
        given().
        when().
                get("/api/usuarios/999/cursos").
        then().
                assertThat().
                statusCode(404);
    }

    @Test
    void agregarMisCursos_conUriList_devuelve201() {
        given().
                contentType("text/uri-list").
                body("http://localhost:8080/api/cursos/1\nhttp://localhost:8080/api/cursos/2").
        when().
                post("/api/usuarios/6/misCursosComprados").
        then().
                assertThat().
                statusCode(201);
    }

    @Test
    void agregarMisCursos_conUsuarioInexistente_devuelve404() {
        given().
                contentType("text/uri-list").
                body("http://localhost:8080/api/cursos/1").
        when().
                post("/api/usuarios/999/misCursosComprados").
        then().
                assertThat().
                statusCode(404);
    }
}
