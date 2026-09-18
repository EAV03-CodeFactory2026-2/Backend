package com.udea.Backend.Negocios.Controllers;

import com.udea.Backend.Negocios.Controllers.DTOs.NegocioCreateRequest;
import com.udea.Backend.Negocios.Entities.Negocio;
import com.udea.Backend.Negocios.Services.NegocioService;
import io.swagger.v3.oas.annotations.Operation;
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
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Negocios", description = "Gestión de negocios del propietario (registro y administración)")
public class NegocioController {

    private final NegocioService negocioService;

    @Operation(
            summary = "Registrar un nuevo negocio",
            description = "Crea un negocio vinculado al propietario autenticado (extraído del JWT). El negocio se activa de inmediato sin necesidad de aprobación. Requiere rol de Propietario."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Negocio creado y activado exitosamente."),
            @ApiResponse(responseCode = "400", description = "Error de validación o usuario no autorizado."),
            @ApiResponse(responseCode = "409", description = "Conflicto: La identificación fiscal ya está en uso.")
    })
    @PostMapping("negocios")
    public ResponseEntity<?> crearNegocio(@Valid @RequestBody NegocioCreateRequest request, Principal principal) {
        try {
            // El Filtro JWT ya se encarga de inyectar el ID del usuario en el getName() del principal
            Integer usuarioId = Integer.parseInt(principal.getName());

            Negocio negocio = negocioService.crearNegocio(request, usuarioId);
            // Mensaje de confirmación de registro exitoso
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("mensaje", "Negocio creado y activado exitosamente.", "idNegocio", negocio.getIdNegocio()));
        } catch (com.udea.Backend.Plataforma.Exceptions.RecursoDuplicadoException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(
            summary = "Listar mis negocios",
            description = "Retorna todos los negocios que pertenecen al usuario autenticado (Propietario). El ID del propietario se extrae automáticamente del token JWT."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado de negocios retornado exitosamente."),
            @ApiResponse(responseCode = "400", description = "El usuario no tiene el rol de Propietario.")
    })
    @GetMapping("negocios")
    public ResponseEntity<?> listarMisNegocios(Principal principal) {
        try {
            Integer usuarioId = Integer.parseInt(principal.getName());
            return ResponseEntity.ok(negocioService.listarMisNegocios(usuarioId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
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
