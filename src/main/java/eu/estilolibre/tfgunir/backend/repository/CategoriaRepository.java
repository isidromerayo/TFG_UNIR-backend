package eu.estilolibre.tfgunir.backend.repository;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import eu.estilolibre.tfgunir.backend.model.Categoria;

/**
 * Repositorio JPA para Categorías (antiguo).
 * 
 * <p>Nota: La anotación @RepositoryRestResource ha sido removida para evitar
 * conflictos con el nuevo controlador REST en infrastructure/web/controller/CategoriaController.
 * Una vez que la migración hexagonal esté completa, este repositorio será eliminado.</p>
 */
public interface CategoriaRepository
        extends PagingAndSortingRepository<Categoria, Long>, CrudRepository<Categoria, Long> {

    // idea de optimización
    @SuppressWarnings("null")
    @Cacheable("categorias")
    List<Categoria> findAll();
}
