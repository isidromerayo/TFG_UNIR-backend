package eu.estilolibre.tfgunir.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Petición de login.
 */
@Data
public class LoginRequest {

    @NotBlank(message = "El email no puede estar vacío")
    @Email(message = "El formato del email no es válido")
    private String email;

    @NotBlank(message = "La contraseña no puede estar vacía")
    @Size(min = 4, message = "La contraseña debe tener al menos 4 caracteres")
    private String password;

    public LoginRequest() {
    }

    @Override
    public String toString() {
        return "LoginRequest [email=" + email + "]";
    }
}
