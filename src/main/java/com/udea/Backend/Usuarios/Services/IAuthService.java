package com.udea.Backend.Usuarios.Services;

import com.udea.Backend.Usuarios.Controllers.DTOs.AuthResponse;
import com.udea.Backend.Usuarios.Controllers.DTOs.LoginRequest;

public interface IAuthService {
    AuthResponse login(LoginRequest request);
}
