package eu.estilolibre.tfgunir.backend.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import tools.jackson.databind.ObjectMapper;

import eu.estilolibre.tfgunir.backend.model.Curso;
import eu.estilolibre.tfgunir.backend.model.Instructor;
import eu.estilolibre.tfgunir.backend.model.Usuario;

@SpringBootTest
class JacksonSerializationTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void serializarUsuario_debeExcluirPassword() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombre("Juan");
        usuario.setApellidos("Pérez");
        usuario.setEmail("juan@example.com");
        usuario.setPassword("secreto123");
        usuario.setEstado("A");

        String json = objectMapper.writeValueAsString(usuario);

        assertThat(json)
            .doesNotContain("secreto123")
            .doesNotContain("\"password\"")
            .contains("juan@example.com", "Juan");
    }

    @Test
    void serializarCurso_debeIncluirCamposCorrectos() {
        Curso curso = new Curso();
        curso.setId(1L);
        curso.setTitulo("Spring Boot Avanzado");
        curso.setDescripcion("Curso completo de Spring Boot");
        curso.setPrecio(new BigDecimal("49.99"));
        curso.setValoracionMedia(4.5);
        curso.setEstado("A");

        String json = objectMapper.writeValueAsString(curso);

        assertThat(json)
            .contains("Spring Boot Avanzado")
            .contains("49.99")
            .doesNotContain("hibernateLazyInitializer");
    }

    @Test
    void serializarInstructor_debeExcluirHibernateHandler() {
        Instructor instructor = new Instructor();
        instructor.setId(1L);
        instructor.setNombre("Carlos");
        instructor.setApellidos("García");
        instructor.setDescripcion("Instructor senior");

        String json = objectMapper.writeValueAsString(instructor);

        assertThat(json)
            .doesNotContain("hibernateLazyInitializer", "handler")
            .contains("Carlos");
    }

    @Test
    void deserializarCurso_debeCrearObjetoCorrecto() {
        String json = """
                {
                    "id": 1,
                    "titulo": "Java Básico",
                    "descripcion": "Fundamentos de Java",
                    "precio": 29.99,
                    "estado": "A"
                }
                """;

        Curso curso = objectMapper.readValue(json, Curso.class);

        assertThat(curso.getTitulo()).isEqualTo("Java Básico");
        assertThat(curso.getDescripcion()).isEqualTo("Fundamentos de Java");
        assertThat(curso.getPrecio()).isEqualByComparingTo("29.99");
    }

    @Test
    void fechas_debenSerializarseCorrectamente() {
        Curso curso = new Curso();
        curso.setId(1L);
        curso.setTitulo("Test");
        LocalDateTime fecha = LocalDateTime.now();
        curso.setFechaCreacion(fecha);

        String json = objectMapper.writeValueAsString(curso);

        assertThat(json)
                .contains("fechaCreacion")
                .contains("2026");
    }
}
