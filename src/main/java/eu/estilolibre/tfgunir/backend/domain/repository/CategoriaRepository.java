package eu.estilolibre.tfgunir.backend.domain.repository;

import eu.estilolibre.tfgunir.backend.domain.modelo.Categoria;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de repositorio para la entidad Categoria en el dominio.
 * 
 * <p>Esta interfaz define el contrato para acceder a datos de categorías
 * desde la capa de dominio. No depende de JPA, Spring u otros frameworks.
 * </p>
 * 
 * <p>Las implementaciones concretas (adapters) serán creadas en la capa
 * de infraestructura y deben implementar esta interfaz.</p>
 */
public interface CategoriaRepository {

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
