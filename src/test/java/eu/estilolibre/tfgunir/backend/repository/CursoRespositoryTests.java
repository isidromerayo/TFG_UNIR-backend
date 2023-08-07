package eu.estilolibre.tfgunir.backend.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import eu.estilolibre.tfgunir.backend.model.Curso;

import java.util.List;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@SpringBootTest
public class CursoRespositoryTests {
    
    @Autowired
    CursoRepository repository;

    @Test
    void buscarTodosLasCursos() {
        List<Curso> result = (List<Curso>)repository.findAll();
        assertThat(result.size(),is(equalTo(6)));
    }
    @Test
    void buscarCursosDestacadosLimitados3() {
        int result = repository.selectMorePoints().size();
        assertThat(result,is(equalTo(3)));
    }
    @Test
    void buscarCursosUltimasActualizacionesLimitados3() {
        int result = repository.selectLastUpdates().size();
        assertThat(result,is(equalTo(3)));
        List<Curso> registers = repository.selectLastUpdates();
        assertThat(registers.get(0).getTitulo(),is(equalTo("Home Studio intermedio")));
    }
    
}
