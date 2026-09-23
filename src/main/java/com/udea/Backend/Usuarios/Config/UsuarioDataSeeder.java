package com.udea.Backend.Usuarios.Config;

import com.udea.Backend.Usuarios.Entities.Rol;
import com.udea.Backend.Usuarios.Entities.TipoRol;
import com.udea.Backend.Usuarios.Entities.Usuario;
import com.udea.Backend.Usuarios.Repositories.RolRepository;
import com.udea.Backend.Usuarios.Repositories.UsuarioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Order(2) // Se ejecuta DESPUÉS del RolDataSeeder
@Slf4j
public class UsuarioDataSeeder implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    // Credenciales del administrador inicial (variables de entorno ADMIN_EMAIL y ADMIN_PASSWORD).
    private final String adminEmail;
    private final String adminPassword;

    public UsuarioDataSeeder(UsuarioRepository usuarioRepository,
                             RolRepository rolRepository,
                             PasswordEncoder passwordEncoder,
                             @Value("${app.admin.email}") String adminEmail,
                             @Value("${app.admin.password}") String adminPassword) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminEmail = adminEmail;
        this.adminPassword = adminPassword;
    }

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
                    .correo(adminEmail)
                    .telefono("0000000000") // Obligatorio según el SQL original
                    .contrasena(passwordEncoder.encode(adminPassword)) // BCrypt es el estándar inyectado en SecurityConfig
                    .estado("Activo")
                    .build();

            // Asignar el rol al usuario
            adminUser.getRoles().add(rolPropietario);

            usuarioRepository.save(adminUser);
            
            log.info("Usuario inicial creado exitosamente: {}", adminEmail);
        } else {
            log.info("La tabla 'usuario' ya contiene datos. Se omite la inicialización de usuario por defecto.");
        }
    }
}
