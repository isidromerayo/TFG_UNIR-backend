package eu.estilolibre.tfgunir.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Petición de registro de un nuevo usuario.
 */
@Data
public class UsuarioRegistroRequest {

    @NotBlank(message = "El nombre no puede estar vacío")
    private String nombre;

    @NotBlank(message = "Los apellidos no pueden estar vacíos")
    private String apellidos;

    @NotBlank(message = "El email no puede estar vacío")
    @Email(message = "El formato del email no es válido")
    private String email;

    @NotBlank(message = "La contraseña no puede estar vacía")
    @Size(min = 4, message = "La contraseña debe tener al menos 4 caracteres")
    private String password;

    @Override
    public String toString() {
        return "UsuarioRegistroRequest [nombre=" + nombre + ", apellidos=" + apellidos + ", email=" + email + "]";
    }
}
