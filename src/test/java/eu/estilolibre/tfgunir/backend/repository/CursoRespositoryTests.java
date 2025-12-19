package eu.estilolibre.tfgunir.backend.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

import eu.estilolibre.tfgunir.backend.model.Curso;

import java.util.List;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CursoRespositoryTests {
    
    @Autowired
    CursoRepository repository;

    @Test
    void buscarTodosLasCursos() {
        List<Curso> result = (List<Curso>)repository.findAll();
        assertThat(result.size(),is(equalTo(6)));
    }
    @Test
    void buscarCursosDestacadosLimitados3() {
        List<Curso> result = repository.selectMorePointsTop3();
        assertThat(result.size(), is(equalTo(3)));
        assertThat(result.get(0).getTitulo(), is(equalTo("Vue.js")));
    }

    @Test
    void buscarCursosUltimasActualizacionesLimitados3() {
        List<Curso> result = repository.selectLastUpdatesTop3();
        assertThat(result.size(), is(equalTo(3)));
        assertThat(result.get(0).getTitulo(), is(equalTo("Home Studio intermedio")));
    }
    
}
