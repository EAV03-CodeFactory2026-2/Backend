package com.udea.Backend.CatalogoServicios.Controllers.DTOs;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Pruebas unitarias de las restricciones de Bean Validation declaradas en
 * {@link ServicioCreateRequest}, cubriendo el caso feliz y los valores límite
 * de duración, precio y longitud de texto de los escenarios de registro de servicio.
 */
class ServicioCreateRequestValidationTest {

    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    private ServicioCreateRequest request;

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
        request = new ServicioCreateRequest();
        request.setNegocioId(1);
        request.setNombre("Corte de cabello");
        request.setDescripcion("Corte clásico con máquina");
        request.setDuracionMinutos(30);
        request.setPrecio(new BigDecimal("15.50"));
        request.setModalidadId(1);
    }

    @Test
    void requestConDatosValidos_noProduceViolaciones() {
        assertThat(validator.validate(request)).isEmpty();
    }

    // ---------- Escenario 3: Duración inválida ----------

    @Test
    void duracionMinutos_enCero_esRechazada() {
        request.setDuracionMinutos(0);

        Set<ConstraintViolation<ServicioCreateRequest>> violaciones = validator.validate(request);

        assertThat(violaciones)
                .extracting(ConstraintViolation::getMessage)
                .contains("La duración en minutos debe ser estrictamente mayor a cero");
    }

    @Test
    void duracionMinutos_negativa_esRechazada() {
        request.setDuracionMinutos(-10);

        assertThat(validator.validate(request))
                .extracting(ConstraintViolation::getMessage)
                .contains("La duración en minutos debe ser estrictamente mayor a cero");
    }

    @Test
    void duracionMinutos_nula_esRechazada() {
        request.setDuracionMinutos(null);

        assertThat(validator.validate(request))
                .extracting(ConstraintViolation::getMessage)
                .contains("La duración en minutos es obligatoria");
    }

    @Test
    void duracionMinutos_enElLimiteInferiorValido_esAceptada() {
        // Caso límite: 1 es el primer valor estrictamente mayor a cero
        request.setDuracionMinutos(1);

        assertThat(validator.validate(request)).isEmpty();
    }

    // ---------- Escenario 4: Precio inválido ----------

    @Test
    void precio_negativo_esRechazado() {
        request.setPrecio(new BigDecimal("-0.01"));

        assertThat(validator.validate(request))
                .extracting(ConstraintViolation::getMessage)
                .contains("El precio no acepta valores negativos");
    }

    @Test
    void precio_nulo_esRechazado() {
        request.setPrecio(null);

        assertThat(validator.validate(request))
                .extracting(ConstraintViolation::getMessage)
                .contains("El precio es obligatorio");
    }

    @Test
    void precio_enCero_esAceptado() {
        // Caso límite: cero no es negativo, por lo que debe ser válido
        request.setPrecio(new BigDecimal("0.00"));

        assertThat(validator.validate(request)).isEmpty();
    }

    @Test
    void precio_conMasDeDosDecimales_esRechazado() {
        request.setPrecio(new BigDecimal("10.999"));

        assertThat(validator.validate(request))
                .extracting(ConstraintViolation::getMessage)
                .contains("El precio admite hasta 2 decimales");
    }

    @Test
    void precio_conExactamenteDosDecimales_esAceptado() {
        request.setPrecio(new BigDecimal("10.99"));

        assertThat(validator.validate(request)).isEmpty();
    }

    // ---------- Escenario 5: Campos obligatorios ----------

    @Test
    void nombre_enBlanco_esRechazado() {
        request.setNombre("   ");

        assertThat(validator.validate(request))
                .extracting(ConstraintViolation::getMessage)
                .contains("El nombre del servicio es obligatorio");
    }

    @Test
    void nombre_nulo_esRechazado() {
        request.setNombre(null);

        assertThat(validator.validate(request))
                .extracting(ConstraintViolation::getMessage)
                .contains("El nombre del servicio es obligatorio");
    }

    @Test
    void descripcion_enBlanco_esRechazada() {
        request.setDescripcion("");

        assertThat(validator.validate(request))
                .extracting(ConstraintViolation::getMessage)
                .contains("La descripción es obligatoria");
    }

    @Test
    void modalidadId_nula_esRechazada() {
        request.setModalidadId(null);

        assertThat(validator.validate(request))
                .extracting(ConstraintViolation::getMessage)
                .contains("La modalidad es obligatoria");
    }

    @Test
    void negocioId_nulo_esRechazado() {
        request.setNegocioId(null);

        assertThat(validator.validate(request))
                .extracting(ConstraintViolation::getMessage)
                .contains("El ID del negocio es obligatorio");
    }

    // ---------- Escenario 6: Longitud de campos de texto ----------

    @Test
    void nombre_enElLimiteMaximoPermitido_esAceptado() {
        // Caso límite: exactamente 100 caracteres
        request.setNombre("A".repeat(100));

        assertThat(validator.validate(request)).isEmpty();
    }

    @Test
    void nombre_queExcedeElLimiteMaximo_esRechazado() {
        // Caso límite: 101 caracteres, uno más que el máximo permitido
        request.setNombre("A".repeat(101));

        assertThat(validator.validate(request))
                .extracting(ConstraintViolation::getMessage)
                .contains("El nombre no puede superar los 100 caracteres");
    }

    @Test
    void descripcion_enElLimiteMaximoPermitido_esAceptada() {
        // Caso límite: exactamente 500 caracteres
        request.setDescripcion("A".repeat(500));

        assertThat(validator.validate(request)).isEmpty();
    }

    @Test
    void descripcion_queExcedeElLimiteMaximo_esRechazada() {
        // Caso límite: 501 caracteres, uno más que el máximo permitido
        request.setDescripcion("A".repeat(501));

        assertThat(validator.validate(request))
                .extracting(ConstraintViolation::getMessage)
                .contains("La descripción no puede superar los 500 caracteres");
    }
}
