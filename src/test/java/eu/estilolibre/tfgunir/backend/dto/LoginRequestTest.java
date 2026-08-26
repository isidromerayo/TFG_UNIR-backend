package eu.estilolibre.tfgunir.backend.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

class LoginRequestTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void validUser_noViolations() {
        LoginRequest user = new LoginRequest();
        user.setEmail("test@example.com");
        user.setPassword("password123");

        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(user);

        assertThat(violations).isEmpty();
    }

    @Test
    void blankEmail_hasViolation() {
        LoginRequest user = new LoginRequest();
        user.setEmail("");
        user.setPassword("password123");

        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(user);

        assertThat(violations.size()).isGreaterThan(0);
    }

    @Test
    void invalidEmailFormat_hasViolation() {
        LoginRequest user = new LoginRequest();
        user.setEmail("not-an-email");
        user.setPassword("password123");

        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(user);

        assertThat(violations).hasSize(1);
    }

    @Test
    void blankPassword_hasViolation() {
        LoginRequest user = new LoginRequest();
        user.setEmail("test@example.com");
        user.setPassword("");

        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(user);

        assertThat(violations.size()).isGreaterThan(0);
    }

    @Test
    void shortPassword_hasViolation() {
        LoginRequest user = new LoginRequest();
        user.setEmail("test@example.com");
        user.setPassword("abc");

        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(user);

        assertThat(violations).hasSize(1);
    }
}
