package eu.estilolibre.tfgunir.backend.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import eu.estilolibre.tfgunir.backend.model.Categoria;

@RepositoryRestResource(collectionResourceRel = "categorias", path = "categorias")
public interface CategoriaRepository extends PagingAndSortingRepository<Categoria, Long> , CrudRepository<Categoria, Long> {
    
}
