package eu.estilolibre.tfgunir.backend.controller;

import java.util.UUID;

import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import eu.estilolibre.tfgunir.backend.dto.ApiError;
import eu.estilolibre.tfgunir.backend.dto.AuthResponse;
import eu.estilolibre.tfgunir.backend.dto.LoginRequest;
import eu.estilolibre.tfgunir.backend.model.Usuario;
import eu.estilolibre.tfgunir.backend.security.LoginRateLimiter;
import eu.estilolibre.tfgunir.backend.security.TokenService;
import eu.estilolibre.tfgunir.backend.service.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Controlador de autenticación.
 *
 * <p>Maneja el login con validación segura de contraseñas (BCrypt) y dos
 * protecciones adicionales:</p>
 * <ul>
 *   <li><b>Sin enumeración por tiempo</b>: se compara siempre contra un hash,
 *       aunque el email no exista (hash señuelo aleatorio por instancia).</li>
 *   <li><b>Límite de intentos</b>: tras varios fallos desde el mismo origen se
 *       responde {@code 429} con {@code Retry-After} ({@link LoginRateLimiter}).</li>
 * </ul>
 */
@Tag(name = "Autenticación", description = "Operaciones de autenticación de usuarios")
@RestController
@RequestMapping("/api/auth")
@Validated
public class LoginController {

    private static final String ESTADO_ACTIVO = "A";

    private final UsuarioService usuarioService;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final LoginRateLimiter rateLimiter;

    /** Hash BCrypt de un valor aleatorio: solo sirve para gastar el mismo tiempo. */
    private volatile String hashSenuelo;

    public LoginController(
            UsuarioService usuarioService,
            PasswordEncoder passwordEncoder,
            TokenService tokenService,
            LoginRateLimiter rateLimiter) {
        this.usuarioService = usuarioService;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
        this.rateLimiter = rateLimiter;
    }

    @Operation(
        summary = "Login de usuario",
        description = "Autentica un usuario con email y contraseña y devuelve un token JWT junto con sus datos básicos."
    )
    @ApiResponse(responseCode = "200", description = "Login correcto, token JWT generado",
        content = @Content(schema = @Schema(implementation = AuthResponse.class)))
    @ApiResponse(responseCode = "400", description = "Validación de entrada fallida",
        content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "401", description = "Credenciales inválidas o usuario inactivo",
        content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "429", description = "Demasiados intentos desde el mismo origen",
        content = @Content(schema = @Schema(implementation = ApiError.class)))
    @PostMapping("")
    public ResponseEntity<Object> auth(@Valid @RequestBody LoginRequest login,
            HttpServletRequest request) {

        String origen = request.getRemoteAddr();

        if (rateLimiter.estaBloqueado(origen)) {
            return demasiadosIntentos(origen);
        }

        var usuarioOpt = usuarioService.findByEmail(login.getEmail());
        Usuario usuario = usuarioOpt.orElse(null);

        // Se compara siempre, exista o no el usuario: evita distinguir cuentas por latencia.
        String hash = usuario != null ? usuario.getPassword() : hashSenuelo();
        boolean passwordMatches = passwordEncoder.matches(login.getPassword(), hash);
        boolean isActive = usuario != null && ESTADO_ACTIVO.equals(usuario.getEstado());

        if (passwordMatches && isActive) {
            rateLimiter.limpiar(origen);

            String token = tokenService.crearToken(usuario.getEmail());

            AuthResponse response = new AuthResponse(
                usuario.getId(),
                usuario.getEmail(),
                token,
                usuario.getNombre() + " " + usuario.getApellidos()
            );

            return ResponseEntity.ok(response);
        }

        rateLimiter.registrarFallo(origen);
        return unauthorized();
    }

    private ResponseEntity<Object> unauthorized() {
        ApiError error = ApiError.of(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "no autorizado");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    private ResponseEntity<Object> demasiadosIntentos(String origen) {
        ApiError error = ApiError.of(
            HttpStatus.TOO_MANY_REQUESTS,
            "TOO_MANY_REQUESTS",
            "Demasiados intentos de login, inténtalo más tarde"
        );

        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .header(HttpHeaders.RETRY_AFTER, String.valueOf(rateLimiter.segundosRestantes(origen)))
                .body(error);
    }

    private String hashSenuelo() {
        String hash = this.hashSenuelo;
        if (hash == null) {
            // Se calcula una sola vez por instancia: el valor es aleatorio y nunca se compara con éxito,
            // porque para un usuario inexistente isActive siempre es false.
            hash = passwordEncoder.encode(UUID.randomUUID().toString());
            this.hashSenuelo = hash;
        }
        return hash;
    }
}
