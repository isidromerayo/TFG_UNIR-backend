package eu.estilolibre.tfgunir.backend.infrastructure.persistence.mapper;

/**
 * Interfaz base para mappers entre entidades JPA y modelos de dominio.
 * 
 * <p>Esta interfaz define los métodos básicos para convertir entre
 * entidades JPA (infrastructure layer) y modelos de dominio (domain layer).</p>
 * 
 * <p>Implementaciones concretas deben manejar la conversión bidireccional
 * y el mapeo de relaciones entre objetos.</p>
 * 
 * @param <D> Tipo del modelo de dominio
 * @param <E> Tipo de la entidad JPA
 */
public interface BaseMapper<D, E> {

    /**
     * Convierte una entidad JPA a modelo de dominio.
     *
     * @param entity la entidad JPA a convertir
     * @return el modelo de dominio correspondienter
     */
    D toDomain(E entity);

    /**
     * Convierte un modelo de dominio a entidad JPA.
     *
     * @param domain el modelo de dominio a convertir
     * @return la entidad JPA correspondiente
     */
    E toEntity(D domain);
}
