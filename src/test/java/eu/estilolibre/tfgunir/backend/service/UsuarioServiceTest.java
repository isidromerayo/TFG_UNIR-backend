package eu.estilolibre.tfgunir.backend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import eu.estilolibre.tfgunir.backend.model.Usuario;
import eu.estilolibre.tfgunir.backend.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository repository;

    private UsuarioService service;

    @BeforeEach
    void setUp() {
        service = new UsuarioService(repository);
    }

    @Test
    void findByEmail_returnsUser_whenExists() {
        Usuario usuario = new Usuario();
        usuario.setEmail("test@example.com");
        when(repository.findByEmail("test@example.com")).thenReturn(List.of(usuario));

        Optional<Usuario> result = service.findByEmail("test@example.com");

        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void findByEmail_returnsEmpty_whenNotExists() {
        when(repository.findByEmail("nonexistent@example.com")).thenReturn(List.of());

        Optional<Usuario> result = service.findByEmail("nonexistent@example.com");

        assertThat(result).isEmpty();
    }

    @Test
    void findById_returnsUser_whenExists() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(usuario));

        Optional<Usuario> result = service.findById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
    }

    @Test
    void findById_returnsEmpty_whenNotExists() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        Optional<Usuario> result = service.findById(999L);

        assertThat(result).isEmpty();
    }

    @Test
    void save_returnsSavedUsuario() {
        Usuario usuario = new Usuario();
        usuario.setEmail("new@example.com");
        when(repository.save(usuario)).thenReturn(usuario);

        Usuario result = service.save(usuario);

        assertThat(result.getEmail()).isEqualTo("new@example.com");
        verify(repository).save(usuario);
    }

    @Test
    void existsByEmail_returnsTrue_whenEmailExists() {
        Usuario usuario = new Usuario();
        when(repository.findByEmail("exists@example.com")).thenReturn(List.of(usuario));

        boolean result = service.existsByEmail("exists@example.com");

        assertThat(result).isTrue();
    }

    @Test
    void existsByEmail_returnsFalse_whenEmailNotExists() {
        when(repository.findByEmail("notexists@example.com")).thenReturn(List.of());

        boolean result = service.existsByEmail("notexists@example.com");

        assertThat(result).isFalse();
    }
}