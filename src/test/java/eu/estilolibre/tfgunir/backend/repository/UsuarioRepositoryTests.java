package eu.estilolibre.tfgunir.backend.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import eu.estilolibre.tfgunir.backend.model.Usuario;

import java.util.List;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class UsuarioRepositoryTests {
 
    @Autowired
    UsuarioRepository repository;

    @Test
    void buscarTodosLasUsuarios() {
        List<Usuario> result = (List<Usuario>)repository.findAll();
        assertThat(result.size(),is(equalTo(8)));
    }

}
