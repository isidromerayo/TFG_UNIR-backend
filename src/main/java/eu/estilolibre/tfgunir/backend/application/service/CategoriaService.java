package eu.estilolibre.tfgunir.backend.application.service;

import eu.estilolibre.tfgunir.backend.application.port.in.CategoriaUseCase;
import eu.estilolibre.tfgunir.backend.application.port.out.CategoriaPort;
import eu.estilolibre.tfgunir.backend.domain.modelo.Categoria;
import eu.estilolibre.tfgunir.backend.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementación del caso de uso para categorías.
 * 
 * <p>Esta clase implementa la interfaz {@link CategoriaUseCase} y orchestra
 * las operaciones de negocio relacionadas con categorías.</p>
 * 
 * <p>Usa el puerto de salida {@link CategoriaPort} para acceder a los datos
 * sin depender directamente de la implementación de persistencia.</p>
 */
@Service
@RequiredArgsConstructor
public class CategoriaService implements CategoriaUseCase {

    private final CategoriaPort categoriaPort;

    @Override
    public List<Categoria> obtenerTodas() {
        return categoriaPort.findAll();
    }

    @Override
    public Categoria obtenerPorId(Long id) {
        return categoriaPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la categoría con id: " + id));
    }

    @Override
    public Categoria crear(Categoria categoria) {
        return categoriaPort.save(categoria);
    }

    @Override
    public Categoria actualizar(Long id, Categoria categoria) {
        if (!categoriaPort.existsById(id)) {
            throw new ResourceNotFoundException("No se encontró la categoría con id: " + id);
        }
        // Actualizar el ID en el modelo de dominio
        categoria.setId(id);
        return categoriaPort.save(categoria);
    }

    @Override
    public void eliminar(Long id) {
        if (!categoriaPort.existsById(id)) {
            throw new ResourceNotFoundException("No se encontró la categoría con id: " + id);
        }
        categoriaPort.deleteById(id);
    }
}
