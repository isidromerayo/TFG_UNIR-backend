package eu.estilolibre.tfgunir.backend.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.CrossOrigin;

import eu.estilolibre.tfgunir.backend.model.Categoria;

@CrossOrigin(origins = {"http://localhost:4200","http://localhost:3000","http://localhost:5173"})
@RepositoryRestResource(collectionResourceRel = "categorias", path = "categorias")
public interface CategoriaRepository extends PagingAndSortingRepository<Categoria, Long> , CrudRepository<Categoria, Long> {
    
}
