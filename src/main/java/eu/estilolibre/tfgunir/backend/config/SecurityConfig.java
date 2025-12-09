package eu.estilolibre.tfgunir.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configuración de seguridad para la aplicación.
 * 
 * Proporciona beans de seguridad como PasswordEncoder para
 * el hashing seguro de contraseñas usando BCrypt.
 */
@Configuration
public class SecurityConfig {

    /**
     * Bean de PasswordEncoder usando BCrypt.
     * 
     * BCrypt proporciona:
     * - Hashing seguro con salt automático
     * - Comparación constant-time (previene timing attacks)
     * - Work factor ajustable (por defecto: 10)
     * 
     * @return PasswordEncoder configurado con BCrypt
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
