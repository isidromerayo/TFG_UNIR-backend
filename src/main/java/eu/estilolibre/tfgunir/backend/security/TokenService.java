package eu.estilolibre.tfgunir.backend.security;

import javax.crypto.SecretKey;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.util.Date;

public class TokenService {
 
    public String crearToken(String usuario, String claveEncriptar, Date expiracion) {
        SecretKey clave = Keys.hmacShaKeyFor(claveEncriptar.getBytes());
        
        String JWT = Jwts.builder()
        .setSubject(usuario)
        .setExpiration(expiracion)
        .signWith(clave,SignatureAlgorithm.HS256)
        .compact();
   
        return JWT;
    }

    public String leerToken(String tokenReal, String claveEncriptar) {
        SecretKey clave = Keys.hmacShaKeyFor(claveEncriptar.getBytes());

        JwtParser parser = Jwts.parserBuilder().setSigningKey(clave).build();

        Jws<Claims> token = parser.parseClaimsJws(tokenReal);
        System.out.println(token.getHeader() + "/" + token.getBody().toString() + "/" + token.getSignature());
        return token.getBody().getSubject();
    }
}
