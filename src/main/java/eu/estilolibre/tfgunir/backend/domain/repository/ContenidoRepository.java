package eu.estilolibre.tfgunir.backend.domain.repository;

import eu.estilolibre.tfgunir.backend.domain.modelo.Contenido;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de repositorio para la entidad Contenido en el dominio.
 * 
 * <p>Esta interfaz define el contrato para acceder a datos de contenidos
 * desde la capa de dominio. No depende de JPA, Spring u otros frameworks.
 * </p>
 * 
 * <p>Las implementaciones concretas (adapters) serán creadas en la capa
 * de infraestructura y deben implementar esta interfaz.</p>
 */
public interface ContenidoRepository {

    /**
     * Busca un contenido por su identificador.
     *
     * @param id el identificador del contenido
     * @return Optional vacío si no se encuentra
     */
    Optional<Contenido> findById(Long id);

    /**
     * Busca todos los contenidos.
     *
     * @return lista de todos los contenidos
     */
    List<Contenido> findAll();

    /**
     * Busca contenidos por curso.
     *
     * @param cursoId el identificador del curso
     * @return lista de contenidos del curso
     */
    List<Contenido> findByCursoId(Long cursoId);

    /**
     * Guarda un contenido.
     *
     * @param contenido el contenido a guardar
     * @return el contenido guardado con su identificador asignado
     */
    Contenido save(Contenido contenido);

    /**
     * Elimina un contenido por su identificador.
     *
     * @param id el identificador del contenido a eliminar
     */
    void deleteById(Long id);

    /**
     * Verifica si existe un contenido con el identificador dado.
     *
     * @param id el identificador a verificar
     * @return true si existe, false en caso contrario
     */
    boolean existsById(Long id);
}
