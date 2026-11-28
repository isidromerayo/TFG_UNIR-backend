package eu.estilolibre.tfgunir.backend.application.port.in;

import eu.estilolibre.tfgunir.backend.domain.modelo.Contenido;

import java.util.List;

/**
 * Puerto de entrada (driving port) para casos de uso de contenidos.
 * 
 * <p>Esta interfaz define las operaciones que pueden ser realizadas
 * sobre contenidos desde la capa de aplicación. Representa los casos
 * de uso del negocio.</p>
 * 
 * <p>Las implementaciones concretas serán creadas en la capa de aplicación
 * y usarán puertos de salida para acceder a la infraestructura.</p>
 */
public interface ContenidoUseCase {

    /**
     * Obtiene todos los contenidos.
     *
     * @return lista de todos los contenidos
     */
    List<Contenido> obtenerTodos();

    /**
     * Obtiene contenidos por curso.
     *
     * @param cursoId el identificador del curso
     * @return lista de contenidos del curso
     */
    List<Contenido> obtenerPorCurso(Long cursoId);

    /**
     * Obtiene un contenido por su identificador.
     *
     * @param id el identificador del contenido
     * @return el contenido encontrado
     * @throws eu.estilolibre.tfgunir.backend.exception.ResourceNotFoundException si no se encuentra
     */
    Contenido obtenerPorId(Long id);

    /**
     * Crea un nuevo contenido.
     *
     * @param contenido el contenido a crear
     * @return el contenido creado con su identificador asignado
     */
    Contenido crear(Contenido contenido);

    /**
     * Actualiza un contenido existente.
     *
     * @param id        el identificador del contenido a actualizar
     * @param contenido los datos actualizados del contenido
     * @return el contenido actualizado
     * @throws eu.estilolibre.tfgunir.backend.exception.ResourceNotFoundException si no se encuentra
     */
    Contenido actualizar(Long id, Contenido contenido);

    /**
     * Elimina un contenido por su identificador.
     *
     * @param id el identificador del contenido a eliminar
     * @throws eu.estilolibre.tfgunir.backend.exception.ResourceNotFoundException si no se encuentra
     */
    void eliminar(Long id);
}
