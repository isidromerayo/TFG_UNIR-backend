package eu.estilolibre.tfgunir.backend.exception;

/**
 * Se lanza al intentar registrar un usuario con un email ya existente.
 */
public class EmailAlreadyExistsException extends RuntimeException {

    public EmailAlreadyExistsException(String email) {
        super("Ya existe un usuario con el email: " + email);
    }
}
