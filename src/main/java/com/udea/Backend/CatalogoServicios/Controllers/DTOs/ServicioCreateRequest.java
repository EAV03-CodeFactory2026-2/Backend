package com.udea.Backend.CatalogoServicios.Controllers.DTOs;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

/**
 * DTO para la petición de creación de un Servicio.
 */
@Data
public class ServicioCreateRequest {

    @NotNull(message = "El ID del negocio es obligatorio")
    private Integer negocioId;

    @NotBlank(message = "El nombre del servicio es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
    private String nombre;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 500, message = "La descripción no puede superar los 500 caracteres")
    private String descripcion;

    @NotNull(message = "La duración en minutos es obligatoria")
    @Min(value = 1, message = "La duración en minutos debe ser estrictamente mayor a cero")
    private Integer duracionMinutos;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.00", message = "El precio no acepta valores negativos")
    @Digits(integer = 10, fraction = 2, message = "El precio admite hasta 2 decimales")
    private BigDecimal precio;

    @NotNull(message = "La modalidad es obligatoria")
    private Integer modalidadId;
}
