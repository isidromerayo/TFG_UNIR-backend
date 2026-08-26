package eu.estilolibre.tfgunir.backend.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import eu.estilolibre.tfgunir.backend.dto.CursoResponse;
import eu.estilolibre.tfgunir.backend.dto.UsuarioRegistroRequest;
import eu.estilolibre.tfgunir.backend.dto.UsuarioResponse;
import eu.estilolibre.tfgunir.backend.exception.EmailAlreadyExistsException;
import eu.estilolibre.tfgunir.backend.exception.ResourceNotFoundException;
import eu.estilolibre.tfgunir.backend.model.Curso;
import eu.estilolibre.tfgunir.backend.model.Usuario;
import eu.estilolibre.tfgunir.backend.repository.CursoRepository;
import eu.estilolibre.tfgunir.backend.repository.UsuarioRepository;

@Service
public class UsuarioService {

    private final UsuarioRepository repository;
    private final CursoRepository cursoRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository repository, CursoRepository cursoRepository,
            PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.cursoRepository = cursoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> findByEmail(String email) {
        List<Usuario> results = repository.findByEmail(email);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> findById(long id) {
        return repository.findById(id);
    }

    @Transactional
    public Usuario save(Usuario usuario) {
        return repository.save(usuario);
    }

    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return !repository.findByEmail(email).isEmpty();
    }

    /**
     * Registra un nuevo usuario con la contraseña codificada con BCrypt.
     * El estado se fuerza siempre a "P" (pendiente de confirmación).
     */
    @Transactional
    public UsuarioResponse registrar(UsuarioRegistroRequest request) {
        if (existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException(request.getEmail());
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(request.getNombre());
        usuario.setApellidos(request.getApellidos());
        usuario.setEmail(request.getEmail());
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setEstado("P");

        return UsuarioResponse.from(repository.save(usuario));
    }

    /**
     * Devuelve los cursos comprados por un usuario.
     */
    @Transactional(readOnly = true)
    public List<CursoResponse> obtenerCursosComprados(long id) {
        Usuario usuario = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));
        return usuario.getMisCursosComprados().stream()
                .map(CursoResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * Añade los cursos indicados a los cursos comprados de un usuario.
     */
    @Transactional
    public void agregarCursosComprados(long id, List<Long> cursoIds) {
        Usuario usuario = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));

        for (Long cursoId : cursoIds) {
            Curso curso = cursoRepository.findById(cursoId)
                    .orElseThrow(() -> new ResourceNotFoundException("Curso no encontrado con id: " + cursoId));
            usuario.getMisCursosComprados().add(curso);
        }

        repository.save(usuario);
    }
}
