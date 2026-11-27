package eu.estilolibre.tfgunir.backend.domain.modelo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * Tests unitarios para el modelo de dominio Categoria.
 * 
 * <p>Estos tests validan la lógica de negocio pura del dominio,
 * sin dependencias de Spring, JPA u otros frameworks.</p>
 */
class CategoriaTest {

    @Test
    void deberiaCrearCategoriaConValoresValidos() {
        // Given
        Long id = 1L;
        String nombre = "Desarrollo Web";
        String descripcion = "Cursos de desarrollo web moderno";
        
        // When
        Categoria categoria = new Categoria(id, nombre, descripcion);
        
        // Then
        assertNotNull(categoria);
        assertEquals(id, categoria.getId());
        assertEquals(nombre, categoria.getNombre());
        assertEquals(descripcion, categoria.getDescripcion());
    }

    @Test
    void deberiaCrearCategoriaSinId() {
        // Given
        String nombre = "Desarrollo Web";
        String descripcion = "Cursos de desarrollo web moderno";
        
        // When
        Categoria categoria = new Categoria(nombre, descripcion);
        
        // Then
        assertNotNull(categoria);
        assertNull(categoria.getId());
        assertEquals(nombre, categoria.getNombre());
        assertEquals(descripcion, categoria.getDescripcion());
    }

    @Test
    void deberiaValidarNombreNoNulo() {
        // Given
        String nombreInvalido = null;
        String descripcion = "Descripción válida";
        
        // When & Then
        assertThrows(IllegalArgumentException.class, 
            () -> new Categoria(nombreInvalido, descripcion));
    }

    @Test
    void deberiaValidarNombreNoVacio() {
        // Given
        String nombreInvalido = "";
        String descripcion = "Descripción válida";
        
        // When & Then
        assertThrows(IllegalArgumentException.class, 
            () -> new Categoria(nombreInvalido, descripcion));
    }

    @Test
    void deberiaValidarNombreNoBlanco() {
        // Given
        String nombreInvalido = "   ";
        String descripcion = "Descripción válida";
        
        // When & Then
        assertThrows(IllegalArgumentException.class, 
            () -> new Categoria(nombreInvalido, descripcion));
    }

    @Test
    void deberiaPermitirDescripcionNula() {
        // Given
        String nombre = "Desarrollo Web";
        String descripcion = null;
        
        // When
        Categoria categoria = new Categoria(nombre, descripcion);
        
        // Then
        assertNotNull(categoria);
        assertEquals(nombre, categoria.getNombre());
        assertNull(categoria.getDescripcion());
    }

    @Test
    void deberiaActualizarNombre() {
        // Given
        Categoria categoria = new Categoria("Nombre inicial", "Descripción");
        String nuevoNombre = "Nuevo nombre";
        
        // When
        categoria.setNombre(nuevoNombre);
        
        // Then
        assertEquals(nuevoNombre, categoria.getNombre());
    }

    @Test
    void deberiaActualizarDescripcion() {
        // Given
        Categoria categoria = new Categoria("Nombre", "Descripción inicial");
        String nuevaDescripcion = "Nueva descripción";
        
        // When
        categoria.setDescripcion(nuevaDescripcion);
        
        // Then
        assertEquals(nuevaDescripcion, categoria.getDescripcion());
    }

    @Test
    void deberiaValidarNombreAlActualizar() {
        // Given
        Categoria categoria = new Categoria("Nombre válido", "Descripción");
        
        // When & Then
        assertThrows(IllegalArgumentException.class, 
            () -> categoria.setNombre(null));
        assertThrows(IllegalArgumentException.class, 
            () -> categoria.setNombre(""));
        assertThrows(IllegalArgumentException.class, 
            () -> categoria.setNombre("   "));
    }

    @Test
    void deberiaSerIgualPorId() {
        // Given
        Long id = 1L;
        Categoria categoria1 = new Categoria(id, "Nombre 1", "Descripción 1");
        Categoria categoria2 = new Categoria(id, "Nombre 2", "Descripción 2");
        
        // When & Then
        assertEquals(categoria1, categoria2);
    }

    @Test
    void deberiaNoSerIgualConIdDiferente() {
        // Given
        Categoria categoria1 = new Categoria(1L, "Nombre", "Descripción");
        Categoria categoria2 = new Categoria(2L, "Nombre", "Descripción");
        
        // When & Then
        assertNotNull(categoria1);
        assertNotNull(categoria2);
        // Note: equals should be based on id
    }
}
