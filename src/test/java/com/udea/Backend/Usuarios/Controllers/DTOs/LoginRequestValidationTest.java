package com.udea.Backend.Usuarios.Controllers.DTOs;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Pruebas unitarias de las restricciones de Bean Validation declaradas en
 * {@link LoginRequest} (HU-0043, escenario de campos obligatorios del login).
 */
class LoginRequestValidationTest {

    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    private LoginRequest request;

    @BeforeAll
    static void setUpValidator() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    static void closeValidator() {
        validatorFactory.close();
    }

    @BeforeEach
    void setUp() {
        request = new LoginRequest();
        request.setCorreo("propietario@ejemplo.com");
        request.setContrasena("MiClave123!");
    }

    @Test
    void requestConDatosValidos_noProduceViolaciones() {
        assertThat(validator.validate(request)).isEmpty();
    }

    // ---------- Correo ----------

    @Test
    void correo_enBlanco_esRechazado() {
        request.setCorreo("   ");

        assertThat(validator.validate(request))
                .extracting(ConstraintViolation::getMessage)
                .contains("El correo es obligatorio");
    }

    @Test
    void correo_nulo_esRechazado() {
        request.setCorreo(null);

        assertThat(validator.validate(request))
                .extracting(ConstraintViolation::getMessage)
                .contains("El correo es obligatorio");
    }

    @Test
    void correo_conFormatoInvalido_esRechazado() {
        request.setCorreo("no-es-un-correo");

        assertThat(validator.validate(request))
                .extracting(ConstraintViolation::getMessage)
                .contains("El formato del correo es inválido");
    }

    // ---------- Contraseña ----------

    @Test
    void contrasena_enBlanco_esRechazada() {
        request.setContrasena("");

        assertThat(validator.validate(request))
                .extracting(ConstraintViolation::getMessage)
                .contains("La contraseña es obligatoria");
    }

    @Test
    void contrasena_nula_esRechazada() {
        request.setContrasena(null);

        assertThat(validator.validate(request))
                .extracting(ConstraintViolation::getMessage)
                .contains("La contraseña es obligatoria");
    }
}
