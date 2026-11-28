package eu.estilolibre.tfgunir.backend.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import eu.estilolibre.tfgunir.backend.application.port.out.ContenidoPort;
import eu.estilolibre.tfgunir.backend.domain.modelo.Contenido;
import eu.estilolibre.tfgunir.backend.exception.ResourceNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

/**
 * Tests unitarios para el servicio de aplicación de contenidos.
 * 
 * <p>Estos tests validan la lógica de orquestación del caso de uso,
 * usando mocks para los puertos de salida.</p>
 */
@ExtendWith(MockitoExtension.class)
class ContenidoServiceTest {

    @Mock
    private ContenidoPort contenidoPort;

    @InjectMocks
    private ContenidoService contenidoService;

    private Contenido contenidoExistente;

    @BeforeEach
    void setUp() {
        contenidoExistente = new Contenido(1L, "Introducción", "Contenido del curso", 1);
    }

    @Test
    void deberiaObtenerTodosLosContenidos() {
        // Given
        List<Contenido> contenidos = List.of(
                contenidoExistente,
                new Contenido(2L, "Capítulo 1", "Contenido del capítulo 1", 2)
        );
        when(contenidoPort.findAll()).thenReturn(contenidos);
        
        // When
        List<Contenido> resultado = contenidoService.obtenerTodos();
        
        // Then
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
    }

    @Test
    void deberiaObtenerContenidosPorCurso() {
        // Given
        Long cursoId = 1L;
        List<Contenido> contenidos = List.of(
                new Contenido(1L, "Introducción", "Contenido del curso", 1),
                new Contenido(2L, "Capítulo 1", "Contenido del capítulo 1", 2)
        );
        when(contenidoPort.findByCursoId(cursoId)).thenReturn(contenidos);
        
        // When
        List<Contenido> resultado = contenidoService.obtenerPorCurso(cursoId);
        
        // Then
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
    }

    @Test
    void deberiaObtenerContenidoPorId() {
        // Given
        when(contenidoPort.findById(1L)).thenReturn(Optional.of(contenidoExistente));
        
        // When
        Contenido resultado = contenidoService.obtenerPorId(1L);
        
        // Then
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Introducción", resultado.getTitulo());
    }

    @Test
    void deberiaLanzarExcepcionAlObtenerContenidoNoExistente() {
        // Given
        when(contenidoPort.findById(999L)).thenReturn(Optional.empty());
        
        // When & Then
        assertThrows(ResourceNotFoundException.class, 
            () -> contenidoService.obtenerPorId(999L));
    }

    @Test
    void deberiaCrearContenido() {
        // Given
        Contenido nuevoContenido = new Contenido("Nuevo Contenido", "Documento", 1);
        Contenido contenidoCreado = new Contenido(1L, "Nuevo Contenido", "Documento", 1);
        when(contenidoPort.save(nuevoContenido)).thenReturn(contenidoCreado);
        
        // When
        Contenido resultado = contenidoService.crear(nuevoContenido);
        
        // Then
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Nuevo Contenido", resultado.getTitulo());
    }

    @Test
    void deberiaActualizarContenido() {
        // Given
        Long id = 1L;
        Contenido contenidoActualizado = new Contenido("Contenido Actualizado", "Nuevo documento", 2);
        when(contenidoPort.existsById(id)).thenReturn(true);
        when(contenidoPort.save(contenidoActualizado)).thenReturn(new Contenido(id, "Contenido Actualizado", "Nuevo documento", 2));
        
        // When
        Contenido resultado = contenidoService.actualizar(id, contenidoActualizado);
        
        // Then
        assertNotNull(resultado);
        assertEquals(id, resultado.getId());
        assertEquals("Contenido Actualizado", resultado.getTitulo());
    }

    @Test
    void deberiaLanzarExcepcionAlActualizarContenidoNoExistente() {
        // Given
        Long id = 999L;
        Contenido contenidoActualizado = new Contenido("Contenido Actualizado", "Nuevo documento", 2);
        when(contenidoPort.existsById(id)).thenReturn(false);
        
        // When & Then
        assertThrows(ResourceNotFoundException.class, 
            () -> contenidoService.actualizar(id, contenidoActualizado));
    }

    @Test
    void deberiaEliminarContenido() {
        // Given
        Long id = 1L;
        when(contenidoPort.existsById(id)).thenReturn(true);
        
        // When & Then (no exception should be thrown)
        contenidoService.eliminar(id);
    }

    @Test
    void deberiaLanzarExcepcionAlEliminarContenidoNoExistente() {
        // Given
        Long id = 999L;
        when(contenidoPort.existsById(id)).thenReturn(false);
        
        // When & Then
        assertThrows(ResourceNotFoundException.class, 
            () -> contenidoService.eliminar(id));
    }
}
