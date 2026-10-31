package eu.estilolibre.tfgunir.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import eu.estilolibre.tfgunir.backend.model.Curso;

/**
 * Respuesta de un curso para las operaciones de usuario.
 * Evita la serialización directa de la entidad (asociaciones LAZY).
 *
 * <p>Las fechas se exponen como {@link LocalDateTime} (java.time, inmutable),
 * por lo que no requieren copias defensivas (java:S2143).</p>
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
        LocalDateTime fechaCreacion,
        LocalDateTime fechaActualizacion,
        InstructorResponse instructor) {

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
