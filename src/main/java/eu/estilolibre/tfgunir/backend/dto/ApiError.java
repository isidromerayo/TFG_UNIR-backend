package eu.estilolibre.tfgunir.backend.dto;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatusCode;

/**
 * Respuesta de error estándar de la API.
 * 
 * @param status  código HTTP
 * @param code    código de negocio
 * @param message mensaje legible
 * @param errors  errores de validación por campo (opcional)
 */
public record ApiError(int status, String code, String message, Map<String, String> errors) {

    public ApiError {
        errors = errors == null ? null : Collections.unmodifiableMap(new HashMap<>(errors));
    }

    @Override
    public Map<String, String> errors() {
        return errors == null ? null : Collections.unmodifiableMap(new HashMap<>(errors));
    }

    /**
     * Crea un error sin errores de campo.
     */
    public static ApiError of(HttpStatusCode status, String code, String message) {
        return new ApiError(status.value(), code, message, null);
    }

    /**
     * Crea un error de validación con los errores por campo.
     */
    public static ApiError ofValidation(HttpStatusCode status, Map<String, String> errors) {
        return new ApiError(status.value(), "VALIDATION_ERROR", "Validation failed", errors);
    }
}
