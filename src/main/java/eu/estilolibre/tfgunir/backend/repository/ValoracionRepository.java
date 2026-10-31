package eu.estilolibre.tfgunir.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import eu.estilolibre.tfgunir.backend.model.Valoracion;

@RepositoryRestResource(collectionResourceRel = "valoraciones", path = "valoraciones")
public interface ValoracionRepository extends JpaRepository<Valoracion, Long> {

    /**
     * API: valoraciones/search/selectLastOpinions
     * 
     * @return List of the last 3 valoraciones ordered by fecha descending
     */
    @RestResource(path = "selectLastOpinions", rel = "selectLastOpinions")
    List<Valoracion> findFirst3ByOrderByFechaDescIdDesc();
}
