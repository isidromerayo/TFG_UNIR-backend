package eu.estilolibre.tfgunir.backend.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class ApiErrorTest {

    @Test
    void of_createsErrorWithStatusCodeAndMessage() {
        ApiError error = ApiError.of(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "no autorizado");

        assertThat(error.status()).isEqualTo(401);
        assertThat(error.code()).isEqualTo("UNAUTHORIZED");
        assertThat(error.message()).isEqualTo("no autorizado");
        assertThat(error.errors()).isNull();
    }

    @Test
    void ofValidation_includesErrorsMap() {
        Map<String, String> errors = Map.of("email", "El email no puede estar vacío");

        ApiError error = ApiError.ofValidation(HttpStatus.BAD_REQUEST, errors);

        assertThat(error.status()).isEqualTo(400);
        assertThat(error.code()).isEqualTo("VALIDATION_ERROR");
        assertThat(error.message()).isEqualTo("Validation failed");
        assertThat(error.errors()).isEqualTo(errors);
    }
}
