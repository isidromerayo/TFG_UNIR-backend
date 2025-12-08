package eu.estilolibre.tfgunir.backend.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

import eu.estilolibre.tfgunir.backend.model.Curso;
import eu.estilolibre.tfgunir.backend.model.Usuario;
import eu.estilolibre.tfgunir.backend.model.Valoracion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class ValoracionRepositoryTests {

    @Autowired
    ValoracionRepository repository;

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    CursoRepository cursoRepository;

    @Test
    void buscarTodasLasValoraciones() {
        // Create a new Usuario and Curso for this test
        Usuario testUsuario = new Usuario();
        testUsuario.setNombre("Test");
        testUsuario.setApellidos("User");
        testUsuario.setEmail("test@example.com");
        testUsuario.setPassword("password");
        testUsuario = usuarioRepository.save(testUsuario);

        Curso testCurso = new Curso();
        testCurso.setTitulo("Test Course");
        testCurso = cursoRepository.save(testCurso);

        // Insert a dummy Valoracion for this test using the created entities
        Valoracion dummyValoracion = new Valoracion();
        dummyValoracion.setEstudiante(testUsuario);
        dummyValoracion.setCurso(testCurso);
        dummyValoracion.setPuntuacion(3);
        dummyValoracion.setComentario("Dummy comment");
        repository.save(dummyValoracion);

        List<Valoracion> result = (List<Valoracion>) repository.findAll();
        assertThat(result.size(), is(equalTo(1)));
    }

    @Test
    void guardarValoracion() {
        Usuario usuario = usuarioRepository.findById(1L).orElse(null);
        Curso curso = cursoRepository.findById(1L).orElse(null);

        Valoracion nuevaValoracion = new Valoracion();
        nuevaValoracion.setEstudiante(usuario);
        nuevaValoracion.setCurso(curso);
        nuevaValoracion.setPuntuacion(5);
        nuevaValoracion.setComentario("¡Excelente curso!");

        Valoracion valoracionGuardada = repository.save(nuevaValoracion);

        assertNotNull(valoracionGuardada);
        assertThat(valoracionGuardada.getId(), is(equalTo(1L)));
        assertEquals("¡Excelente curso!", valoracionGuardada.getComentario());
    }

    @Test
    void buscarUltimasOpiniones() throws ParseException {
        // Datos iniciales ya tienen una valoración con fecha '2023-05-02'
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Usuario usuario = usuarioRepository.findById(2L).orElse(null);
        Curso curso = cursoRepository.findById(2L).orElse(null);

        // Original Valoracion from data.sql
        Valoracion valoracionMedia = new Valoracion();
        valoracionMedia.setEstudiante(usuarioRepository.findById(1L).orElse(null));
        valoracionMedia.setCurso(cursoRepository.findById(4L).orElse(null));
        valoracionMedia.setPuntuacion(4);
        valoracionMedia.setComentario("comentario de prueba de 02-05-2023");
        valoracionMedia.setFecha(sdf.parse("2023-05-02 10:00:00"));
        repository.save(valoracionMedia);

        Valoracion valoracionReciente1 = new Valoracion();
        valoracionReciente1.setEstudiante(usuario);
        valoracionReciente1.setCurso(curso);
        valoracionReciente1.setPuntuacion(4);
        valoracionReciente1.setComentario("Más reciente");
        valoracionReciente1.setFecha(sdf.parse("2024-10-20 12:00:00"));
        repository.save(valoracionReciente1);

        Valoracion valoracionReciente2 = new Valoracion();
        valoracionReciente2.setEstudiante(usuario);
        valoracionReciente2.setCurso(curso);
        valoracionReciente2.setPuntuacion(5);
        valoracionReciente2.setComentario("La más reciente de todas");
        valoracionReciente2.setFecha(sdf.parse("2024-10-25 14:00:00"));
        repository.save(valoracionReciente2);

        Valoracion valoracionAntigua = new Valoracion();
        valoracionAntigua.setEstudiante(usuario);
        valoracionAntigua.setCurso(curso);
        valoracionAntigua.setPuntuacion(3);
        valoracionAntigua.setComentario("Antigua");
        valoracionAntigua.setFecha(sdf.parse("2022-01-01 08:00:00"));
        repository.save(valoracionAntigua);

        List<Valoracion> result = repository.findFirst3ByOrderByFechaDescIdDesc();

        assertThat(result.size(), is(equalTo(3)));
        assertEquals("La más reciente de todas", result.get(0).getComentario());
        assertEquals("Más reciente", result.get(1).getComentario());
        assertEquals("comentario de prueba de 02-05-2023", result.get(2).getComentario());
    }
}
