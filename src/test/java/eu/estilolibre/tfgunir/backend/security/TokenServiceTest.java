package eu.estilolibre.tfgunir.backend.security;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.Date;

import org.junit.jupiter.api.Test;

public class TokenServiceTest {
    @Test
    void testCrearToken() {
        String secretKey = "813cef5f-3459-4618-87a6-a69e2a1296d4_mySecretKey_mySecretKey";
        Date expiration = new Date(System.currentTimeMillis() + 3600000);
        String token = new TokenService().crearToken("a@example.com", secretKey, expiration);
        assertNotEquals("xxxx", token);
    }

    @Test
    void testLeerToken() {
        String secretKey = "813cef5f-3459-4618-87a6-a69e2a1296d4_mySecretKey_mySecretKey";
        Date expiration = new Date(System.currentTimeMillis() + 3600000);
        String email = "test@example.com";
        
        TokenService tokenService = new TokenService();
        String token = tokenService.crearToken(email, secretKey, expiration);
        
        String emailFromToken = tokenService.leerToken(token, secretKey);
        
        assertNotEquals("", emailFromToken);
        assertNotEquals(null, emailFromToken);
    }
}
