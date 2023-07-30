package eu.estilolibre.tfgunir.backend.repository;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

import eu.estilolibre.tfgunir.backend.model.Curso;

@CrossOrigin(origins = {"http://localhost:4200","http://localhost:3000","http://localhost:5173"})
@RepositoryRestResource(collectionResourceRel = "cursos", path = "cursos")
public interface CursoRepository extends PagingAndSortingRepository<Curso, Long> , CrudRepository<Curso, Long> {
    
    /**
     * @return
     */
    @Query("select c from Curso c order by c.valoracionMedia desc limit 3")
    List<Curso> selectMorePoints();

    /**
     * @return
     */
    @Query("select c from Curso c order by c.fechaActualizacion desc limit 3")
    List<Curso> selectLastUpdates();
    

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
