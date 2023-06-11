package eu.estilolibre.tfgunir.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import eu.estilolibre.tfgunir.backend.model.Curso;

@RepositoryRestResource(collectionResourceRel = "cursos", path = "cursos")
public interface CursoRepository extends PagingAndSortingRepository<Curso, Long> , CrudRepository<Curso, Long> {
    
    @Query("select c from Curso c order by c.valoracionMedia desc limit 3")
    List<Curso> selectMorePoints();

    /**
     * @return
     */
    @Query("select c from Curso c order by c.fechaActualizacion desc limit 3")
    List<Curso> selectLastUpdates();
}
