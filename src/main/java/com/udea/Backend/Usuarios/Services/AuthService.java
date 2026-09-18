package com.udea.Backend.Usuarios.Services;

import com.udea.Backend.Plataforma.Security.JwtService;
import com.udea.Backend.Usuarios.Controllers.DTOs.AuthResponse;
import com.udea.Backend.Usuarios.Controllers.DTOs.LoginRequest;
import com.udea.Backend.Usuarios.Entities.Usuario;
import com.udea.Backend.Usuarios.Repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthResponse login(LoginRequest request) {
        Usuario usuario = usuarioRepository.findByCorreo(request.getCorreo())
                .orElseThrow(() -> new IllegalArgumentException("Credenciales inválidas."));

        // Comparamos contraseñas (soportando tanto BCrypt como fallback en texto plano para testing inicial)
        boolean match;
        if (usuario.getContrasena().startsWith("$2a$")) {
            match = passwordEncoder.matches(request.getContrasena(), usuario.getContrasena());
        } else {
            // Fallback inseguro solo para facilitar tus pruebas si insertaste datos a mano en SQL
            match = request.getContrasena().equals(usuario.getContrasena());
        }

        if (!match) {
            throw new IllegalArgumentException("Credenciales inválidas.");
        }

        if (!"Activo".equalsIgnoreCase(usuario.getEstado())) {
            throw new IllegalArgumentException("El usuario se encuentra inactivo.");
        }

        String jwtToken = jwtService.generateToken(usuario.getIdUsuario());

        return AuthResponse.builder()
                .token(jwtToken)
                .mensaje("Autenticación exitosa")
                .build();
    }
}
