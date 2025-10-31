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

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ValoracionRepositoryTests {

    @Autowired
    ValoracionRepository repository;

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    CursoRepository cursoRepository;

    @Test
    void buscarTodasLasValoraciones() {
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
        assertNotNull(valoracionGuardada.getId());
        assertEquals("¡Excelente curso!", valoracionGuardada.getComentario());
    }

    @Test
    void buscarUltimasOpiniones() throws ParseException {
        // Datos iniciales ya tienen una valoración con fecha '2023-05-02'

        Usuario usuario = usuarioRepository.findById(2L).orElse(null);
        Curso curso = cursoRepository.findById(2L).orElse(null);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Valoracion valoracionReciente1 = new Valoracion();
        valoracionReciente1.setEstudiante(usuario);
        valoracionReciente1.setCurso(curso);
        valoracionReciente1.setPuntuacion(4);
        valoracionReciente1.setComentario("Más reciente");
        valoracionReciente1.setFecha(sdf.parse("2024-10-20"));
        repository.save(valoracionReciente1);

        Valoracion valoracionReciente2 = new Valoracion();
        valoracionReciente2.setEstudiante(usuario);
        valoracionReciente2.setCurso(curso);
        valoracionReciente2.setPuntuacion(5);
        valoracionReciente2.setComentario("La más reciente de todas");
        valoracionReciente2.setFecha(sdf.parse("2024-10-25"));
        repository.save(valoracionReciente2);

        Valoracion valoracionAntigua = new Valoracion();
        valoracionAntigua.setEstudiante(usuario);
        valoracionAntigua.setCurso(curso);
        valoracionAntigua.setPuntuacion(3);
        valoracionAntigua.setComentario("Antigua");
        valoracionAntigua.setFecha(sdf.parse("2022-01-01"));
        repository.save(valoracionAntigua);

        List<Valoracion> result = repository.selectLastOpinions();

        assertThat(result.size(), is(equalTo(3)));
        assertEquals("La más reciente de todas", result.get(0).getComentario());
        assertEquals("Más reciente", result.get(1).getComentario());
        assertEquals("comentario de prueba de 02-05-2023", result.get(2).getComentario());
    }
}
