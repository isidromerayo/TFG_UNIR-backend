package eu.estilolibre.tfgunir.backend.infrastructure.web.controller;

import eu.estilolibre.tfgunir.backend.application.port.in.ContenidoUseCase;
import eu.estilolibre.tfgunir.backend.domain.modelo.Contenido;
import eu.estilolibre.tfgunir.backend.exception.ResourceNotFoundException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controlador REST para operaciones de contenidos.
 * 
 * <p>Este es un adapter de entrada (driving adapter) que expone
 * la funcionalidad de contenidos a través de una API REST.</p>
 * 
 * <p>Usa el caso de uso {@link ContenidoUseCase} para acceder
 * a la lógica de aplicación sin depender de la implementación.</p>
 */
@RestController
@RequestMapping("/api/contenidos")
@RequiredArgsConstructor
@Tag(name = "Contenidos", description = "Operaciones de gestión de contenidos")
public class ContenidoController {

    private final ContenidoUseCase contenidoUseCase;

    @Operation(
        summary = "Obtener todos los contenidos",
        description = "Devuelve la lista completa de contenidos"
    )
    @ApiResponse(responseCode = "200", description = "Lista de contenidos",
        content = @Content(array = @ArraySchema(schema = @Schema(implementation = ContenidoResponse.class))))
    @GetMapping
    public ResponseEntity<List<ContenidoResponse>> obtenerTodos() {
        List<Contenido> contenidos = contenidoUseCase.obtenerTodos();
        List<ContenidoResponse> respuestas = contenidos.stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(respuestas);
    }

    @Operation(
        summary = "Obtener contenidos por curso",
        description = "Devuelve los contenidos de un curso específico"
    )
    @ApiResponse(responseCode = "200", description = "Lista de contenidos del curso",
        content = @Content(array = @ArraySchema(schema = @Schema(implementation = ContenidoResponse.class))))
    @GetMapping("/curso/{cursoId}")
    public ResponseEntity<List<ContenidoResponse>> obtenerPorCurso(@PathVariable Long cursoId) {
        List<Contenido> contenidos = contenidoUseCase.obtenerPorCurso(cursoId);
        List<ContenidoResponse> respuestas = contenidos.stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(respuestas);
    }

    @Operation(
        summary = "Obtener contenido por ID",
        description = "Devuelve un contenido específico por su identificador"
    )
    @ApiResponse(responseCode = "200", description = "Contenido encontrado",
        content = @Content(schema = @Schema(implementation = ContenidoResponse.class)))
    @ApiResponse(responseCode = "404", description = "Contenido no encontrado")
    @GetMapping("/{id}")
    public ResponseEntity<ContenidoResponse> obtenerPorId(@PathVariable Long id) {
        Contenido contenido = contenidoUseCase.obtenerPorId(id);
        return ResponseEntity.ok(toResponse(contenido));
    }

    @Operation(
        summary = "Crear nuevo contenido",
        description = "Crea un nuevo contenido en el sistema"
    )
    @ApiResponse(responseCode = "201", description = "Contenido creado",
        content = @Content(schema = @Schema(implementation = ContenidoResponse.class)))
    @PostMapping
    public ResponseEntity<ContenidoResponse> crear(@RequestBody ContenidoRequest request) {
        Contenido contenido = toDomain(request);
        Contenido creado = contenidoUseCase.crear(contenido);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(creado));
    }

    @Operation(
        summary = "Actualizar contenido",
        description = "Actualiza un contenido existente"
    )
    @ApiResponse(responseCode = "200", description = "Contenido actualizado",
        content = @Content(schema = @Schema(implementation = ContenidoResponse.class)))
    @ApiResponse(responseCode = "404", description = "Contenido no encontrado")
    @PutMapping("/{id}")
    public ResponseEntity<ContenidoResponse> actualizar(
            @PathVariable Long id, 
            @RequestBody ContenidoRequest request) {
        Contenido contenido = toDomain(request);
        Contenido actualizado = contenidoUseCase.actualizar(id, contenido);
        return ResponseEntity.ok(toResponse(actualizado));
    }

    @Operation(
        summary = "Eliminar contenido",
        description = "Elimina un contenido del sistema"
    )
    @ApiResponse(responseCode = "204", description = "Contenido eliminado")
    @ApiResponse(responseCode = "404", description = "Contenido no encontrado")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        contenidoUseCase.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // ==================== MAPPERS ====================

    private Contenido toDomain(ContenidoRequest request) {
        return new Contenido(request.getTitulo(), request.getDocumento(), request.getOrden());
    }

    private ContenidoResponse toResponse(Contenido domain) {
        ContenidoResponse response = new ContenidoResponse();
        response.setId(domain.getId());
        response.setTitulo(domain.getTitulo());
        response.setDocumento(domain.getDocumento());
        response.setOrden(domain.getOrden());
        return response;
    }

    // ==================== DTOs ====================

    /**
     * DTO para recibir datos de contenido en requests.
     */
    public static class ContenidoRequest {
        private String titulo;
        private String documento;
        private int orden;

        public String getTitulo() {
            return titulo;
        }

        public void setTitulo(String titulo) {
            this.titulo = titulo;
        }

        public String getDocumento() {
            return documento;
        }

        public void setDocumento(String documento) {
            this.documento = documento;
        }

        public int getOrden() {
            return orden;
        }

        public void setOrden(int orden) {
            this.orden = orden;
        }
    }

    /**
     * DTO para enviar datos de contenido en responses.
     */
    public static class ContenidoResponse {
        private Long id;
        private String titulo;
        private String documento;
        private int orden;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getTitulo() {
            return titulo;
        }

        public void setTitulo(String titulo) {
            this.titulo = titulo;
        }

        public String getDocumento() {
            return documento;
        }

        public void setDocumento(String documento) {
            this.documento = documento;
        }

        public int getOrden() {
            return orden;
        }

        public void setOrden(int orden) {
            this.orden = orden;
        }
    }
}
