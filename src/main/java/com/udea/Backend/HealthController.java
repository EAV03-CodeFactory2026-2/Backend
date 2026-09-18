package com.udea.Backend;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Health", description = "Endpoint de verificación del estado del servidor")
public class HealthController {

    @Operation(summary = "Verificar estado del servidor", description = "Retorna el estado actual del backend. Útil para monitoreo y balanceadores de carga.")
    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("status", "UP");
    }
}
