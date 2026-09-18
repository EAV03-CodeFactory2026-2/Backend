package com.udea.Backend.CatalogoServicios.Config;

import com.udea.Backend.CatalogoServicios.Entities.ModalidadServicio;
import com.udea.Backend.CatalogoServicios.Entities.TipoModalidad;
import com.udea.Backend.CatalogoServicios.Repositories.ModalidadServicioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

/**
 * Componente que se ejecuta al iniciar la aplicación.
 * Verifica si la tabla de modalidades está vacía y, de ser así, inserta las modalidades por defecto.
 */
@Component
@Order(1)
@RequiredArgsConstructor
@Slf4j
public class ModalidadDataSeeder implements CommandLineRunner {

    private final ModalidadServicioRepository modalidadRepository;

    @Override
    @Transactional
    public void run(String... args) {
        if (modalidadRepository.count() == 0) {
            log.info("La tabla 'modalidad_servicio' está vacía. Procediendo a crear las modalidades por defecto...");

            Arrays.stream(TipoModalidad.values()).forEach(tipo -> {
                ModalidadServicio modalidad = ModalidadServicio.builder()
                        .nombreModalidad(tipo.getNombre())
                        .build();
                modalidadRepository.save(modalidad);
                log.info("Modalidad persistida en base de datos: {}", tipo.getNombre());
            });

            log.info("Inicialización de modalidades completada.");
        } else {
            log.info("La tabla 'modalidad_servicio' ya contiene datos. Se omite la inicialización por defecto.");
        }
    }
}
