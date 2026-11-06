package eu.estilolibre.tfgunir.backend.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import eu.estilolibre.tfgunir.backend.dto.ApiError;
import eu.estilolibre.tfgunir.backend.dto.CursoResponse;
import eu.estilolibre.tfgunir.backend.dto.UsuarioRegistroRequest;
import eu.estilolibre.tfgunir.backend.dto.UsuarioResponse;
import eu.estilolibre.tfgunir.backend.service.UsuarioService;

/**
 * Controlador de usuarios.
 * 
 * Expone únicamente las operaciones necesarias para el registro y la gestión
 * de cursos comprados, manteniendo oculto el listado global de usuarios.
 */
@Tag(name = "Usuarios", description = "Operaciones de registro de usuarios y gestión de cursos comprados")
@RestController
@RequestMapping("/api/usuarios")
@Validated
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Operation(
        summary = "Registrar un nuevo usuario",
        description = "Crea un usuario con la contraseña codificada con BCrypt y estado pendiente de confirmación."
    )
    @ApiResponse(responseCode = "201", description = "Usuario creado correctamente",
        content = @Content(schema = @Schema(implementation = UsuarioResponse.class)))
    @ApiResponse(responseCode = "400", description = "Validación de entrada fallida",
        content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "409", description = "Ya existe un usuario con ese email",
        content = @Content(schema = @Schema(implementation = ApiError.class)))
    @PostMapping
    public ResponseEntity<UsuarioResponse> registrar(@Valid @RequestBody UsuarioRegistroRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.registrar(request));
    }

    @Operation(
        summary = "Cursos comprados de un usuario",
        description = "Devuelve la lista de cursos que el usuario ha comprado."
    )
    @ApiResponse(responseCode = "200", description = "Lista de cursos comprados")
    @ApiResponse(responseCode = "401", description = "No autenticado",
        content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "403", description = "El recurso pertenece a otro usuario",
        content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "404", description = "Usuario no encontrado",
        content = @Content(schema = @Schema(implementation = ApiError.class)))
    @GetMapping("/{id}/cursos")
    public ResponseEntity<List<CursoResponse>> obtenerCursosComprados(@PathVariable long id,
            Authentication authentication) {
        verificarPropietario(id, authentication);
        return ResponseEntity.ok(usuarioService.obtenerCursosComprados(id));
    }

    @Operation(
        summary = "Añadir cursos comprados a un usuario",
        description = "Asocia los cursos indicados (text/uri-list) a los cursos comprados del usuario."
    )
    @ApiResponse(responseCode = "201", description = "Cursos añadidos correctamente")
    @ApiResponse(responseCode = "401", description = "No autenticado",
        content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "403", description = "El recurso pertenece a otro usuario",
        content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "404", description = "Usuario o curso no encontrado",
        content = @Content(schema = @Schema(implementation = ApiError.class)))
    @PostMapping("/{id}/misCursosComprados")
    public ResponseEntity<Void> agregarMisCursos(@PathVariable long id, @RequestBody String body,
            Authentication authentication) {
        verificarPropietario(id, authentication);
        usuarioService.agregarCursosComprados(id, parseCursoIds(body));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * Comprueba que el usuario autenticado es el dueño del recurso solicitado.
     *
     * <p>El identificador del path no se usa para resolver al usuario: siempre se
     * compara con el que viaja en el token, para impedir IDOR/BOLA. Se responde 403
     * (no 404) también cuando el id no existe, para no permitir enumeración.</p>
     *
     * @param id           identificador recibido en la ruta
     * @param authentication autenticación establecida por el filtro JWT
     * @throws AccessDeniedException si no hay autenticación o el id no coincide
     */
    private void verificarPropietario(long id, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("No autenticado");
        }

        long idAutenticado = usuarioService.findByEmail(authentication.getName())
                .orElseThrow(() -> new AccessDeniedException("No autenticado"))
                .getId();

        if (idAutenticado != id) {
            throw new AccessDeniedException("No puedes acceder a los datos de otro usuario");
        }
    }

    /**
     * Extrae los identificadores numéricos de las URIs de cursos contenidas
     * en el cuerpo (separadas por comas o saltos de línea).
     *
     * <p>Implementado sin regex (java:S8786): un barrido lineal por los
     * caracteres finales de cada token es suficiente y evita el backtracking.</p>
     *
     * @param body contenido text/uri-list
     * @return lista de ids de curso extraídos
     */
    static List<Long> parseCursoIds(String body) {
        if (body == null || body.isBlank()) {
            return List.of();
        }

        return Arrays.stream(body.split("[,;\\s]+"))
                .map(String::trim)
                .filter(token -> !token.isEmpty())
                .map(UsuarioController::extractId)
                .filter(Objects::nonNull)
                .toList();
    }

    private static Long extractId(String uri) {
        String end = uri.stripTrailing();
        int i = end.length();
        while (i > 0 && Character.isDigit(end.charAt(i - 1))) {
            i--;
        }
        return i < end.length() ? Long.valueOf(end.substring(i)) : null;
    }
}
