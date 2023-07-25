package eu.estilolibre.tfgunir.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.CrossOrigin;

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
}
