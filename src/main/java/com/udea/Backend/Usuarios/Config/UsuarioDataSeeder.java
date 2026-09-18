package com.udea.Backend.Usuarios.Config;

import com.udea.Backend.Usuarios.Entities.Rol;
import com.udea.Backend.Usuarios.Entities.TipoRol;
import com.udea.Backend.Usuarios.Entities.Usuario;
import com.udea.Backend.Usuarios.Repositories.RolRepository;
import com.udea.Backend.Usuarios.Repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Order(2) // Se ejecuta DESPUÉS del RolDataSeeder
@RequiredArgsConstructor
@Slf4j
public class UsuarioDataSeeder implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        if (usuarioRepository.count() == 0) {
            log.info("La tabla 'usuario' está vacía. Procediendo a crear el usuario Propietario por defecto...");

            // Buscar el rol PROPIETARIO en la BD (que debió ser insertado por el RolDataSeeder)
            Rol rolPropietario = rolRepository.findByNombreRol(TipoRol.PROPIETARIO.getNombre())
                    .orElseThrow(() -> new IllegalStateException("Error: El rol PROPIETARIO no existe en la BD."));

            // Crear el usuario con contraseña cifrada usando BCrypt
            Usuario adminUser = Usuario.builder()
                    .nombre("Admin")
                    .apellido("Propietario")
                    .correo("admin@admin.com")
                    .telefono("0000000000") // Obligatorio según el SQL original
                    .contrasena(passwordEncoder.encode("12345")) // BCrypt es el estándar inyectado en SecurityConfig
                    .estado("Activo")
                    .build();

            // Asignar el rol al usuario
            adminUser.getRoles().add(rolPropietario);

            usuarioRepository.save(adminUser);
            
            log.info("Usuario inicial creado exitosamente: admin@admin.com / 12345");
        } else {
            log.info("La tabla 'usuario' ya contiene datos. Se omite la inicialización de usuario por defecto.");
        }
    }
}
