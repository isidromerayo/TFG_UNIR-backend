package eu.estilolibre.tfgunir.backend.security;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Límite de intentos fallidos de login por origen (IP).
 *
 * <p>Ventana deslizante simple basada en el primer fallo: al alcanzar
 * {@code auth.login.max-attempts} fallos dentro de {@code auth.login.window},
 * el origen queda bloqueado hasta que la ventana expira. Un login correcto
 * limpia el contador.</p>
 *
 * <p>El estado vive en memoria del proceso: es suficiente para una instancia
 * única. Con varias réplicas habría que moverlo a un almacén compartido
 * (Redis) y entonces el límite pasaría a ser global.</p>
 */
@Service
public class LoginRateLimiter {

    private final int maxIntentos;
    private final Duration ventana;
    private final Clock clock;
    private final Map<String, Registro> registros = new ConcurrentHashMap<>();

    public LoginRateLimiter(
            @Value("${auth.login.max-attempts:5}") int maxIntentos,
            @Value("${auth.login.window:PT15M}") Duration ventana,
            Clock clock) {
        this.maxIntentos = maxIntentos;
        this.ventana = ventana;
        this.clock = clock;
    }

    /**
     * @param origen identificador del cliente (IP)
     * @return {@code true} si ha agotado los intentos dentro de la ventana
     */
    public boolean estaBloqueado(String origen) {
        Registro registro = registroVigente(origen);
        return registro != null && registro.fallos() >= maxIntentos;
    }

    /**
     * @param origen identificador del cliente (IP)
     * @return segundos hasta poder reintentar, o {@code 0} si no está bloqueado
     */
    public long segundosRestantes(String origen) {
        Registro registro = registroVigente(origen);
        if (registro == null || registro.fallos() < maxIntentos) {
            return 0L;
        }
        Instant fin = registro.inicio().plus(ventana);
        return Math.max(Duration.between(clock.instant(), fin).getSeconds(), 1L);
    }

    /**
     * Anota un intento fallido. Al expirar la ventana el contador vuelve a empezar.
     */
    public void registrarFallo(String origen) {
        Instant ahora = clock.instant();
        registros.compute(origen, (clave, actual) ->
                actual == null || haExpirado(actual, ahora)
                        ? new Registro(1, ahora)
                        : new Registro(actual.fallos() + 1, actual.inicio()));
    }

    /**
     * Reinicia el contador del origen (login correcto).
     */
    public void limpiar(String origen) {
        registros.remove(origen);
    }

    private Registro registroVigente(String origen) {
        Registro registro = registros.get(origen);
        if (registro == null) {
            return null;
        }
        if (haExpirado(registro, clock.instant())) {
            registros.remove(origen, registro);
            return null;
        }
        return registro;
    }

    private boolean haExpirado(Registro registro, Instant ahora) {
        return !ahora.isBefore(registro.inicio().plus(ventana));
    }

    private record Registro(int fallos, Instant inicio) {
    }
}
