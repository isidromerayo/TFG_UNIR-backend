package eu.estilolibre.tfgunir.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

/**
 * Configuración de la documentación OpenAPI/Swagger.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI tfgUnirOpenAPI(@Value("${info.app.version}") String version) {
        return new OpenAPI()
                .info(new Info()
                        .title("TFG UNIR Backend API")
                        .description("API REST del backend TFG UNIR. Autenticación JWT y acceso a catálogo de cursos.")
                        .version(version));
    }
}
