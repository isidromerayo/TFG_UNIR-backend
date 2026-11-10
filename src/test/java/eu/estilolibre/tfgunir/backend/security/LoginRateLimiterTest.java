package eu.estilolibre.tfgunir.backend.security;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Límite de intentos de login por origen. H-2 del informe de seguridad:
 * sin este control, {@code POST /api/auth} acepta intentos ilimitados
 * (fuerza bruta y credential stuffing).
 */
class LoginRateLimiterTest {

    private static final Duration VENTANA = Duration.ofMinutes(15);
    private static final int MAX_INTENTOS = 3;

    private RelojAjustable reloj;
    private LoginRateLimiter limitador;

    @BeforeEach
    void setUp() {
        reloj = new RelojAjustable();
        limitador = new LoginRateLimiter(MAX_INTENTOS, VENTANA, reloj);
    }

    @Test
    void noBloqueaAntesDeAlcanzarElMaximo() {
        limitador.registrarFallo("10.0.0.1");
        limitador.registrarFallo("10.0.0.1");

        assertThat(limitador.estaBloqueado("10.0.0.1")).isFalse();
    }

    @Test
    void bloqueaAlAlcanzarElMaximoDeFallos() {
        for (int i = 0; i < MAX_INTENTOS; i++) {
            limitador.registrarFallo("10.0.0.1");
        }

        assertThat(limitador.estaBloqueado("10.0.0.1")).isTrue();
    }

    @Test
    void desbloqueaAlExpirarLaVentana() {
        for (int i = 0; i < MAX_INTENTOS; i++) {
            limitador.registrarFallo("10.0.0.1");
        }

        reloj.avanzar(VENTANA.plusSeconds(1));

        assertThat(limitador.estaBloqueado("10.0.0.1")).isFalse();
    }

    @Test
    void elLoginCorrectoReiniciaElContador() {
        limitador.registrarFallo("10.0.0.1");
        limitador.registrarFallo("10.0.0.1");

        limitador.limpiar("10.0.0.1");
        limitador.registrarFallo("10.0.0.1");

        assertThat(limitador.estaBloqueado("10.0.0.1")).isFalse();
    }

    @Test
    void cadaOrigenTieneSuPropioContador() {
        for (int i = 0; i < MAX_INTENTOS; i++) {
            limitador.registrarFallo("10.0.0.1");
        }

        assertThat(limitador.estaBloqueado("10.0.0.1")).isTrue();
        assertThat(limitador.estaBloqueado("10.0.0.2")).isFalse();
    }

    @Test
    void informaDeLosSegundosQueQuedanDeBloqueo() {
        for (int i = 0; i < MAX_INTENTOS; i++) {
            limitador.registrarFallo("10.0.0.1");
        }

        reloj.avanzar(Duration.ofMinutes(5));

        assertThat(limitador.segundosRestantes("10.0.0.1")).isEqualTo(Duration.ofMinutes(10).toSeconds());
    }

    @Test
    void sinIntentosPreviosNoHayEspera() {
        assertThat(limitador.segundosRestantes("10.0.0.3")).isZero();
        assertThat(limitador.estaBloqueado("10.0.0.3")).isFalse();
    }

    private static final class RelojAjustable extends Clock {

        private Instant instante = Instant.parse("2026-11-10T10:00:00Z");

        @Override
        public ZoneId getZone() {
            return ZoneOffset.UTC;
        }

        @Override
        public Clock withZone(ZoneId zone) {
            return this;
        }

        @Override
        public Instant instant() {
            return instante;
        }

        void avanzar(Duration duracion) {
            instante = instante.plus(duracion);
        }
    }
}
