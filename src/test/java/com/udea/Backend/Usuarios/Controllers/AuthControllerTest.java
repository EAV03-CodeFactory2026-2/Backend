package com.udea.Backend.Usuarios.Controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.udea.Backend.Usuarios.Controllers.DTOs.AuthResponse;
import com.udea.Backend.Usuarios.Controllers.DTOs.LoginRequest;
import com.udea.Backend.Usuarios.Services.IAuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Pruebas unitarias de {@link AuthController}, verificando que el login
 * traduzca las reglas de negocio del service en los códigos HTTP y mensajes
 * esperados por los escenarios de inicio de sesión (HU-0043).
 */
@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private IAuthService authService;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        AuthController controller = new AuthController(authService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setValidator(new LocalValidatorFactoryBean())
                .build();
    }

    private LoginRequest requestValido() {
        LoginRequest request = new LoginRequest();
        request.setCorreo("propietario@ejemplo.com");
        request.setContrasena("MiClave123!");
        return request;
    }

    // ---------- Escenario: inicio de sesión exitoso ----------

    @Test
    void login_conCredencialesValidas_retorna200ConToken() throws Exception {
        AuthResponse respuesta = AuthResponse.builder()
                .token("token-jwt-generado")
                .mensaje("Autenticación exitosa")
                .build();
        when(authService.login(any(LoginRequest.class))).thenReturn(respuesta);

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestValido())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token-jwt-generado"))
                .andExpect(jsonPath("$.mensaje").value("Autenticación exitosa"));
    }

    // ---------- Escenario: inicio de sesión fallido ----------

    @Test
    void login_conCredencialesInvalidas_retorna401ConMensajeDeError() throws Exception {
        when(authService.login(any(LoginRequest.class)))
                .thenThrow(new IllegalArgumentException("Credenciales inválidas."));

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestValido())))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Credenciales inválidas."));
    }

    @Test
    void login_conUsuarioInactivo_retorna401ConMensajeDeError() throws Exception {
        when(authService.login(any(LoginRequest.class)))
                .thenThrow(new IllegalArgumentException("El usuario se encuentra inactivo."));

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestValido())))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("El usuario se encuentra inactivo."));
    }

    // ---------- Validación de campos ----------

    @Test
    void login_conCorreoEnFormatoInvalido_retorna400() throws Exception {
        LoginRequest request = requestValido();
        request.setCorreo("no-es-un-correo");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_conContrasenaEnBlanco_retorna400() throws Exception {
        LoginRequest request = requestValido();
        request.setContrasena("");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
