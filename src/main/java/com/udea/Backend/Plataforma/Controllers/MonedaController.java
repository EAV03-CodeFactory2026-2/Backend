package com.udea.Backend.Plataforma.Controllers;

import com.udea.Backend.Plataforma.Entities.Moneda;
import com.udea.Backend.Plataforma.Repositories.MonedaRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/monedas")
@RequiredArgsConstructor
@Tag(name = "Monedas", description = "Catálogo de divisas disponibles en la plataforma (ISO 4217)")
public class MonedaController {

    private final MonedaRepository monedaRepository;

    @Operation(
            summary = "Listar monedas disponibles",
            description = "Retorna el listado completo de monedas registradas en el sistema (código ISO y nombre). Útil para poblar el desplegable de moneda base al crear un negocio."
    )
    @ApiResponse(responseCode = "200", description = "Listado de monedas retornado exitosamente.")
    @GetMapping
    public ResponseEntity<List<Moneda>> obtenerMonedas() {
        return ResponseEntity.ok(monedaRepository.findAll());
    }
}
