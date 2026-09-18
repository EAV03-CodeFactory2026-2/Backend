package com.udea.Backend.CatalogoServicios.Controllers;

import com.udea.Backend.CatalogoServicios.Entities.ModalidadServicio;
import com.udea.Backend.CatalogoServicios.Repositories.ModalidadServicioRepository;
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
@RequestMapping("/api/v1/modalidades")
@RequiredArgsConstructor
@Tag(name = "Modalidades", description = "Catálogo de modalidades de servicio disponibles (Presencial, Virtual)")
public class ModalidadController {

    private final ModalidadServicioRepository modalidadRepository;

    @Operation(
            summary = "Listar modalidades de servicio",
            description = "Retorna el listado completo de modalidades registradas en el sistema. Útil para poblar el desplegable de modalidad al crear un servicio."
    )
    @ApiResponse(responseCode = "200", description = "Listado de modalidades retornado exitosamente.")
    @GetMapping
    public ResponseEntity<List<ModalidadServicio>> obtenerModalidades() {
        return ResponseEntity.ok(modalidadRepository.findAll());
    }
}
