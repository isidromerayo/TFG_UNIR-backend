package eu.estilolibre.tfgunir.backend.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

import eu.estilolibre.tfgunir.backend.dto.LoginRequest;
import eu.estilolibre.tfgunir.backend.model.Usuario;
import eu.estilolibre.tfgunir.backend.security.LoginRateLimiter;
import eu.estilolibre.tfgunir.backend.security.TokenService;
import eu.estilolibre.tfgunir.backend.service.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;

@ExtendWith(MockitoExtension.class)
class LoginControllerTest {

    private static final String IP = "10.0.0.1";
    private static final String HASH_SENUELO = "$2a$10$senuelo";

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TokenService tokenService;

    @Mock
    private LoginRateLimiter rateLimiter;

    @Mock
    private HttpServletRequest request;

    private LoginController controller;

    @BeforeEach
    void setUp() {
        controller = new LoginController(usuarioService, passwordEncoder, tokenService, rateLimiter);
        when(request.getRemoteAddr()).thenReturn(IP);
    }

    @Test
    void usuarioInexistente_comparaContraUnHashSenueloParaNoDelatarSiExiste() {
        LoginRequest login = login("nadie@example.com", "secreto123");
        when(usuarioService.findByEmail("nadie@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn(HASH_SENUELO);
        when(passwordEncoder.matches("secreto123", HASH_SENUELO)).thenReturn(false);

        var response = controller.auth(login, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        verify(passwordEncoder).matches("secreto123", HASH_SENUELO);
        verify(rateLimiter).registrarFallo(IP);
    }

    @Test
    void origenBloqueado_devuelve429_sinConsultarUsuarios() {
        LoginRequest login = login("helena@localhost", "1234");
        when(rateLimiter.estaBloqueado(IP)).thenReturn(true);
        when(rateLimiter.segundosRestantes(IP)).thenReturn(600L);

        var response = controller.auth(login, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.TOO_MANY_REQUESTS);
        assertThat(response.getHeaders().getFirst("Retry-After")).isEqualTo("600");
        verify(usuarioService, never()).findByEmail(anyString());
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    void loginCorrecto_reiniciaElContadorDeIntentos() {
        LoginRequest login = login("helena@localhost", "1234");
        Usuario usuario = usuarioActivo("helena@localhost", "$2a$10$real");
        when(usuarioService.findByEmail("helena@localhost")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("1234", "$2a$10$real")).thenReturn(true);
        when(tokenService.crearToken("helena@localhost")).thenReturn("jwt");

        var response = controller.auth(login, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(rateLimiter).limpiar(IP);
        verify(rateLimiter, never()).registrarFallo(IP);
    }

    @Test
    void passwordIncorrecta_registraElFallo() {
        LoginRequest login = login("helena@localhost", "mala");
        Usuario usuario = usuarioActivo("helena@localhost", "$2a$10$real");
        when(usuarioService.findByEmail("helena@localhost")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("mala", "$2a$10$real")).thenReturn(false);

        var response = controller.auth(login, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        verify(rateLimiter).registrarFallo(IP);
        verify(rateLimiter, never()).limpiar(IP);
    }

    @Test
    void usuarioInactivo_conPasswordCorrecta_noEmiteTokenYRegistraElFallo() {
        LoginRequest login = login("pendiente@example.com", "secreto123");
        Usuario usuario = usuarioActivo("pendiente@example.com", "$2a$10$real");
        usuario.setEstado("P");
        when(usuarioService.findByEmail("pendiente@example.com")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("secreto123", "$2a$10$real")).thenReturn(true);

        var response = controller.auth(login, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        verify(tokenService, never()).crearToken(anyString());
        verify(rateLimiter).registrarFallo(IP);
    }

    private LoginRequest login(String email, String password) {
        LoginRequest login = new LoginRequest();
        login.setEmail(email);
        login.setPassword(password);
        return login;
    }

    private Usuario usuarioActivo(String email, String password) {
        Usuario usuario = new Usuario();
        usuario.setId(5L);
        usuario.setEmail(email);
        usuario.setPassword(password);
        usuario.setEstado("A");
        usuario.setNombre("Helena");
        usuario.setApellidos("Fabero");
        return usuario;
    }
}
