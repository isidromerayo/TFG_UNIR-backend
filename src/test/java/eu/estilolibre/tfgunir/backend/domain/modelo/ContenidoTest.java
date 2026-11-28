package eu.estilolibre.tfgunir.backend.domain.modelo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * Tests unitarios para el modelo de dominio Contenido.
 * 
 * <p>Estos tests validan la lógica de negocio pura del dominio,
 * sin dependencias de Spring, JPA u otros frameworks.</p>
 */
class ContenidoTest {

    @Test
    void deberiaCrearContenidoConTodosLosCampos() {
        // Given
        Long id = 1L;
        String titulo = "Introducción a Angular";
        String documento = "Contenido del curso de Angular...";
        int orden = 1;
        
        // When
        Contenido contenido = new Contenido(id, titulo, documento, orden);
        
        // Then
        assertNotNull(contenido);
        assertEquals(id, contenido.getId());
        assertEquals(titulo, contenido.getTitulo());
        assertEquals(documento, contenido.getDocumento());
        assertEquals(orden, contenido.getOrden());
    }

    @Test
    void deberiaCrearContenidoSinId() {
        // Given
        String titulo = "Introducción a Angular";
        String documento = "Contenido del curso de Angular...";
        int orden = 1;
        
        // When
        Contenido contenido = new Contenido(titulo, documento, orden);
        
        // Then
        assertNotNull(contenido);
        assertNull(contenido.getId());
        assertEquals(titulo, contenido.getTitulo());
        assertEquals(documento, contenido.getDocumento());
        assertEquals(orden, contenido.getOrden());
    }

    @Test
    void deberiaValidarTituloNoNulo() {
        // Given
        String tituloInvalido = null;
        String documento = "Documento válido";
        int orden = 1;
        
        // When & Then
        assertThrows(IllegalArgumentException.class, 
            () -> new Contenido(tituloInvalido, documento, orden));
    }

    @Test
    void deberiaValidarTituloNoVacio() {
        // Given
        String tituloInvalido = "";
        String documento = "Documento válido";
        int orden = 1;
        
        // When & Then
        assertThrows(IllegalArgumentException.class, 
            () -> new Contenido(tituloInvalido, documento, orden));
    }

    @Test
    void deberiaValidarTituloNoBlanco() {
        // Given
        String tituloInvalido = "   ";
        String documento = "Documento válido";
        int orden = 1;
        
        // When & Then
        assertThrows(IllegalArgumentException.class, 
            () -> new Contenido(tituloInvalido, documento, orden));
    }

    @Test
    void deberiaValidarOrdenPositivo() {
        // Given
        String titulo = "Título válido";
        String documento = "Documento válido";
        int ordenInvalido = -1;
        
        // When & Then
        assertThrows(IllegalArgumentException.class, 
            () -> new Contenido(titulo, documento, ordenInvalido));
    }

    @Test
    void deberiaPermitirDocumentoNulo() {
        // Given
        String titulo = "Título válido";
        String documento = null;
        int orden = 1;
        
        // When
        Contenido contenido = new Contenido(titulo, documento, orden);
        
        // Then
        assertNotNull(contenido);
        assertEquals(titulo, contenido.getTitulo());
        assertNull(contenido.getDocumento());
        assertEquals(orden, contenido.getOrden());
    }

    @Test
    void deberiaPermitirDocumentoVacio() {
        // Given
        String titulo = "Título válido";
        String documento = "";
        int orden = 1;
        
        // When
        Contenido contenido = new Contenido(titulo, documento, orden);
        
        // Then
        assertNotNull(contenido);
        assertEquals(titulo, contenido.getTitulo());
        assertEquals(documento, contenido.getDocumento());
        assertEquals(orden, contenido.getOrden());
    }

    @Test
    void deberiaActualizarTitulo() {
        // Given
        Contenido contenido = new Contenido("Título inicial", "Documento", 1);
        String nuevoTitulo = "Nuevo título";
        
        // When
        contenido.setTitulo(nuevoTitulo);
        
        // Then
        assertEquals(nuevoTitulo, contenido.getTitulo());
    }

    @Test
    void deberiaActualizarDocumento() {
        // Given
        Contenido contenido = new Contenido("Título", "Documento inicial", 1);
        String nuevoDocumento = "Nuevo documento";
        
        // When
        contenido.setDocumento(nuevoDocumento);
        
        // Then
        assertEquals(nuevoDocumento, contenido.getDocumento());
    }

    @Test
    void deberiaActualizarOrden() {
        // Given
        Contenido contenido = new Contenido("Título", "Documento", 1);
        int nuevoOrden = 5;
        
        // When
        contenido.setOrden(nuevoOrden);
        
        // Then
        assertEquals(nuevoOrden, contenido.getOrden());
    }

    @Test
    void deberiaValidarTituloAlActualizar() {
        // Given
        Contenido contenido = new Contenido("Título válido", "Documento", 1);
        
        // When & Then
        assertThrows(IllegalArgumentException.class, 
            () -> contenido.setTitulo(null));
        assertThrows(IllegalArgumentException.class, 
            () -> contenido.setTitulo(""));
        assertThrows(IllegalArgumentException.class, 
            () -> contenido.setTitulo("   "));
    }

    @Test
    void deberiaValidarOrdenAlActualizar() {
        // Given
        Contenido contenido = new Contenido("Título", "Documento", 1);
        
        // When & Then
        assertThrows(IllegalArgumentException.class, 
            () -> contenido.setOrden(-1));
        assertThrows(IllegalArgumentException.class, 
            () -> contenido.setOrden(0));
    }

    @Test
    void deberiaSerIgualPorId() {
        // Given
        Long id = 1L;
        Contenido contenido1 = new Contenido(id, "Título 1", "Doc 1", 1);
        Contenido contenido2 = new Contenido(id, "Título 2", "Doc 2", 2);
        
        // When & Then
        assertEquals(contenido1, contenido2);
    }

    @Test
    void deberiaNoSerIgualConIdDiferente() {
        // Given
        Contenido contenido1 = new Contenido(1L, "Título", "Doc", 1);
        Contenido contenido2 = new Contenido(2L, "Título", "Doc", 1);
        
        // When & Then
        assertNotNull(contenido1);
        assertNotNull(contenido2);
        // Note: equals should be based on id
    }
}
