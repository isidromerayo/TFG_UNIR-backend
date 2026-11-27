package eu.estilolibre.tfgunir.backend.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import eu.estilolibre.tfgunir.backend.application.port.out.CategoriaPort;
import eu.estilolibre.tfgunir.backend.domain.modelo.Categoria;
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
 * Tests unitarios para el servicio de aplicación de categorías.
 * 
 * <p>Estos tests validan la lógica de orquestación del caso de uso,
 * usando mocks para los puertos de salida.</p>
 */
@ExtendWith(MockitoExtension.class)
class CategoriaServiceTest {

    @Mock
    private CategoriaPort categoriaPort;

    @InjectMocks
    private CategoriaService categoriaService;

    private Categoria categoriaExistente;

    @BeforeEach
    void setUp() {
        categoriaExistente = new Categoria(1L, "Desarrollo Web", "Cursos de desarrollo web");
    }

    @Test
    void deberiaObtenerTodasLasCategorias() {
        // Given
        List<Categoria> categorias = List.of(
                categoriaExistente,
                new Categoria(2L, "Base de Datos", "Cursos de bases de datos")
        );
        when(categoriaPort.findAll()).thenReturn(categorias);
        
        // When
        List<Categoria> resultado = categoriaService.obtenerTodas();
        
        // Then
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
    }

    @Test
    void deberiaObtenerCategoriaPorId() {
        // Given
        when(categoriaPort.findById(1L)).thenReturn(Optional.of(categoriaExistente));
        
        // When
        Categoria resultado = categoriaService.obtenerPorId(1L);
        
        // Then
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Desarrollo Web", resultado.getNombre());
    }

    @Test
    void deberiaLanzarExcepcionAlObtenerCategoriaNoExistente() {
        // Given
        when(categoriaPort.findById(999L)).thenReturn(Optional.empty());
        
        // When & Then
        assertThrows(ResourceNotFoundException.class, 
            () -> categoriaService.obtenerPorId(999L));
    }

    @Test
    void deberiaCrearCategoria() {
        // Given
        Categoria nuevaCategoria = new Categoria("Nueva Categoría", "Descripción");
        Categoria categoriaCreada = new Categoria(1L, "Nueva Categoría", "Descripción");
        when(categoriaPort.save(nuevaCategoria)).thenReturn(categoriaCreada);
        
        // When
        Categoria resultado = categoriaService.crear(nuevaCategoria);
        
        // Then
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Nueva Categoría", resultado.getNombre());
    }

    @Test
    void deberiaActualizarCategoria() {
        // Given
        Long id = 1L;
        Categoria categoriaActualizada = new Categoria("Categoría Actualizada", "Nueva descripción");
        when(categoriaPort.existsById(id)).thenReturn(true);
        when(categoriaPort.save(categoriaActualizada)).thenReturn(new Categoria(id, "Categoría Actualizada", "Nueva descripción"));
        
        // When
        Categoria resultado = categoriaService.actualizar(id, categoriaActualizada);
        
        // Then
        assertNotNull(resultado);
        assertEquals(id, resultado.getId());
        assertEquals("Categoría Actualizada", resultado.getNombre());
    }

    @Test
    void deberiaLanzarExcepcionAlActualizarCategoriaNoExistente() {
        // Given
        Long id = 999L;
        Categoria categoriaActualizada = new Categoria("Categoría Actualizada", "Nueva descripción");
        when(categoriaPort.existsById(id)).thenReturn(false);
        
        // When & Then
        assertThrows(ResourceNotFoundException.class, 
            () -> categoriaService.actualizar(id, categoriaActualizada));
    }

    @Test
    void deberiaEliminarCategoria() {
        // Given
        Long id = 1L;
        when(categoriaPort.existsById(id)).thenReturn(true);
        
        // When & Then (no exception should be thrown)
        categoriaService.eliminar(id);
        // Verify no exception
    }

    @Test
    void deberiaLanzarExcepcionAlEliminarCategoriaNoExistente() {
        // Given
        Long id = 999L;
        when(categoriaPort.existsById(id)).thenReturn(false);
        
        // When & Then
        assertThrows(ResourceNotFoundException.class, 
            () -> categoriaService.eliminar(id));
    }
}
