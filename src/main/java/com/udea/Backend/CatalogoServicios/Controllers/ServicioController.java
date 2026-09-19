package com.udea.Backend.CatalogoServicios.Controllers;

import com.udea.Backend.CatalogoServicios.Controllers.DTOs.ServicioCreateRequest;
import com.udea.Backend.CatalogoServicios.Entities.Servicio;
import com.udea.Backend.CatalogoServicios.Services.IServicioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/servicios")
@RequiredArgsConstructor
@Tag(name = "Servicios", description = "Gestión del catálogo de servicios de un negocio")
public class ServicioController {

    private final IServicioService servicioService;

    @Operation(
            summary = "Registrar un nuevo servicio",
            description = "Crea un servicio dentro del catálogo de un negocio. El servicio nace con estado 'No Asignado'. Solo el Propietario dueño del negocio puede ejecutar esta acción. La moneda del precio se hereda automáticamente de la moneda base del negocio."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Servicio creado exitosamente con estado 'No Asignado'."),
            @ApiResponse(responseCode = "400", description = "Error de validación o negocio no pertenece al usuario."),
            @ApiResponse(responseCode = "409", description = "Conflicto: Ya existe un servicio con ese nombre en este negocio.")
    })
    @PostMapping
    public ResponseEntity<?> crearServicio(@Valid @RequestBody ServicioCreateRequest request, Principal principal) {
        try {
            Integer usuarioId = Integer.parseInt(principal.getName());
            Servicio servicio = servicioService.crearServicio(request, usuarioId);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of(
                            "mensaje", "Servicio creado exitosamente con estado 'No Asignado'.",
                            "idServicio", servicio.getIdServicio()
                    ));
        } catch (com.udea.Backend.Plataforma.Exceptions.RecursoDuplicadoException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(
            summary = "Listar servicios de un negocio",
            description = "Retorna todos los servicios registrados en el catálogo de un negocio específico."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado de servicios retornado exitosamente.")
    })
    @GetMapping("/negocio/{negocioId}")
    public ResponseEntity<List<Servicio>> listarPorNegocio(
            @Parameter(description = "ID del negocio del cual se desea consultar el catálogo de servicios") @PathVariable Integer negocioId) {
        return ResponseEntity.ok(servicioService.listarServiciosPorNegocio(negocioId));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }
}
