package eu.estilolibre.tfgunir.backend.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Utilidad para generar hashes BCrypt de passwords.
 * 
 * Usado para generar los hashes que se insertan en data.sql
 * 
 * Ejecutar con: ./mvnw test -Dtest=PasswordHashGenerator
 */
public class PasswordHashGenerator {

    public static void main(String[] args) {
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        
        System.out.println("=== BCrypt Password Hashes ===\n");
        
        // Passwords usadas en data.sql
        String[] passwords = {"1234", "admin"};
        
        for (String password : passwords) {
            String hash = encoder.encode(password);
            System.out.println("Password: " + password);
            System.out.println("BCrypt:   " + hash);
            System.out.println();
        }
        
        System.out.println("=== Verificación ===\n");
        
        // Verificar que funciona
        String testPassword = "admin";
        String testHash = encoder.encode(testPassword);
        boolean matches = encoder.matches(testPassword, testHash);
        
        System.out.println("Test password: " + testPassword);
        System.out.println("Test hash:     " + testHash);
        System.out.println("Matches:       " + matches);
    }
}
