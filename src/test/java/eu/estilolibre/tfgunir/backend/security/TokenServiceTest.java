package eu.estilolibre.tfgunir.backend.security;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * Clase de prueba para el servicio de tokens JWT.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
public class TokenServiceTest {
    
    @Autowired
    private TokenService tokenService;
    
    // Tests para métodos actuales (no obsoletos)
    @Test
    void crearToken_debeRetornarTokenValido() {
        // Act
        String token = tokenService.crearToken("usuario@ejemplo.com");
        
        // Assert
        assertNotNull(token, "El token no debe ser nulo");
        assertFalse(token.isEmpty(), "El token no debe estar vacío");
    }
    
    @Test
    void leerToken_debeRetornarEmailOriginal() {
        // Arrange
        String email = "usuario@ejemplo.com";
        String token = tokenService.crearToken(email);
        
        // Act
        String emailRecuperado = tokenService.leerToken(token);
        
        // Assert
        assertNotNull(emailRecuperado, "El email recuperado no debe ser nulo");
        assertFalse(emailRecuperado.isEmpty(), "El email recuperado no debe estar vacío");
        assertEquals(email, emailRecuperado, "El email recuperado debe coincidir con el original");
    }
    
    @Test
    void leerToken_conTokenInvalido_debeLanzarExcepcion() {
        // Arrange
        String tokenInvalido = "token.invalido";
        
        // Act & Assert
        assertThrows(Exception.class, () -> tokenService.leerToken(tokenInvalido),
            "Debe lanzar una excepción con un token inválido");
    }
    
}
