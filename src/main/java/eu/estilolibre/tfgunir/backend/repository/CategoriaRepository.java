package eu.estilolibre.tfgunir.backend.repository;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import eu.estilolibre.tfgunir.backend.model.Categoria;

@RepositoryRestResource(collectionResourceRel = "categorias", path = "categorias")
public interface CategoriaRepository
        extends PagingAndSortingRepository<Categoria, Long>, CrudRepository<Categoria, Long> {

    // idea de optimización
    @SuppressWarnings("null")
    @Cacheable("categorias")
    List<Categoria> findAll();
}
