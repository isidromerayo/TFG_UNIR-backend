package eu.estilolibre.tfgunir.backend.security;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * Clase de prueba para el servicio de tokens JWT.
 * Incluye pruebas para los métodos actuales y obsoletos para mantener la compatibilidad.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
@TestPropertySource(properties = {
    "jwt.secret=813cef5f-3459-4618-87a6-a69e2a1296d4_mySecretKey_mySecretKey",
    "jwt.expiration=3600000" // 1 hora en milisegundos
})
public class TokenServiceTest {
    
    @Autowired
    private TokenService tokenService;
    
    private static final String SECRET_KEY = "813cef5f-3459-4618-87a6-a69e2a1296d4_mySecretKey_mySecretKey";
    
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
    
    // Tests para métodos obsoletos (se mantienen para compatibilidad)
    @Test
    @SuppressWarnings("deprecation")
    void testCrearToken_metodoObsoleto() {
        // Arrange
        Date expiration = new Date(System.currentTimeMillis() + 3600000);
        
        // Act
        String token = new TokenService().crearToken("a@example.com", SECRET_KEY, expiration);
        
        // Assert
        assertNotEquals("xxxx", token, "El token generado debe ser diferente a 'xxxx'");
    }
    
    @Test
    @SuppressWarnings("deprecation")
    void testLeerToken_metodoObsoleto() {
        // Arrange
        Date expiration = new Date(System.currentTimeMillis() + 3600000);
        String email = "test@example.com";
        
        // Act
        TokenService tokenService = new TokenService();
        String token = tokenService.crearToken(email, SECRET_KEY, expiration);
        String emailFromToken = tokenService.leerToken(token, SECRET_KEY);
        
        // Assert
        assertNotEquals("", emailFromToken, "El email recuperado no debe estar vacío");
        assertNotNull(emailFromToken, "El email recuperado no debe ser nulo");
        assertEquals(email, emailFromToken, "El email recuperado debe coincidir con el original");
    }
}
