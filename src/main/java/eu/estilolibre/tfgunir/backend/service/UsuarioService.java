package eu.estilolibre.tfgunir.backend.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import eu.estilolibre.tfgunir.backend.model.Usuario;
import eu.estilolibre.tfgunir.backend.repository.UsuarioRepository;

@Service
public class UsuarioService {
    
    private final UsuarioRepository repository;

    public UsuarioService(UsuarioRepository repository) {
        this.repository = repository;
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
}