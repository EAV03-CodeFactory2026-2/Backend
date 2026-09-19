package com.udea.Backend.Usuarios.Controllers;

import com.udea.Backend.Usuarios.Controllers.DTOs.AuthResponse;
import com.udea.Backend.Usuarios.Controllers.DTOs.LoginRequest;
import com.udea.Backend.Usuarios.Services.IAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Endpoints para el inicio de sesión y obtención de tokens JWT")
public class AuthController {

    private final IAuthService authService;

    @Operation(
            summary = "Iniciar sesión",
            description = "Autentica un usuario mediante correo y contraseña. Si las credenciales son válidas, retorna un token JWT para consumir los endpoints protegidos."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Autenticación exitosa. Se retorna el token JWT."),
            @ApiResponse(responseCode = "401", description = "Credenciales inválidas o usuario inactivo.")
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            AuthResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
