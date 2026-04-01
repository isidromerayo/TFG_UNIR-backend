package eu.estilolibre.tfgunir.backend.controller;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ConstraintViolation;

import java.util.Set;

class FormUserTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void validUser_noViolations() {
        FormUser user = new FormUser();
        user.setEmail("test@example.com");
        user.setPassword("password123");

        Set<ConstraintViolation<FormUser>> violations = validator.validate(user);

        assertThat(violations).isEmpty();
    }

    @Test
    void blankEmail_hasViolation() {
        FormUser user = new FormUser();
        user.setEmail("");
        user.setPassword("password123");

        Set<ConstraintViolation<FormUser>> violations = validator.validate(user);

        assertThat(violations.size()).isGreaterThan(0);
    }

    @Test
    void invalidEmailFormat_hasViolation() {
        FormUser user = new FormUser();
        user.setEmail("not-an-email");
        user.setPassword("password123");

        Set<ConstraintViolation<FormUser>> violations = validator.validate(user);

        assertThat(violations).hasSize(1);
    }

    @Test
    void blankPassword_hasViolation() {
        FormUser user = new FormUser();
        user.setEmail("test@example.com");
        user.setPassword("");

        Set<ConstraintViolation<FormUser>> violations = validator.validate(user);

        assertThat(violations.size()).isGreaterThan(0);
    }

    @Test
    void shortPassword_hasViolation() {
        FormUser user = new FormUser();
        user.setEmail("test@example.com");
        user.setPassword("abc");

        Set<ConstraintViolation<FormUser>> violations = validator.validate(user);

        assertThat(violations).hasSize(1);
    }
}