package com.udea.Backend.Usuarios.Config;

import com.udea.Backend.Usuarios.Entities.Rol;
import com.udea.Backend.Usuarios.Entities.TipoRol;
import com.udea.Backend.Usuarios.Repositories.RolRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

/**
 * Componente que se ejecuta al iniciar la aplicación.
 * Verifica si la tabla de roles está vacía y, de ser así, inserta los roles por defecto.
 */
@Component
@Order(1)
@RequiredArgsConstructor
@Slf4j
public class RolDataSeeder implements CommandLineRunner {

    private final RolRepository rolRepository;

    @Override
    @Transactional
    public void run(String... args) {
        if (rolRepository.count() == 0) {
            log.info("La tabla 'rol' está vacía. Procediendo a crear los roles por defecto...");

            Arrays.stream(TipoRol.values()).forEach(tipo -> {
                Rol rol = Rol.builder()
                        .nombreRol(tipo.getNombre())
                        .build();
                rolRepository.save(rol);
                log.info("Rol persistido en base de datos: {}", tipo.getNombre());
            });

            log.info("Inicialización de roles completada.");
        } else {
            log.info("La tabla 'rol' ya contiene datos. Se omite la inicialización por defecto.");
        }
    }
}
