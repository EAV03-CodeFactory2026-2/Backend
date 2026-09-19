package com.udea.Backend.Plataforma.Security;

public interface IJwtService {
    String extractUserId(String token);
    boolean isTokenValid(String token);
    String generateToken(Integer usuarioId);
}
