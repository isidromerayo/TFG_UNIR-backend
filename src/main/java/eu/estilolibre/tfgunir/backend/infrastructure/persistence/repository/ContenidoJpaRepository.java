package eu.estilolibre.tfgunir.backend.infrastructure.persistence.repository;

import eu.estilolibre.tfgunir.backend.infrastructure.persistence.entity.ContenidoEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio Spring Data JPA para la entidad ContenidoEntity.
 * 
 * <p>Este repositorio proporciona operaciones CRUD básicas para
 * la entidad ContenidoEntity en la base de datos.</p>
 * 
 * <p>No debe ser usado directamente por la capa de aplicación o dominio.
 * En su lugar, usar el adapter {@link eu.estilolibre.tfgunir.backend.infrastructure.persistence.ContenidoJpaAdapter}.</p>
 */
@Repository
public interface ContenidoJpaRepository extends JpaRepository<ContenidoEntity, Long> {

    /**
     * Busca contenidos por curso.
     *
     * @param cursoId el identificador del curso
     * @return lista de contenidos del curso
     */
    List<ContenidoEntity> findByCursoId(Long cursoId);
}
