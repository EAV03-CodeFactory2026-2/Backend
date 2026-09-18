package com.udea.Backend.Plataforma.Controllers;

import com.udea.Backend.Plataforma.Entities.Moneda;
import com.udea.Backend.Plataforma.Repositories.MonedaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/monedas")
@RequiredArgsConstructor
public class MonedaController {

    private final MonedaRepository monedaRepository;

    @GetMapping
    public ResponseEntity<List<Moneda>> obtenerMonedas() {
        // Al ser un catálogo simple, podemos devolver directamente las entidades
        // Esto poblará el desplegable del Frontend
        return ResponseEntity.ok(monedaRepository.findAll());
    }
}
