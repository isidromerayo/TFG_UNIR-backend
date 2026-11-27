package eu.estilolibre.tfgunir.backend.infrastructure.persistence.mapper;

import eu.estilolibre.tfgunir.backend.domain.modelo.Categoria;
import eu.estilolibre.tfgunir.backend.infrastructure.persistence.entity.CategoriaEntity;

import org.springframework.stereotype.Component;

/**
 * Mapper para convertir entre el modelo de dominio Categoria
 * y la entidad JPA CategoriaEntity.
 * 
 * <p>Este mapper maneja la conversión bidireccional, permitiendo
 * separar completamente la capa de dominio de la capa de persistencia.</p>
 */
@Component
public class CategoriaMapper implements BaseMapper<Categoria, CategoriaEntity> {

    /**
     * Convierte una entidad JPA a modelo de dominio.
     *
     * @param entity la entidad JPA a convertir
     * @return el modelo de dominio correspondienter, o null si la entidad es null
     */
    @Override
    public Categoria toDomain(CategoriaEntity entity) {
        if (entity == null) {
            return null;
        }
        return new Categoria(
                entity.getId(),
                entity.getNombre(),
                entity.getDescripcion()
        );
    }

    /**
     * Convierte un modelo de dominio a entidad JPA.
     *
     * @param domain el modelo de dominio a convertir
     * @return la entidad JPA correspondiente, o null si el dominio es null
     */
    @Override
    public CategoriaEntity toEntity(Categoria domain) {
        if (domain == null) {
            return null;
        }
        CategoriaEntity entity = new CategoriaEntity();
        entity.setId(domain.getId());
        entity.setNombre(domain.getNombre());
        entity.setDescripcion(domain.getDescripcion());
        return entity;
    }
}
