package eu.estilolibre.tfgunir.backend.dto;

/**
 * Respuesta de autenticación.
 */
public record AuthResponse(long id, String username, String token, String fullname) {
}
