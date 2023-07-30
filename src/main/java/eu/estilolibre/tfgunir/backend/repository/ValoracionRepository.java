package eu.estilolibre.tfgunir.backend.repository;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

import eu.estilolibre.tfgunir.backend.model.Valoracion;
@CrossOrigin(origins = {"http://localhost:4200","http://localhost:3000","http://localhost:5173"})
@RepositoryRestResource(collectionResourceRel = "valoraciones", path = "valoraciones")
public interface ValoracionRepository extends  JpaRepository<Valoracion, Long> {
    
    /**
     * 
     * API: valoraciones/search/selectLastOpinions
     * 
     * @return
     */
    @Query("select v from Valoracion v order by v.fecha desc limit 3")
    List<Valoracion> selectLastOpinions();

    @Configuration
    static class RepositoryConfig implements RepositoryRestConfigurer {
        @Override
        public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config,
                CorsRegistry corsRegistry) {
            config.exposeIdsFor(Valoracion.class);
        }
    }
}
