package eu.estilolibre.tfgunir.backend.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Configuración CORS única para la API.
 *
 * <p>Los orígenes se externalizan en {@code app.cors.allowed-origins} (variable
 * de entorno {@code APP_CORS_ALLOWED_ORIGINS}) para poder desplegar en distintos
 * entornos sin recompilar. Se usa {@link CorsConfiguration#setAllowedOriginPatterns}
 * en lugar de {@code setAllowedOrigins} porque con credenciales habilitadas no se
 * admite el comodín {@code *}, pero sí patrones como
 * {@code https://*.estilolibre.eu}.</p>
 */
@Configuration
public class CorsConfig {

    private final String[] allowedOriginPatterns;

    public CorsConfig(
            @Value("${app.cors.allowed-origins:http://localhost:4200,http://localhost:3000,http://localhost:5173}")
            String[] allowedOriginPatterns) {
        this.allowedOriginPatterns = allowedOriginPatterns;
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList(allowedOriginPatterns));
        configuration.setAllowedMethods(List.of("GET", "POST", "OPTIONS", "DELETE", "PUT", "PATCH"));
        configuration.setAllowedHeaders(List.of(
                "Authorization",
                "Content-Type",
                "X-Requested-With",
                "Accept",
                "Origin",
                "Access-Control-Request-Method",
                "Access-Control-Request-Headers"));
        configuration.setExposedHeaders(List.of(
                "Access-Control-Allow-Origin",
                "Access-Control-Allow-Credentials",
                "Authorization"));
        configuration.setAllowCredentials(Boolean.TRUE);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        return source;
    }
}
