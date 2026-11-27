package eu.estilolibre.tfgunir.backend.infrastructure.persistence.repository;

import eu.estilolibre.tfgunir.backend.infrastructure.persistence.entity.CategoriaEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio Spring Data JPA para la entidad CategoriaEntity.
 * 
 * <p>Este repositorio proporciona operaciones CRUD básicas para
 * la entidad CategoriaEntity en la base de datos.</p>
 * 
 * <p>No debe ser usado directamente por la capa de aplicación o dominio.
 * En su lugar, usar el adapter {@link eu.estilolibre.tfgunir.backend.infrastructure.persistence.CategoriaJpaAdapter}.</p>
 */
@Repository
public interface CategoriaJpaRepository extends JpaRepository<CategoriaEntity, Long> {
}
