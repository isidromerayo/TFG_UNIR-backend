package eu.estilolibre.tfgunir.backend.application.service;

import eu.estilolibre.tfgunir.backend.application.port.in.ContenidoUseCase;
import eu.estilolibre.tfgunir.backend.application.port.out.ContenidoPort;
import eu.estilolibre.tfgunir.backend.domain.modelo.Contenido;
import eu.estilolibre.tfgunir.backend.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementación del caso de uso para contenidos.
 * 
 * <p>Esta clase implementa la interfaz {@link ContenidoUseCase} y orchestra
 * las operaciones de negocio relacionadas con contenidos.</p>
 * 
 * <p>Usa el puerto de salida {@link ContenidoPort} para acceder a los datos
 * sin depender directamente de la implementación de persistencia.</p>
 */
@Service
@RequiredArgsConstructor
public class ContenidoService implements ContenidoUseCase {

    private final ContenidoPort contenidoPort;

    @Override
    public List<Contenido> obtenerTodos() {
        return contenidoPort.findAll();
    }

    @Override
    public List<Contenido> obtenerPorCurso(Long cursoId) {
        return contenidoPort.findByCursoId(cursoId);
    }

    @Override
    public Contenido obtenerPorId(Long id) {
        return contenidoPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el contenido con id: " + id));
    }

    @Override
    public Contenido crear(Contenido contenido) {
        return contenidoPort.save(contenido);
    }

    @Override
    public Contenido actualizar(Long id, Contenido contenido) {
        if (!contenidoPort.existsById(id)) {
            throw new ResourceNotFoundException("No se encontró el contenido con id: " + id);
        }
        contenido.setId(id);
        return contenidoPort.save(contenido);
    }

    @Override
    public void eliminar(Long id) {
        if (!contenidoPort.existsById(id)) {
            throw new ResourceNotFoundException("No se encontró el contenido con id: " + id);
        }
        contenidoPort.deleteById(id);
    }
}
