package com.udea.Backend.Plataforma.Config;

import com.udea.Backend.Plataforma.Entities.Moneda;
import com.udea.Backend.Plataforma.Entities.TipoMoneda;
import com.udea.Backend.Plataforma.Repositories.MonedaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

/**
 * Componente que se ejecuta al iniciar la aplicación.
 * Verifica si la tabla de monedas está vacía y, de ser así, inserta las monedas comunes.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class MonedaDataSeeder implements CommandLineRunner {

    private final MonedaRepository monedaRepository;

    @Override
    @Transactional
    public void run(String... args) {
        if (monedaRepository.count() == 0) {
            log.info("La tabla 'moneda' está vacía. Procediendo a crear las monedas por defecto...");

            Arrays.stream(TipoMoneda.values()).forEach(tipo -> {
                Moneda moneda = Moneda.builder()
                        .monedaCodigoIso(tipo.name()) // Usa 'COP', 'USD', etc.
                        .nombreMoneda(tipo.getNombre())
                        .build();
                monedaRepository.save(moneda);
                log.info("Moneda persistida en base de datos: {} - {}", tipo.name(), tipo.getNombre());
            });

            log.info("Inicialización de monedas completada.");
        } else {
            log.info("La tabla 'moneda' ya contiene datos. Se omite la inicialización por defecto.");
        }
    }
}
