package eu.estilolibre.tfgunir.backend.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.Date;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import eu.estilolibre.tfgunir.backend.model.Curso;
import eu.estilolibre.tfgunir.backend.model.Instructor;
import eu.estilolibre.tfgunir.backend.model.Usuario;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class JacksonSerializationTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void serializarUsuario_debeExcluirPassword() throws JsonProcessingException {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombre("Juan");
        usuario.setApellidos("Pérez");
        usuario.setEmail("juan@example.com");
        usuario.setPassword("secreto123");
        usuario.setEstado("A");

        String json = objectMapper.writeValueAsString(usuario);

        assertThat(json).doesNotContain("secreto123");
        assertThat(json).doesNotContain("\"password\"");
        assertThat(json).contains("juan@example.com");
        assertThat(json).contains("Juan");
    }

    @Test
    void serializarCurso_debeIncluirCamposCorrectos() throws JsonProcessingException {
        Curso curso = new Curso();
        curso.setId(1L);
        curso.setTitulo("Spring Boot Avanzado");
        curso.setDescripcion("Curso completo de Spring Boot");
        curso.setPrecio(new BigDecimal("49.99"));
        curso.setValoracionMedia(4.5);
        curso.setEstado("A");

        String json = objectMapper.writeValueAsString(curso);

        assertThat(json).contains("Spring Boot Avanzado");
        assertThat(json).contains("49.99");
        assertThat(json).doesNotContain("hibernateLazyInitializer");
    }

    @Test
    void serializarInstructor_debeExcluirHibernateHandler() throws JsonProcessingException {
        Instructor instructor = new Instructor();
        instructor.setId(1L);
        instructor.setNombre("Carlos");
        instructor.setApellidos("García");
        instructor.setDescripcion("Instructor senior");

        String json = objectMapper.writeValueAsString(instructor);

        assertThat(json).doesNotContain("hibernateLazyInitializer");
        assertThat(json).doesNotContain("handler");
        assertThat(json).contains("Carlos");
    }

    @Test
    void deserializarCurso_debeCrearObjetoCorrecto() throws JsonProcessingException {
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
    void fechas_debenSerializarseCorrectamente() throws JsonProcessingException {
        Curso curso = new Curso();
        curso.setId(1L);
        curso.setTitulo("Test");
        Date fecha = new Date();
        curso.setFechaCreacion(fecha);

        String json = objectMapper.writeValueAsString(curso);

        assertThat(json).contains("fechaCreacion");
        assertThat(json).contains("2026");
    }
}
