package eu.estilolibre.tfgunir.backend.security;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import eu.estilolibre.tfgunir.backend.model.Usuario;
import eu.estilolibre.tfgunir.backend.service.UsuarioService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Valida el JWT que llega en la cabecera {@code Authorization: Bearer ...} y
 * publica la autenticación correspondiente en el {@link SecurityContextHolder}.
 *
 * <p>Solo se autentica a usuarios existentes y activos ({@code estado = "A"}).
 * Un token inválido, expirado, firmado con otro secreto o de un usuario dado de
 * baja no autentica: el contexto queda vacío y la petición continúa, de modo que
 * las reglas de {@link WebSecurityConfig} decidan (401 en rutas protegidas).</p>
 *
 * <p>No es un bean de Spring a propósito: lo instancia {@link WebSecurityConfig}
 * y lo inserta solo en la cadena de seguridad. Registrarlo además como
 * {@code @Component} haría que el contenedor lo ejecutase dos veces (como filtro
 * de servlet y dentro de la cadena).</p>
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";
    private static final String ESTADO_ACTIVO = "A";
    private static final List<SimpleGrantedAuthority> AUTHORITIES =
            List.of(new SimpleGrantedAuthority("ROLE_USER"));

    private final TokenService tokenService;
    private final UsuarioService usuarioService;

    public JwtAuthenticationFilter(TokenService tokenService, UsuarioService usuarioService) {
        this.tokenService = tokenService;
        this.usuarioService = usuarioService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header != null && header.startsWith(BEARER_PREFIX)) {
            autenticar(header.substring(BEARER_PREFIX.length()).trim(), request);
        }

        filterChain.doFilter(request, response);
    }

    private void autenticar(String token, HttpServletRequest request) {
        try {
            String email = tokenService.leerToken(token);
            usuarioService.findByEmail(email)
                    .filter(this::estaActivo)
                    .ifPresent(usuario -> publicarAutenticacion(usuario, request));
        } catch (JwtException | IllegalArgumentException ex) {
            // Token inválido o manipulado: no se autentica y no se registra su contenido
            // (nunca se loguean tokens ni secretos).
            SecurityContextHolder.clearContext();
        }
    }

    private boolean estaActivo(Usuario usuario) {
        return ESTADO_ACTIVO.equals(usuario.getEstado());
    }

    private void publicarAutenticacion(Usuario usuario, HttpServletRequest request) {
        var autenticacion = new UsernamePasswordAuthenticationToken(
                usuario.getEmail(), null, AUTHORITIES);
        autenticacion.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(autenticacion);
    }
}
