package eu.estilolibre.tfgunir.backend.repository;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

import eu.estilolibre.tfgunir.backend.model.Valoracion;

@RepositoryRestResource(collectionResourceRel = "valoraciones", path = "valoraciones")
public interface ValoracionRepository extends  JpaRepository<Valoracion, Long> {
    
    /**
     * 
     * API: valoraciones/search/selectLastOpinions
     * 
     * @return
     */
    List<Valoracion> findFirst3ByOrderByFechaDescIdDesc();

    @Configuration
    static class RepositoryConfig implements RepositoryRestConfigurer {
        @Override
        public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config,
                CorsRegistry corsRegistry) {
            config.exposeIdsFor(Valoracion.class);
        }
    }
}
