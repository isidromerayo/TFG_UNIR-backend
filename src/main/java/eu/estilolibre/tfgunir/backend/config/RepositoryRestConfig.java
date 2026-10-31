package eu.estilolibre.tfgunir.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

import eu.estilolibre.tfgunir.backend.model.Categoria;
import eu.estilolibre.tfgunir.backend.model.Curso;
import eu.estilolibre.tfgunir.backend.model.Valoracion;

/**
 * Exposición de ids de entidad en las respuestas de Spring Data REST para las
 * categorías, los cursos y las valoraciones.
 *
 * <p>Extraída de las interfaces de repositorio (java:S9398): las configuraciones
 * Spring no deben anidarse dentro de interfaces de datos.</p>
 */
@Configuration
public class RepositoryRestConfig implements RepositoryRestConfigurer {

    @Override
    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config,
            CorsRegistry corsRegistry) {
        config.exposeIdsFor(Categoria.class, Curso.class, Valoracion.class);
    }
}
