package eu.estilolibre.tfgunir.backend.dto;

import java.math.BigDecimal;
import java.util.Date;

import eu.estilolibre.tfgunir.backend.model.Curso;

/**
 * Respuesta de un curso para las operaciones de usuario.
 * Evita la serialización directa de la entidad (asociaciones LAZY).
 *
 * @param id                 identificador del curso
 * @param titulo             título del curso
 * @param descripcion        descripción del curso
 * @param precio             precio del curso
 * @param valoracionMedia    valoración media del curso
 * @param fechaCreacion      fecha de creación del curso
 * @param fechaActualizacion fecha de actualización del curso
 * @param instructor         instructor del curso (o null)
 */
public record CursoResponse(
        long id,
        String titulo,
        String descripcion,
        BigDecimal precio,
        double valoracionMedia,
        Date fechaCreacion,
        Date fechaActualizacion,
        InstructorResponse instructor) {

    /**
     * Compact constructor: defensively copies mutable Date instances
     * to prevent SpotBugs EI_EXPOSE_REP2.
     */
    public CursoResponse {
        fechaCreacion = fechaCreacion == null ? null : new Date(fechaCreacion.getTime());
        fechaActualizacion = fechaActualizacion == null ? null : new Date(fechaActualizacion.getTime());
    }

    @Override
    public Date fechaCreacion() {
        return fechaCreacion == null ? null : new Date(fechaCreacion.getTime());
    }

    @Override
    public Date fechaActualizacion() {
        return fechaActualizacion == null ? null : new Date(fechaActualizacion.getTime());
    }

    public static CursoResponse from(Curso curso) {
        InstructorResponse instructorResponse = curso.getInstructor() == null
                ? null
                : InstructorResponse.from(curso.getInstructor());

        return new CursoResponse(
            curso.getId(),
            curso.getTitulo(),
            curso.getDescripcion(),
            curso.getPrecio(),
            curso.getValoracionMedia(),
            curso.getFechaCreacion(),
            curso.getFechaActualizacion(),
            instructorResponse
        );
    }
}
