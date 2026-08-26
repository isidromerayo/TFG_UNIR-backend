package eu.estilolibre.tfgunir.backend.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import eu.estilolibre.tfgunir.backend.dto.CursoResponse;
import eu.estilolibre.tfgunir.backend.dto.UsuarioRegistroRequest;
import eu.estilolibre.tfgunir.backend.dto.UsuarioResponse;
import eu.estilolibre.tfgunir.backend.service.UsuarioService;

@ExtendWith(MockitoExtension.class)
class UsuarioControllerTest {

    @Mock
    private UsuarioService usuarioService;

    private UsuarioController controller;

    @BeforeEach
    void setUp() {
        controller = new UsuarioController(usuarioService);
    }

    @Test
    void registrar_devuelve201_conUsuarioResponse() {
        UsuarioRegistroRequest request = new UsuarioRegistroRequest();
        request.setNombre("Ana");
        request.setApellidos("Pérez");
        request.setEmail("ana@example.com");
        request.setPassword("secreto");
        UsuarioResponse expected = new UsuarioResponse(42L, "Ana", "Pérez", "ana@example.com", "P");
        when(usuarioService.registrar(request)).thenReturn(expected);

        var response = controller.registrar(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(expected);
        verify(usuarioService).registrar(request);
    }

    @Test
    void obtenerCursosComprados_devuelve200_conListaDeCursos() {
        CursoResponse curso = new CursoResponse(4L, "Angular", "Descripción", null, 4.6, null, null, null);
        when(usuarioService.obtenerCursosComprados(1L)).thenReturn(List.of(curso));

        var response = controller.obtenerCursosComprados(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0).id()).isEqualTo(4L);
        verify(usuarioService).obtenerCursosComprados(1L);
    }

    @Test
    void agregarMisCursos_devuelve201_yDelegaIdsParseados() {
        String body = "http://localhost:8080/api/cursos/4,http://localhost:8080/api/cursos/5";

        var response = controller.agregarMisCursos(1L, body);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        verify(usuarioService).agregarCursosComprados(1L, List.of(4L, 5L));
    }

    @Test
    void parseCursoIds_extraeIdsDeUrisSeparadasPorComa() {
        String body = "http://localhost:8080/api/cursos/4,http://localhost:8080/api/cursos/5";

        assertThat(UsuarioController.parseCursoIds(body)).containsExactly(4L, 5L);
    }

    @Test
    void parseCursoIds_extraeIdsDeUrisSeparadasPorSaltoDeLinea() {
        String body = "http://localhost:8080/api/cursos/4\nhttp://localhost:8080/api/cursos/5";

        assertThat(UsuarioController.parseCursoIds(body)).containsExactly(4L, 5L);
    }

    @Test
    void parseCursoIds_devuelveListaVacia_cuandoBodyVacio() {
        assertThat(UsuarioController.parseCursoIds("")).isEmpty();
        assertThat(UsuarioController.parseCursoIds(null)).isEmpty();
    }

    @Test
    void parseCursoIds_ignoraTokensSinId() {
        String body = "http://localhost:8080/api/cursos/4,texto-sin-id,http://localhost:8080/api/cursos/7";

        assertThat(UsuarioController.parseCursoIds(body)).containsExactly(4L, 7L);
    }
}
