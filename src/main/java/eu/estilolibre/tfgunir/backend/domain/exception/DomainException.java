package eu.estilolibre.tfgunir.backend.domain.exception;

/**
 * Excepción base para errores de dominio.
 * 
 * <p>Esta clase representa errores que ocurren en la capa de dominio,
 * como validaciones de negocio fallidas, reglas de dominio violadas,
 * o invariantes no cumplidas.</p>
 * 
 * <p>No depende de ninguna librería externa (Spring, JPA, etc.) para
 * mantener la capa de dominio completamente aislada de la infraestructura.</p>
 */
public class DomainException extends RuntimeException {

    public DomainException(String message) {
        super(message);
    }

    public DomainException(String message, Throwable cause) {
        super(message, cause);
    }
}
