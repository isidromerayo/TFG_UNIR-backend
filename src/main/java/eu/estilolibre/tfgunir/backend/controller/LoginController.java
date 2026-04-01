package eu.estilolibre.tfgunir.backend.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import eu.estilolibre.tfgunir.backend.model.Usuario;
import eu.estilolibre.tfgunir.backend.security.TokenService;
import eu.estilolibre.tfgunir.backend.service.UsuarioService;

/**
 * Controlador de autenticación.
 * 
 * Maneja el login de usuarios con validación segura de contraseñas
 * usando BCrypt para prevenir timing attacks.
 */
@CrossOrigin(origins = { "http://localhost:4200", "http://localhost:3000", "http://localhost:5173" })
@RestController
@RequestMapping("/api/auth")
@Validated
public class LoginController {

    private final UsuarioService usuarioService;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    @Autowired
    public LoginController(
            UsuarioService usuarioService,
            PasswordEncoder passwordEncoder,
            TokenService tokenService) {
        this.usuarioService = usuarioService;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    /**
     * DTO para respuestas de error de autenticación.
     */
    private record ErrorResponse(String message) {}

    @PostMapping("")
    public ResponseEntity<Object> auth(@Valid @RequestBody FormUser login) {
        var usuarioOpt = usuarioService.findByEmail(login.getEmail());
        
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse("no autorizado"));
        }
        
        var usuario = usuarioOpt.get();
        boolean passwordMatches = passwordEncoder.matches(login.getPassword(), usuario.getPassword());
        boolean isActive = "A".equals(usuario.getEstado());
        
        if (passwordMatches && isActive) {
            String token = tokenService.crearToken(usuario.getEmail());
            
            User user = new User();
            user.setUsername(usuario.getEmail());
            user.setFullname(usuario.getNombre() + " " + usuario.getApellidos());
            user.setId(usuario.getId());
            user.setToken(token);
            
            return ResponseEntity.ok(user);
        }

        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(new ErrorResponse("no autorizado"));
    }
}
