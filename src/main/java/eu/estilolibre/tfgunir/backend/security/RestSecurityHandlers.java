package eu.estilolibre.tfgunir.backend.security;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

import jakarta.servlet.http.HttpServletResponse;

/**
 * Respuestas JSON homogéneas (formato {@code ApiError}) para los rechazos de
 * Spring Security, en lugar del HTML o el 403 por defecto del contenedor.
 *
 * <p>Los mensajes son constantes: nunca se refleja en la respuesta el recurso
 * solicitado ni el motivo exacto del rechazo, para no facilitar enumeración.</p>
 */
public final class RestSecurityHandlers {

    private static final String JSON_TEMPLATE =
            "{\"status\":%d,\"code\":\"%s\",\"message\":\"%s\",\"errors\":null}";

    private RestSecurityHandlers() {
        // Clase de utilidad: no se instancia.
    }

    public static AuthenticationEntryPoint unauthorized() {
        return (request, response, authException) ->
                write(response, HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "No autenticado");
    }

    public static AccessDeniedHandler forbidden() {
        return (request, response, accessDeniedException) ->
                write(response, HttpStatus.FORBIDDEN, "FORBIDDEN", "Acceso denegado");
    }

    private static void write(HttpServletResponse response, HttpStatusCode status, String code,
            String message) throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(String.format(JSON_TEMPLATE, status.value(), code, message));
    }
}
