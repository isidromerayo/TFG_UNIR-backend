package eu.estilolibre.tfgunir.backend.repository;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

import eu.estilolibre.tfgunir.backend.model.Categoria;

@RepositoryRestResource(collectionResourceRel = "categorias", path = "categorias")
public interface CategoriaRepository
        extends PagingAndSortingRepository<Categoria, Long>, CrudRepository<Categoria, Long> {

    @Configuration
    static class RepositoryConfig implements RepositoryRestConfigurer {
        @Override
        public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config,
                CorsRegistry corsRegistry) {
            config.exposeIdsFor(Categoria.class);
        }
    }
}
