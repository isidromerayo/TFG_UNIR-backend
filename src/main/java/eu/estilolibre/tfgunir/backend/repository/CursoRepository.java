package eu.estilolibre.tfgunir.backend.repository;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

import eu.estilolibre.tfgunir.backend.model.Curso;

@RepositoryRestResource(collectionResourceRel = "cursos", path = "cursos")
public interface CursoRepository extends PagingAndSortingRepository<Curso, Long> , CrudRepository<Curso, Long> {
    
    /**
     * @return
     */
    @Query(value = "SELECT c FROM Curso c ORDER BY c.valoracionMedia DESC")
    List<Curso> selectMorePoints();

    /**
     * @return Top 3 courses by rating
     */
    @Query(nativeQuery = true, value = "SELECT * FROM cursos ORDER BY valoracion_media DESC LIMIT 3")
    List<Curso> selectMorePointsTop3();

    /**
     * @return
     */
    @Query(value = "SELECT c FROM Curso c ORDER BY c.fechaActualizacion DESC")
    List<Curso> selectLastUpdates();

    /**
     * @return Top 3 most recently updated courses
     */
    @Query(nativeQuery = true, value = "SELECT * FROM cursos ORDER BY fecha_actualizacion DESC LIMIT 3")
    List<Curso> selectLastUpdatesTop3();
    

    /**
     * 
     * @return
     */
    List<Curso> findByTituloContaining(String titulo);
    
    @Configuration
    static class RepositoryConfig implements RepositoryRestConfigurer {
        @Override
        public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config,
                CorsRegistry corsRegistry) {
            config.exposeIdsFor(Curso.class);
        }
    }
}
