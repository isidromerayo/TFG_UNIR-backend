package eu.estilolibre.tfgunir.backend.repository;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

import eu.estilolibre.tfgunir.backend.model.Curso;
import eu.estilolibre.tfgunir.backend.model.Usuario;
import java.util.List;

@RepositoryRestResource(collectionResourceRel = "usuarios", path = "usuarios")
public interface UsuarioRepository
        extends JpaRepository<Usuario, Long> {
    
    @Configuration
    static class RepositoryConfig implements RepositoryRestConfigurer {
        @Override
        public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config,
                CorsRegistry corsRegistry) {
            config.exposeIdsFor(Usuario.class);
        }
    }
    /**
     * 
     * @param email
     * @return List<Usuario>
     */
    public List<Usuario> findByEmail(String email);
}
