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
}
