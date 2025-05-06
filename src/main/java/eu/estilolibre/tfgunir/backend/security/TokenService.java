package eu.estilolibre.tfgunir.backend.security;

import javax.crypto.SecretKey;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import java.util.logging.Logger;

public class TokenService {

    private static final Logger logger = Logger.getLogger(TokenService.class.getName());

    public String crearToken(String usuario, String claveEncriptar, Date expiracion) {
        SecretKey clave = Keys.hmacShaKeyFor(claveEncriptar.getBytes());

        return Jwts.builder()
                .claim("sub", usuario)
                .expiration(expiracion)
                .signWith(clave)
                .compact();
    }

    public String leerToken(String tokenReal, String claveEncriptar) {
        SecretKey clave = Keys.hmacShaKeyFor(claveEncriptar.getBytes());

        JwtParser parser = Jwts.parser().verifyWith(clave).build();

        Jws<Claims> token = parser.parseSignedClaims(tokenReal);
        String[] parts = tokenReal.split("\\.");
        String signature = (parts.length == 3) ? parts[2] : "";
        logger.info(token.getHeader() + "/" + token.getPayload().toString() + "/" + signature);
        return token.getPayload().getSubject();
    }
}
