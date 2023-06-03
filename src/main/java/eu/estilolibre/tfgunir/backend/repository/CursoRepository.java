package eu.estilolibre.tfgunir.backend.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import eu.estilolibre.tfgunir.backend.model.Curso;

@RepositoryRestResource(collectionResourceRel = "cursos", path = "cursos")
public interface CursoRepository extends PagingAndSortingRepository<Curso, Long> , CrudRepository<Curso, Long> {
    
}
