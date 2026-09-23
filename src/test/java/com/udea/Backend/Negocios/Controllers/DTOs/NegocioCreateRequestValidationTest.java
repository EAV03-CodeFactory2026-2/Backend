package com.udea.Backend.Negocios.Controllers.DTOs;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Pruebas unitarias de las restricciones de Bean Validation declaradas en
 * {@link NegocioCreateRequest}, cubriendo el caso feliz y los valores límite
 * de longitud y formato de los escenarios de creación de negocio (HU-0042).
 */
class NegocioCreateRequestValidationTest {

    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    private NegocioCreateRequest request;

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
        request = new NegocioCreateRequest();
        request.setNombreNegocio("Salón Bella Vista");
        request.setDireccionNegocio("Calle 10 # 20-30");
        request.setIdentificacionFiscal("900123456");
        request.setMonedaCodigoIso("COP");
        request.setTelefono("3001234567");
    }

    @Test
    void requestConDatosValidos_noProduceViolaciones() {
        assertThat(validator.validate(request)).isEmpty();
    }

    // ---------- Nombre del negocio ----------

    @Test
    void nombreNegocio_enBlanco_esRechazado() {
        request.setNombreNegocio("   ");

        assertThat(validator.validate(request))
                .extracting(ConstraintViolation::getMessage)
                .contains("El nombre del negocio es obligatorio");
    }

    @Test
    void nombreNegocio_nulo_esRechazado() {
        request.setNombreNegocio(null);

        assertThat(validator.validate(request))
                .extracting(ConstraintViolation::getMessage)
                .contains("El nombre del negocio es obligatorio");
    }

    @Test
    void nombreNegocio_conMenosDelMinimoPermitido_esRechazado() {
        // Caso límite: 2 caracteres, uno menos que el mínimo permitido (3).
        request.setNombreNegocio("Ab");

        assertThat(validator.validate(request))
                .extracting(ConstraintViolation::getMessage)
                .contains("El nombre debe tener entre 3 y 250 caracteres");
    }

    @Test
    void nombreNegocio_enElLimiteMinimoValido_esAceptado() {
        // Caso límite: exactamente 3 caracteres.
        request.setNombreNegocio("Abc");

        assertThat(validator.validate(request)).isEmpty();
    }

    @Test
    void nombreNegocio_enElLimiteMaximoValido_esAceptado() {
        // Caso límite: exactamente 250 caracteres.
        request.setNombreNegocio("A".repeat(250));

        assertThat(validator.validate(request)).isEmpty();
    }

    @Test
    void nombreNegocio_queExcedeElLimiteMaximo_esRechazado() {
        // Caso límite: 251 caracteres, uno más que el máximo permitido.
        request.setNombreNegocio("A".repeat(251));

        assertThat(validator.validate(request))
                .extracting(ConstraintViolation::getMessage)
                .contains("El nombre debe tener entre 3 y 250 caracteres");
    }

    // ---------- Dirección del negocio ----------

    @Test
    void direccionNegocio_enBlanco_esRechazada() {
        request.setDireccionNegocio("");

        assertThat(validator.validate(request))
                .extracting(ConstraintViolation::getMessage)
                .contains("La dirección es obligatoria");
    }

    @Test
    void direccionNegocio_enElLimiteMaximoValido_esAceptada() {
        // Caso límite: exactamente 250 caracteres.
        request.setDireccionNegocio("A".repeat(250));

        assertThat(validator.validate(request)).isEmpty();
    }

    @Test
    void direccionNegocio_queExcedeElLimiteMaximo_esRechazada() {
        // Caso límite: 251 caracteres, uno más que el máximo permitido.
        request.setDireccionNegocio("A".repeat(251));

        assertThat(validator.validate(request))
                .extracting(ConstraintViolation::getMessage)
                .contains("La dirección no puede superar los 250 caracteres");
    }

    // ---------- Identificación fiscal ----------

    @Test
    void identificacionFiscal_enBlanco_esRechazada() {
        request.setIdentificacionFiscal("");

        assertThat(validator.validate(request))
                .extracting(ConstraintViolation::getMessage)
                .contains("La identificación fiscal es obligatoria");
    }

    @Test
    void identificacionFiscal_conMenosDelMinimoPermitido_esRechazada() {
        // Caso límite: 8 caracteres, uno menos que el mínimo permitido (9).
        request.setIdentificacionFiscal("12345678");

        assertThat(validator.validate(request))
                .extracting(ConstraintViolation::getMessage)
                .contains("La identificación fiscal debe tener entre 9 y 20 caracteres");
    }

    @Test
    void identificacionFiscal_enElLimiteMinimoValido_esAceptada() {
        // Caso límite: exactamente 9 caracteres alfanuméricos.
        request.setIdentificacionFiscal("123456789");

        assertThat(validator.validate(request)).isEmpty();
    }

    @Test
    void identificacionFiscal_enElLimiteMaximoValido_esAceptada() {
        // Caso límite: exactamente 20 caracteres alfanuméricos.
        request.setIdentificacionFiscal("A".repeat(20));

        assertThat(validator.validate(request)).isEmpty();
    }

    @Test
    void identificacionFiscal_queExcedeElLimiteMaximo_esRechazada() {
        // Caso límite: 21 caracteres, uno más que el máximo permitido.
        request.setIdentificacionFiscal("A".repeat(21));

        assertThat(validator.validate(request))
                .extracting(ConstraintViolation::getMessage)
                .contains("La identificación fiscal debe tener entre 9 y 20 caracteres");
    }

    @Test
    void identificacionFiscal_conCaracteresNoAlfanumericos_esRechazada() {
        request.setIdentificacionFiscal("900-123-45");

        assertThat(validator.validate(request))
                .extracting(ConstraintViolation::getMessage)
                .contains("La identificación fiscal debe contener solo caracteres alfanuméricos");
    }

    @Test
    void identificacionFiscal_soloAlfanumerica_esAceptada() {
        // Caso límite: mezcla de letras y números, sin símbolos, dentro del rango de longitud.
        request.setIdentificacionFiscal("ABC123XYZ9");

        assertThat(validator.validate(request)).isEmpty();
    }

    // ---------- Moneda base ----------

    @Test
    void monedaCodigoIso_enBlanco_esRechazado() {
        request.setMonedaCodigoIso("");

        assertThat(validator.validate(request))
                .extracting(ConstraintViolation::getMessage)
                .contains("El código de moneda es obligatorio");
    }

    @Test
    void monedaCodigoIso_nulo_esRechazado() {
        request.setMonedaCodigoIso(null);

        assertThat(validator.validate(request))
                .extracting(ConstraintViolation::getMessage)
                .contains("El código de moneda es obligatorio");
    }

    // ---------- Teléfono (opcional, sin restricciones) ----------

    @Test
    void telefono_nulo_noProduceViolaciones() {
        // El teléfono no es obligatorio en el registro del negocio.
        request.setTelefono(null);

        assertThat(validator.validate(request)).isEmpty();
    }
}
