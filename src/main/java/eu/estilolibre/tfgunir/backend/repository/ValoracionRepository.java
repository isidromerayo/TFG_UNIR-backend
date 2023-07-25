package eu.estilolibre.tfgunir.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.CrossOrigin;

import eu.estilolibre.tfgunir.backend.model.Valoracion;

@CrossOrigin(origins = {"http://localhost:4200","http://localhost:3000","http://localhost:5173"})
@RepositoryRestResource(collectionResourceRel = "valoraciones", path = "valoraciones")
public interface ValoracionRepository extends  JpaRepository<Valoracion, Long> {
    
    /**
     * 
     * API: valoraciones/search/selectLastOpinions
     * 
     * @return
     */
    @Query("select v from Valoracion v order by v.fecha desc limit 3")
    List<Valoracion> selectLastOpinions();
}
