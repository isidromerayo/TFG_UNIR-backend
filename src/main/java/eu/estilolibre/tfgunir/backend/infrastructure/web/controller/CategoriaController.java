package eu.estilolibre.tfgunir.backend.infrastructure.web.controller;

import eu.estilolibre.tfgunir.backend.application.port.in.CategoriaUseCase;
import eu.estilolibre.tfgunir.backend.domain.modelo.Categoria;
import eu.estilolibre.tfgunir.backend.dto.CursoResponse;
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
 * Controlador REST para operaciones de categorías.
 * 
 * <p>Este es un adapter de entrada (driving adapter) que expone
 * la funcionalidad de categorías a través de una API REST.</p>
 * 
 * <p>Usa el caso de uso {@link CategoriaUseCase} para acceder
 * a la lógica de aplicación sin depender de la implementación.</p>
 */
@RestController
@RequestMapping("/api/categorias")
@RequiredArgsConstructor
@Tag(name = "Categorías", description = "Operaciones de gestión de categorías")
public class CategoriaController {

    private final CategoriaUseCase categoriaUseCase;

    @Operation(
        summary = "Obtener todas las categorías",
        description = "Devuelve la lista completa de categorías disponibles"
    )
    @ApiResponse(responseCode = "200", description = "Lista de categorías",
        content = @Content(array = @ArraySchema(schema = @Schema(implementation = CategoriaResponse.class))))
    @GetMapping
    public ResponseEntity<List<CategoriaResponse>> obtenerTodas() {
        List<Categoria> categorias = categoriaUseCase.obtenerTodas();
        List<CategoriaResponse> respuestas = categorias.stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(respuestas);
    }

    @Operation(
        summary = "Obtener categoría por ID",
        description = "Devuelve una categoría específica por su identificador"
    )
    @ApiResponse(responseCode = "200", description = "Categoría encontrada",
        content = @Content(schema = @Schema(implementation = CategoriaResponse.class)))
    @ApiResponse(responseCode = "404", description = "Categoría no encontrada")
    @GetMapping("/{id}")
    public ResponseEntity<CategoriaResponse> obtenerPorId(@PathVariable Long id) {
        Categoria categoria = categoriaUseCase.obtenerPorId(id);
        return ResponseEntity.ok(toResponse(categoria));
    }

    @Operation(
        summary = "Crear nueva categoría",
        description = "Crea una nueva categoría en el sistema"
    )
    @ApiResponse(responseCode = "201", description = "Categoría creada",
        content = @Content(schema = @Schema(implementation = CategoriaResponse.class)))
    @PostMapping
    public ResponseEntity<CategoriaResponse> crear(@RequestBody CategoriaRequest request) {
        Categoria categoria = toDomain(request);
        Categoria creada = categoriaUseCase.crear(categoria);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(creada));
    }

    @Operation(
        summary = "Actualizar categoría",
        description = "Actualiza una categoría existente"
    )
    @ApiResponse(responseCode = "200", description = "Categoría actualizada",
        content = @Content(schema = @Schema(implementation = CategoriaResponse.class)))
    @ApiResponse(responseCode = "404", description = "Categoría no encontrada")
    @PutMapping("/{id}")
    public ResponseEntity<CategoriaResponse> actualizar(
            @PathVariable Long id, 
            @RequestBody CategoriaRequest request) {
        Categoria categoria = toDomain(request);
        Categoria actualizada = categoriaUseCase.actualizar(id, categoria);
        return ResponseEntity.ok(toResponse(actualizada));
    }

    @Operation(
        summary = "Eliminar categoría",
        description = "Elimina una categoría del sistema"
    )
    @ApiResponse(responseCode = "204", description = "Categoría eliminada")
    @ApiResponse(responseCode = "404", description = "Categoría no encontrada")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        categoriaUseCase.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // ==================== MAPPERS ====================

    private Categoria toDomain(CategoriaRequest request) {
        return new Categoria(request.getNombre(), request.getDescripcion());
    }

    private CategoriaResponse toResponse(Categoria domain) {
        CategoriaResponse response = new CategoriaResponse();
        response.setId(domain.getId());
        response.setNombre(domain.getNombre());
        response.setDescripcion(domain.getDescripcion());
        return response;
    }

    // ==================== DTOs ====================

    /**
     * DTO para recibir datos de categoría en requests.
     */
    public static class CategoriaRequest {
        private String nombre;
        private String descripcion;

        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

        public String getDescripcion() {
            return descripcion;
        }

        public void setDescripcion(String descripcion) {
            this.descripcion = descripcion;
        }
    }

    /**
     * DTO para enviar datos de categoría en responses.
     */
    public static class CategoriaResponse {
        private Long id;
        private String nombre;
        private String descripcion;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

        public String getDescripcion() {
            return descripcion;
        }

        public void setDescripcion(String descripcion) {
            this.descripcion = descripcion;
        }
    }
}
