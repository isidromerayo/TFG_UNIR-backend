package eu.estilolibre.tfgunir.backend.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import eu.estilolibre.tfgunir.backend.model.Usuario;
import eu.estilolibre.tfgunir.backend.repository.UsuarioRepository;
import eu.estilolibre.tfgunir.backend.security.TokenService;

/**
 * Controlador de autenticación.
 * 
 * Maneja el login de usuarios con validación segura de contraseñas
 * usando BCrypt para prevenir timing attacks.
 */
@CrossOrigin(origins = { "http://localhost:4200", "http://localhost:3000", "http://localhost:5173" })
@RestController
@RequestMapping("/api/auth")
public class LoginController {

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    @Autowired
    public LoginController(
            UsuarioRepository repository,
            PasswordEncoder passwordEncoder,
            TokenService tokenService) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    /**
     * Autentica un usuario y genera un token JWT.
     * 
     * Utiliza BCrypt para comparación segura de contraseñas (constant-time),
     * previniendo timing attacks.
     * 
     * @param login Credenciales del usuario (email y password)
     * @return ResponseEntity con token JWT si la autenticación es exitosa,
     *         o mensaje de error si falla
     */
    @PostMapping("")
    public ResponseEntity<?> auth(@RequestBody FormUser login) {
        // Buscar usuario en BBDD
        List<Usuario> result = repository.findByEmail(login.getEmail());

        if (result.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "no autorizado"));
        }

        Usuario usuario = result.get(0);
        
        // ✅ SEGURIDAD: Comparación constant-time con BCrypt
        // Previene timing attacks al comparar passwords
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
                .body(Map.of("message", "no autorizado"));
    }
}
