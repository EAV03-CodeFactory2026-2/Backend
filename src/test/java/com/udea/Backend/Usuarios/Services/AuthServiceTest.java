package com.udea.Backend.Usuarios.Services;

import com.udea.Backend.Plataforma.Security.IJwtService;
import com.udea.Backend.Usuarios.Controllers.DTOs.AuthResponse;
import com.udea.Backend.Usuarios.Controllers.DTOs.LoginRequest;
import com.udea.Backend.Usuarios.Entities.Usuario;
import com.udea.Backend.Usuarios.Repositories.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Pruebas unitarias de {@link AuthService#login}, cubriendo el inicio de
 * sesión exitoso y el rechazo por credenciales inválidas o usuario inactivo
 * (HU-0043).
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private IJwtService jwtService;

    @InjectMocks
    private AuthService authService;

    private static final String CORREO = "propietario@ejemplo.com";
    private static final String CONTRASENA_PLANA = "MiClave123!";
    private static final String HASH_BCRYPT = "$2a$10$abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQ";

    private LoginRequest request;

    @BeforeEach
    void setUp() {
        request = new LoginRequest();
        request.setCorreo(CORREO);
        request.setContrasena(CONTRASENA_PLANA);
    }

    private Usuario.UsuarioBuilder usuarioBase() {
        return Usuario.builder()
                .idUsuario(1)
                .correo(CORREO)
                .estado("Activo");
    }

    // ---------- Escenario: inicio de sesión exitoso ----------

    @Test
    void login_conCredencialesValidasYUsuarioActivo_devuelveTokenYMensajeExitoso() {
        Usuario usuario = usuarioBase().contrasena(HASH_BCRYPT).build();
        when(usuarioRepository.findByCorreo(CORREO)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches(CONTRASENA_PLANA, HASH_BCRYPT)).thenReturn(true);
        when(jwtService.generateToken(1)).thenReturn("token-jwt-generado");

        AuthResponse respuesta = authService.login(request);

        assertThat(respuesta.getToken()).isEqualTo("token-jwt-generado");
        assertThat(respuesta.getMensaje()).isEqualTo("Autenticación exitosa");
    }

    // ---------- Escenario: inicio de sesión fallido ----------

    @Test
    void login_conCorreoNoRegistrado_lanzaIllegalArgumentException() {
        when(usuarioRepository.findByCorreo(CORREO)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Credenciales inválidas");

        verify(jwtService, never()).generateToken(anyInt());
    }

    @Test
    void login_conContrasenaIncorrecta_lanzaIllegalArgumentException() {
        Usuario usuario = usuarioBase().contrasena(HASH_BCRYPT).build();
        when(usuarioRepository.findByCorreo(CORREO)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches(CONTRASENA_PLANA, HASH_BCRYPT)).thenReturn(false);

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Credenciales inválidas");

        verify(jwtService, never()).generateToken(anyInt());
    }

    @Test
    void login_conUsuarioInactivo_lanzaIllegalArgumentException() {
        Usuario usuario = usuarioBase().contrasena(HASH_BCRYPT).estado("Inactivo").build();
        when(usuarioRepository.findByCorreo(CORREO)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches(CONTRASENA_PLANA, HASH_BCRYPT)).thenReturn(true);

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("inactivo");

        verify(jwtService, never()).generateToken(anyInt());
    }

    // ---------- Fallback de contraseña en texto plano ----------
    // NOTA: este fallback es un hallazgo abierto del diagnóstico SAMM (ver Roadmap,
    // Secure Architecture). Estos tests documentan el comportamiento ACTUAL, no lo
    // avalan: cuando se elimine el fallback en AuthService, hay que actualizarlos.

    @Test
    void login_conContrasenaEnTextoPlanoQueCoincideConElFallback_autenticaSinBCrypt() {
        Usuario usuario = usuarioBase().contrasena(CONTRASENA_PLANA).build(); // sin prefijo $2a$
        when(usuarioRepository.findByCorreo(CORREO)).thenReturn(Optional.of(usuario));
        when(jwtService.generateToken(1)).thenReturn("token-jwt-generado");

        AuthResponse respuesta = authService.login(request);

        assertThat(respuesta.getToken()).isEqualTo("token-jwt-generado");
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    void login_conContrasenaEnTextoPlanoQueNoCoincide_lanzaIllegalArgumentException() {
        Usuario usuario = usuarioBase().contrasena("otra-contrasena-distinta").build();
        when(usuarioRepository.findByCorreo(CORREO)).thenReturn(Optional.of(usuario));

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Credenciales inválidas");
    }
}
