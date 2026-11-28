package eu.estilolibre.tfgunir.backend.infrastructure.persistence.mapper;

import eu.estilolibre.tfgunir.backend.domain.modelo.Contenido;
import eu.estilolibre.tfgunir.backend.infrastructure.persistence.entity.ContenidoEntity;

import org.springframework.stereotype.Component;

/**
 * Mapper para convertir entre el modelo de dominio Contenido
 * y la entidad JPA ContenidoEntity.
 * 
 * <p>Este mapper maneja la conversión bidireccional, permitiendo
 * separar completamente la capa de dominio de la capa de persistencia.</p>
 */
@Component
public class ContenidoMapper implements BaseMapper<Contenido, ContenidoEntity> {

    /**
     * Convierte una entidad JPA a modelo de dominio.
     *
     * @param entity la entidad JPA a convertir
     * @return el modelo de dominio correspondiente, o null si la entidad es null
     */
    @Override
    public Contenido toDomain(ContenidoEntity entity) {
        if (entity == null) {
            return null;
        }
        return new Contenido(
                entity.getId(),
                entity.getTitulo(),
                entity.getDocumento(),
                entity.getOrden()
        );
    }

    /**
     * Convierte un modelo de dominio a entidad JPA.
     *
     * @param domain el modelo de dominio a convertir
     * @return la entidad JPA correspondiente, o null si el dominio es null
     */
    @Override
    public ContenidoEntity toEntity(Contenido domain) {
        if (domain == null) {
            return null;
        }
        ContenidoEntity entity = new ContenidoEntity();
        entity.setId(domain.getId());
        entity.setTitulo(domain.getTitulo());
        entity.setDocumento(domain.getDocumento());
        entity.setOrden(domain.getOrden());
        return entity;
    }
}
