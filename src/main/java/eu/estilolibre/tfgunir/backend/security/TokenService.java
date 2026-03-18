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

@Service
public class TokenService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration:3600000}")
    private long expiration;

    public String crearToken(String usuario) {
        SecretKey clave = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        Date expiracion = new Date(System.currentTimeMillis() + this.expiration);

        return Jwts.builder()
                .claim("sub", usuario)
                .expiration(expiracion)
                .signWith(clave)
                .compact();
    }

    public String leerToken(String tokenReal) {
        SecretKey clave = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));

        JwtParser parser = Jwts.parser().verifyWith(clave).build();

        Jws<Claims> token = parser.parseSignedClaims(tokenReal);
        
        return token.getPayload().getSubject();
    }
}
