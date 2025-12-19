package eu.estilolibre.tfgunir.backend.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import eu.estilolibre.tfgunir.backend.model.Categoria;

import java.util.List;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@SpringBootTest
class CategoriaRespositoryTests {
    
    @Autowired
    CategoriaRepository repository;

    @Test
    void buscarTodasLasCategorias() {
        List<Categoria> result = (List<Categoria>)repository.findAll();
        assertThat(result.size(),is(equalTo(5)));
    }
}
