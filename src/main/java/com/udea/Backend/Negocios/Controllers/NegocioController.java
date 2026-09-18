package com.udea.Backend.Negocios.Controllers;

import com.udea.Backend.Negocios.Controllers.DTOs.NegocioCreateRequest;
import com.udea.Backend.Negocios.Entities.Negocio;
import com.udea.Backend.Negocios.Services.NegocioService;
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
@RequestMapping("/api/v1/negocios")
@RequiredArgsConstructor
public class NegocioController {

    private final NegocioService negocioService;

    @PostMapping
    public ResponseEntity<?> crearNegocio(@Valid @RequestBody NegocioCreateRequest request, Principal principal) {
        try {
            // El Filtro JWT ya se encarga de inyectar el ID del usuario en el getName() del principal
            Integer usuarioId = Integer.parseInt(principal.getName());

            Negocio negocio = negocioService.crearNegocio(request, usuarioId);
            // Mensaje de confirmación de registro exitoso
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("mensaje", "Negocio creado y activado exitosamente.", "idNegocio", negocio.getIdNegocio()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Escenarios 4 y 5: Manejo de errores de validación de campos vacíos o longitudes inválidas
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
