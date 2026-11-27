package eu.estilolibre.tfgunir.backend.application.port.in;

import eu.estilolibre.tfgunir.backend.domain.modelo.Categoria;

import java.util.List;

/**
 * Puerto de entrada (driving port) para casos de uso de categorías.
 * 
 * <p>Esta interfaz define las operaciones que pueden ser realizadas
 * sobre categorías desde la capa de aplicación. Representa los casos
 * de uso del negocio.</p>
 * 
 * <p>Las implementaciones concretas serán creadas en la capa de aplicación
 * y usarán puertos de salida para acceder a la infraestructura.</p>
 */
public interface CategoriaUseCase {

    /**
     * Obtiene todas las categorías.
     *
     * @return lista de todas las categorías
     */
    List<Categoria> obtenerTodas();

    /**
     * Obtiene una categoría por su identificador.
     *
     * @param id el identificador de la categoría
     * @return la categoría encontrada
     * @throws eu.estilolibre.tfgunir.backend.exception.ResourceNotFoundException si no se encuentra
     */
    Categoria obtenerPorId(Long id);

    /**
     * Crea una nueva categoría.
     *
     * @param categoria la categoría a crear
     * @return la categoría creada con su identificador asignado
     */
    Categoria crear(Categoria categoria);

    /**
     * Actualiza una categoría existente.
     *
     * @param id        el identificador de la categoría a actualizar
     * @param categoria los datos actualizados de la categoría
     * @return la categoría actualizada
     * @throws eu.estilolibre.tfgunir.backend.exception.ResourceNotFoundException si no se encuentra
     */
    Categoria actualizar(Long id, Categoria categoria);

    /**
     * Elimina una categoría por su identificador.
     *
     * @param id el identificador de la categoría a eliminar
     * @throws eu.estilolibre.tfgunir.backend.exception.ResourceNotFoundException si no se encuentra
     */
    void eliminar(Long id);
}
