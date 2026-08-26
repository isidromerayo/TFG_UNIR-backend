package eu.estilolibre.tfgunir.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import eu.estilolibre.tfgunir.backend.model.Usuario;
import java.util.List;

@RepositoryRestResource(exported = false)
public interface UsuarioRepository
        extends JpaRepository<Usuario, Long> {

    /**
     * 
     * @param email
     * @return List<Usuario>
     */
    public List<Usuario> findByEmail(String email);
}
