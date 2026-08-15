package eu.estilolibre.tfgunir.backend.controller;

import jakarta.validation.Valid;
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
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import eu.estilolibre.tfgunir.backend.dto.ApiError;
import eu.estilolibre.tfgunir.backend.dto.AuthResponse;
import eu.estilolibre.tfgunir.backend.dto.LoginRequest;
import eu.estilolibre.tfgunir.backend.security.TokenService;
import eu.estilolibre.tfgunir.backend.service.UsuarioService;

/**
 * Controlador de autenticación.
 * 
 * Maneja el login de usuarios con validación segura de contraseñas
 * usando BCrypt para prevenir timing attacks.
 */
@Tag(name = "Autenticación", description = "Operaciones de autenticación de usuarios")
@RestController
@RequestMapping("/api/auth")
@Validated
public class LoginController {

    private final UsuarioService usuarioService;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public LoginController(
            UsuarioService usuarioService,
            PasswordEncoder passwordEncoder,
            TokenService tokenService) {
        this.usuarioService = usuarioService;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    @Operation(
        summary = "Login de usuario",
        description = "Autentica un usuario con email y contraseña y devuelve un token JWT junto con sus datos básicos."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Login correcto, token JWT generado",
            content = @Content(schema = @Schema(implementation = AuthResponse.class))),
        @ApiResponse(responseCode = "400", description = "Validación de entrada fallida",
            content = @Content(schema = @Schema(implementation = ApiError.class))),
        @ApiResponse(responseCode = "401", description = "Credenciales inválidas o usuario inactivo",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PostMapping("")
    public ResponseEntity<?> auth(@Valid @RequestBody LoginRequest login) {
        var usuarioOpt = usuarioService.findByEmail(login.getEmail());

        if (usuarioOpt.isEmpty()) {
            return unauthorized();
        }

        var usuario = usuarioOpt.get();
        boolean passwordMatches = passwordEncoder.matches(login.getPassword(), usuario.getPassword());
        boolean isActive = "A".equals(usuario.getEstado());

        if (passwordMatches && isActive) {
            String token = tokenService.crearToken(usuario.getEmail());

            AuthResponse response = new AuthResponse(
                usuario.getId(),
                usuario.getEmail(),
                token,
                usuario.getNombre() + " " + usuario.getApellidos()
            );

            return ResponseEntity.ok(response);
        }

        return unauthorized();
    }

    private ResponseEntity<?> unauthorized() {
        ApiError error = ApiError.of(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "no autorizado");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }
}
