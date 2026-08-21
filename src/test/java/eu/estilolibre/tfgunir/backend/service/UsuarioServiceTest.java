package eu.estilolibre.tfgunir.backend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import eu.estilolibre.tfgunir.backend.dto.CursoResponse;
import eu.estilolibre.tfgunir.backend.dto.UsuarioRegistroRequest;
import eu.estilolibre.tfgunir.backend.exception.EmailAlreadyExistsException;
import eu.estilolibre.tfgunir.backend.exception.ResourceNotFoundException;
import eu.estilolibre.tfgunir.backend.model.Curso;
import eu.estilolibre.tfgunir.backend.model.Usuario;
import eu.estilolibre.tfgunir.backend.repository.CursoRepository;
import eu.estilolibre.tfgunir.backend.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository repository;

    @Mock
    private CursoRepository cursoRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private UsuarioService service;

    @BeforeEach
    void setUp() {
        service = new UsuarioService(repository, cursoRepository, passwordEncoder);
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

    @Test
    void registrar_devuelveUsuarioResponse_conPasswordCodificadoYEstadoP() {
        UsuarioRegistroRequest request = new UsuarioRegistroRequest();
        request.setNombre("Ana");
        request.setApellidos("Pérez");
        request.setEmail("ana@example.com");
        request.setPassword("secreto");

        when(repository.findByEmail("ana@example.com")).thenReturn(List.of());
        when(passwordEncoder.encode("secreto")).thenReturn("$2a$10$hash");
        when(repository.save(any(Usuario.class))).thenAnswer(invocation -> {
            Usuario saved = invocation.getArgument(0);
            saved.setId(42L);
            return saved;
        });

        var response = service.registrar(request);

        assertThat(response.id()).isEqualTo(42L);
        assertThat(response.nombre()).isEqualTo("Ana");
        assertThat(response.apellidos()).isEqualTo("Pérez");
        assertThat(response.email()).isEqualTo("ana@example.com");
        assertThat(response.estado()).isEqualTo("P");
        verify(passwordEncoder).encode("secreto");

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getPassword()).isEqualTo("$2a$10$hash");
        assertThat(captor.getValue().getEstado()).isEqualTo("P");
    }

    @Test
    void registrar_lanzaEmailAlreadyExists_cuandoEmailDuplicado() {
        UsuarioRegistroRequest request = new UsuarioRegistroRequest();
        request.setNombre("Ana");
        request.setApellidos("Pérez");
        request.setEmail("ana@example.com");
        request.setPassword("secreto");

        Usuario existing = new Usuario();
        existing.setEmail("ana@example.com");
        when(repository.findByEmail("ana@example.com")).thenReturn(List.of(existing));

        assertThatThrownBy(() -> service.registrar(request))
                .isInstanceOf(EmailAlreadyExistsException.class);

        verify(repository, never()).save(any(Usuario.class));
    }

    @Test
    void obtenerCursosComprados_devuelveCursos_cuandoUsuarioExiste() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        Curso curso1 = new Curso();
        curso1.setId(4L);
        curso1.setTitulo("Angular");
        Curso curso2 = new Curso();
        curso2.setId(5L);
        curso2.setTitulo("React");
        usuario.setMisCursosComprados(new HashSet<>(Set.of(curso1, curso2)));
        when(repository.findById(1L)).thenReturn(Optional.of(usuario));

        var result = service.obtenerCursosComprados(1L);

        assertThat(result).hasSize(2);
        assertThat(result).extracting(CursoResponse::id).containsExactlyInAnyOrder(4L, 5L);
        assertThat(result).extracting(CursoResponse::titulo).containsExactlyInAnyOrder("Angular", "React");
    }

    @Test
    void obtenerCursosComprados_lanzaResourceNotFound_cuandoUsuarioNoExiste() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.obtenerCursosComprados(999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void agregarCursosComprados_añadeCursoExistente() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        Curso curso = new Curso();
        curso.setId(4L);
        when(repository.findById(1L)).thenReturn(Optional.of(usuario));
        when(cursoRepository.findById(4L)).thenReturn(Optional.of(curso));

        service.agregarCursosComprados(1L, List.of(4L));

        assertThat(usuario.getMisCursosComprados()).contains(curso);
        verify(repository).save(usuario);
    }

    @Test
    void agregarCursosComprados_lanzaResourceNotFound_cuandoUsuarioNoExiste() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.agregarCursosComprados(999L, List.of(4L)))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void agregarCursosComprados_lanzaResourceNotFound_cuandoCursoNoExiste() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(usuario));
        when(cursoRepository.findById(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.agregarCursosComprados(1L, List.of(404L)))
                .isInstanceOf(ResourceNotFoundException.class);
        verify(repository, never()).save(any(Usuario.class));
    }
}
