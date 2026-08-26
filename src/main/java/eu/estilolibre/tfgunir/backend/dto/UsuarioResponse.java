package eu.estilolibre.tfgunir.backend.dto;

import eu.estilolibre.tfgunir.backend.model.Usuario;

/**
 * Respuesta de usuario, sin datos sensibles como la contraseña.
 *
 * @param id       identificador del usuario
 * @param nombre   nombre del usuario
 * @param apellidos apellidos del usuario
 * @param email    email del usuario
 * @param estado   estado del usuario (P pendiente, A activo, ...)
 */
public record UsuarioResponse(long id, String nombre, String apellidos, String email, String estado) {

    public static UsuarioResponse from(Usuario usuario) {
        return new UsuarioResponse(
            usuario.getId(),
            usuario.getNombre(),
            usuario.getApellidos(),
            usuario.getEmail(),
            usuario.getEstado()
        );
    }
}
