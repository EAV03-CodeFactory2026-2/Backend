package com.udea.Backend.Plataforma.Security;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Pruebas unitarias de {@link JwtAuthenticationFilter}: protección de rutas
 * mediante token JWT (HU-0043, escenarios de acceso a rutas protegidas/públicas
 * con y sin token válido).
 */
@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private IJwtService jwtService;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private FilterChain filterChain;

    private JwtAuthenticationFilter filter;

    @BeforeEach
    void setUp() {
        filter = new JwtAuthenticationFilter(jwtService);
    }

    @AfterEach
    void limpiarContextoDeSeguridad() {
        // El SecurityContextHolder es estático: hay que limpiarlo entre tests
        // para que uno no filtre autenticación hacia el siguiente.
        SecurityContextHolder.clearContext();
    }

    // ---------- Escenario: acceso a rutas públicas (sin token) ----------

    @Test
    void doFilterInternal_sinHeaderAuthorization_continuaElFiltroSinAutenticar() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);

        filter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_conHeaderQueNoEmpiezaConBearer_continuaSinAutenticar() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Basic abc123");

        filter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain).doFilter(request, response);
        verify(jwtService, never()).extractUserId(anyString());
    }

    // ---------- Escenario: acceso con token válido ----------

    @Test
    void doFilterInternal_conTokenValido_estableceLaAutenticacionEnElContexto() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer token-valido");
        when(jwtService.extractUserId("token-valido")).thenReturn("7");
        when(jwtService.isTokenValid("token-valido")).thenReturn(true);

        filter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).isEqualTo("7");
        verify(filterChain).doFilter(request, response);
    }

    // ---------- Escenario: bloqueo sin token válido ----------

    @Test
    void doFilterInternal_conTokenExpiradoOInvalido_noEstableceAutenticacionYContinuaElFiltro() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer token-expirado");
        when(jwtService.extractUserId("token-expirado")).thenReturn("7");
        when(jwtService.isTokenValid("token-expirado")).thenReturn(false);

        filter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_conTokenMalformadoQueLanzaExcepcionAlExtraer_noPropagaLaExcepcionYContinuaElFiltro()
            throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer token-falso-o-alterado");
        when(jwtService.extractUserId("token-falso-o-alterado")).thenThrow(new JwtException("token inválido"));

        // No debe lanzar la excepción hacia arriba: el filtro la atrapa y sigue la cadena.
        filter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain).doFilter(request, response);
    }
}
