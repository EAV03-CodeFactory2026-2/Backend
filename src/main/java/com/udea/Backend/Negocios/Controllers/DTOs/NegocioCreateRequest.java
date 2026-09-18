package com.udea.Backend.Negocios.Controllers.DTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class NegocioCreateRequest {

    @NotBlank(message = "El nombre del negocio es obligatorio")
    @Size(min = 3, max = 250, message = "El nombre debe tener entre 3 y 250 caracteres")
    private String nombreNegocio;

    @NotBlank(message = "La dirección es obligatoria")
    @Size(max = 250, message = "La dirección no puede superar los 250 caracteres")
    private String direccionNegocio;

    @NotBlank(message = "La identificación fiscal es obligatoria")
    @Size(min = 9, max = 20, message = "La identificación fiscal debe tener entre 9 y 20 caracteres")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "La identificación fiscal debe contener solo caracteres alfanuméricos")
    private String identificacionFiscal;

    @NotBlank(message = "El código de moneda es obligatorio")
    private String monedaCodigoIso;

    private String telefono;
}
