package eu.estilolibre.tfgunir.backend.application.port.out;

import eu.estilolibre.tfgunir.backend.domain.modelo.Categoria;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida (driven port) para operaciones de persistencia de categorías.
 * 
 * <p>Esta interfaz define el contrato para acceder a datos de categorías
 * desde la capa de aplicación. Es utilizada por los casos de uso para
 * interactuar con la capa de infraestructura.</p>
 * 
 * <p>La implementación concreta (adapter) se encontrará en la capa de
 * infraestructura y usará el repositorio de dominio correspondienter.</p>
 */
public interface CategoriaPort {

    /**
     * Busca una categoría por su identificador.
     *
     * @param id el identificador de la categoría
     * @return Optional vacío si no se encuentra
     */
    Optional<Categoria> findById(Long id);

    /**
     * Busca todas las categorías.
     *
     * @return lista de todas las categorías
     */
    List<Categoria> findAll();

    /**
     * Guarda una categoría.
     *
     * @param categoria la categoría a guardar
     * @return la categoría guardada con su identificador asignado
     */
    Categoria save(Categoria categoria);

    /**
     * Elimina una categoría por su identificador.
     *
     * @param id el identificador de la categoría a eliminar
     */
    void deleteById(Long id);

    /**
     * Verifica si existe una categoría con el identificador dado.
     *
     * @param id el identificador a verificar
     * @return true si existe, false en caso contrario
     */
    boolean existsById(Long id);
}
