package eu.estilolibre.tfgunir.backend.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import org.junit.jupiter.api.Test;

/**
 * Contrato de configuración: ningún fichero versionado debe contener secretos
 * utilizables ni valores por defecto para ellos.
 *
 * <p>Cubre el hallazgo H-1 (secreto JWT con fallback de producción) y M-3/M-4
 * (password de base de datos) del informe
 * {@code docs/security/informe-security-review-2026-11-06.md}. Estos tests leen
 * los ficheros del repositorio directamente (no el classpath), porque el
 * {@code application.properties} de test se impone al de producción.</p>
 */
class SecretsConfigurationTest {

    /** Valor histórico que estuvo versionado y no debe reaparecer. */
    private static final String OLD_DB_PASSWORD = "tfg_un1r_PWD";

    /** Prefijo del secreto JWT que estuvo versionado. */
    private static final String OLD_JWT_SECRET_PREFIX = "813cef5f-3459-4618-87a6-a69e2a1296d4";

    /** Marcador de valor a rellenar por el operador. */
    private static final String PLACEHOLDER_PREFIX = "CHANGE_ME";

    private static final Path MAIN_PROPERTIES = Path.of("src/main/resources/application.properties");
    private static final Path DOCKER_COMPOSE = Path.of("docker-compose.yml");
    private static final Path ENV_EXAMPLE = Path.of(".env.example");
    private static final Path PODMAN_SCRIPT = Path.of("scripts/podman-pod.sh");

    @Test
    void applicationProperties_noDefineValorPorDefectoParaJwtNiPassword() throws IOException {
        Properties properties = loadProperties(MAIN_PROPERTIES);

        assertThat(properties.getProperty("jwt.secret"))
                .as("jwt.secret debe exigir JWT_SECRET sin fallback")
                .isEqualTo("${JWT_SECRET}");

        assertThat(properties.getProperty("spring.datasource.password"))
                .as("la password de BD debe exigir SPRING_DATASOURCE_PASSWORD sin fallback")
                .isEqualTo("${SPRING_DATASOURCE_PASSWORD}");
    }

    @Test
    void applicationProperties_noContieneSecretosConocidos() throws IOException {
        String contenido = Files.readString(MAIN_PROPERTIES, StandardCharsets.UTF_8);

        assertThat(contenido).doesNotContain(OLD_DB_PASSWORD);
        assertThat(contenido).doesNotContain(OLD_JWT_SECRET_PREFIX);
    }

    @Test
    void dockerComposeExigeLosSecretosEnLugarDeTenerDefaults() throws IOException {
        String contenido = Files.readString(DOCKER_COMPOSE, StandardCharsets.UTF_8);

        assertThat(contenido)
                .as("POSTGRES_PASSWORD debe ser obligatoria en compose")
                .contains("${POSTGRES_PASSWORD:?");
        assertThat(contenido)
                .as("JWT_SECRET debe llegar al contenedor del API")
                .contains("JWT_SECRET");
        assertThat(contenido).doesNotContain(OLD_DB_PASSWORD);
        assertThat(contenido).doesNotContain(OLD_JWT_SECRET_PREFIX);
    }

    @Test
    void envExampleNoContieneSecretosUtilizables() throws IOException {
        String contenido = Files.readString(ENV_EXAMPLE, StandardCharsets.UTF_8);

        assertThat(contenido).doesNotContain(OLD_DB_PASSWORD);
        assertThat(contenido).doesNotContain(OLD_JWT_SECRET_PREFIX);
        assertThat(contenido)
                .as("el ejemplo debe pedir valores nuevos, no copiarlos")
                .contains(PLACEHOLDER_PREFIX + "_POSTGRES_PASSWORD")
                .contains(PLACEHOLDER_PREFIX + "_JWT_SECRET");
    }

    @Test
    void scriptDePodmanExigeLosSecretosEnLugarDeInyectarDefaults() throws IOException {
        String contenido = Files.readString(PODMAN_SCRIPT, StandardCharsets.UTF_8);

        assertThat(contenido).doesNotContain(OLD_DB_PASSWORD);
        assertThat(contenido).doesNotContain(OLD_JWT_SECRET_PREFIX);
        assertThat(contenido)
                .as("el script debe abortar si falta el secreto")
                .contains("${POSTGRES_PASSWORD:?")
                .contains("${JWT_SECRET:?");
    }

    @Test
    void ningunScriptVersionaSecretos() throws IOException {
        try (var scripts = Files.list(Path.of("scripts"))) {
            for (Path script : scripts.filter(path -> path.toString().endsWith(".sh")).toList()) {
                String contenido = Files.readString(script, StandardCharsets.UTF_8);
                assertThat(contenido)
                        .as("%s no debe contener la password histórica", script)
                        .doesNotContain(OLD_DB_PASSWORD);
                assertThat(contenido)
                        .as("%s no debe contener el secreto JWT histórico", script)
                        .doesNotContain(OLD_JWT_SECRET_PREFIX);
            }
        }
    }

    private Properties loadProperties(Path path) throws IOException {
        Properties properties = new Properties();
        try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            properties.load(reader);
        }
        return properties;
    }
}
