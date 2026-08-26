package eu.estilolibre.tfgunir.backend.dto;

import eu.estilolibre.tfgunir.backend.model.Instructor;

/**
 * Resumen de un instructor para respuestas de la API.
 *
 * @param id         identificador del instructor
 * @param nombre     nombre del instructor
 * @param apellidos  apellidos del instructor
 * @param descripcion descripción del instructor
 */
public record InstructorResponse(long id, String nombre, String apellidos, String descripcion) {

    public static InstructorResponse from(Instructor instructor) {
        return new InstructorResponse(
            instructor.getId(),
            instructor.getNombre(),
            instructor.getApellidos(),
            instructor.getDescripcion()
        );
    }
}
