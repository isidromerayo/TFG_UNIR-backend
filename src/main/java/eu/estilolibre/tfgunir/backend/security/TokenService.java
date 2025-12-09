package eu.estilolibre.tfgunir.backend.security;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.logging.Logger;

/**
 * Servicio para la creación y validación de tokens JWT.
 * 
 * Utiliza configuración externa para la clave secreta y tiempo de expiración.
 */
@Service
public class TokenService {

    private static final Logger logger = Logger.getLogger(TokenService.class.getName());

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration:3600000}")
    private long expiration;

    /**
     * Crea un token JWT para el usuario especificado.
     * 
     * @param usuario Email o identificador del usuario
     * @return Token JWT firmado
     */
    public String crearToken(String usuario) {
        SecretKey clave = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        Date expiracion = new Date(System.currentTimeMillis() + this.expiration);

        return Jwts.builder()
                .claim("sub", usuario)
                .expiration(expiracion)
                .signWith(clave)
                .compact();
    }

    /**
     * Método legacy para compatibilidad con código existente.
     * 
     * @deprecated Usar {@link #crearToken(String)} en su lugar
     */
    @Deprecated(since = "0.2.2")
    public String crearToken(String usuario, String claveEncriptar, Date expiracion) {
        SecretKey clave = Keys.hmacShaKeyFor(claveEncriptar.getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
                .claim("sub", usuario)
                .expiration(expiracion)
                .signWith(clave)
                .compact();
    }

    /**
     * Lee y valida un token JWT.
     * 
     * @param tokenReal Token JWT a validar
     * @return Subject (usuario) del token
     */
    public String leerToken(String tokenReal) {
        SecretKey clave = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));

        JwtParser parser = Jwts.parser().verifyWith(clave).build();

        Jws<Claims> token = parser.parseSignedClaims(tokenReal);
        
        if (logger.isLoggable(java.util.logging.Level.INFO)) {
            String[] parts = tokenReal.split("\\.");
            String signature = (parts.length == 3) ? parts[2] : "";
            logger.info(token.getHeader() + "/" + token.getPayload().toString() + "/" + signature);
        }
        
        return token.getPayload().getSubject();
    }

    /**
     * Método legacy para compatibilidad con código existente.
     * 
     * @deprecated Usar {@link #leerToken(String)} en su lugar
     */
    @Deprecated(since = "0.2.2")
    public String leerToken(String tokenReal, String claveEncriptar) {
        SecretKey clave = Keys.hmacShaKeyFor(claveEncriptar.getBytes(StandardCharsets.UTF_8));

        JwtParser parser = Jwts.parser().verifyWith(clave).build();

        Jws<Claims> token = parser.parseSignedClaims(tokenReal);
        
        if (logger.isLoggable(java.util.logging.Level.INFO)) {
            String[] parts = tokenReal.split("\\.");
            String signature = (parts.length == 3) ? parts[2] : "";
            logger.info(token.getHeader() + "/" + token.getPayload().toString() + "/" + signature);
        }
        
        return token.getPayload().getSubject();
    }
}
