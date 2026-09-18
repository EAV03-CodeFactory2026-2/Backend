package com.udea.Backend.Plataforma.Security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userId;

        // Validar si la petición tiene el header Authorization con el prefijo "Bearer "
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extraer el token
        jwt = authHeader.substring(7);
        
        try {
            userId = jwtService.extractUserId(jwt);

            // Si se extrajo el ID y aún no hay autenticación en el contexto
            if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                
                // Validar fecha de expiración y firma
                if (jwtService.isTokenValid(jwt)) {
                    // Instanciar token de autenticación (el subject será el principal.getName())
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userId, // Principal
                            null,   // Credentials (no necesarias)
                            Collections.emptyList() // Authorities (roles manejados por BD directamente por ahora)
                    );
                    
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    
                    // Inyectar usuario en el contexto de seguridad de Spring
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            // Token inválido, expirado, o mal formado. El filtro simplemente pasará 
            // y Spring Security rechazará la petición por falta de contexto.
        }

        filterChain.doFilter(request, response);
    }
}
