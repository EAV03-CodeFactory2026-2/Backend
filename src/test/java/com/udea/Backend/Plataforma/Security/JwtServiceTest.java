package com.udea.Backend.Plataforma.Security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Pruebas unitarias de {@link JwtService}: generación, extracción del
 * identificador de usuario y validación de expiración/firma de tokens
 * (HU-0043, escenarios de emisión y validación de token).
 */
class JwtServiceTest {

    // Clave Base64 de prueba, exclusiva de este test: NUNCA la clave real de la aplicación.
    private static final String TEST_SECRET = Base64.getEncoder()
            .encodeToString("clave-de-prueba-unicamente-para-tests-jwt-2026".getBytes(StandardCharsets.UTF_8));

    private static final long UN_DIA_MS = 1000 * 60 * 60 * 24;

    private JwtService jwtServiceConExpiracion(long expirationMs) {
        return new JwtService(TEST_SECRET, expirationMs);
    }

    // ---------- Generación y extracción ----------

    @Test
    void generarToken_yExtraerUserId_devuelveElMismoIdDeUsuario() {
        JwtService jwtService = jwtServiceConExpiracion(UN_DIA_MS);

        String token = jwtService.generateToken(42);

        assertThat(jwtService.extractUserId(token)).isEqualTo("42");
    }

    @Test
    void generarToken_produceUnTokenValidoInmediatamente() {
        JwtService jwtService = jwtServiceConExpiracion(UN_DIA_MS);

        String token = jwtService.generateToken(1);

        assertThat(jwtService.isTokenValid(token)).isTrue();
    }

    // ---------- Expiración ----------

    @Test
    void isTokenValid_conTokenYaExpirado_lanzaExpiredJwtException() {
        // Caso límite: tiempo de expiración negativo -> el token nace ya vencido.
        // NOTA: isTokenValid() no devuelve false para un token expirado; JJWT valida
        // la expiración dentro de parseSignedClaims() y lanza ExpiredJwtException.
        // JwtAuthenticationFilter compensa esto atrapando cualquier excepción, pero
        // el método en sí no cumple lo que su nombre promete.
        JwtService jwtService = jwtServiceConExpiracion(-1000);
        String token = jwtService.generateToken(1);

        assertThatThrownBy(() -> jwtService.isTokenValid(token))
                .isInstanceOf(ExpiredJwtException.class);
    }

    // ---------- Integridad / firma ----------

    @Test
    void extractUserId_conTokenManipulado_lanzaJwtException() {
        JwtService jwtService = jwtServiceConExpiracion(UN_DIA_MS);
        String token = jwtService.generateToken(1);
        String tokenAlterado = token.substring(0, token.length() - 2) + "xx";

        assertThatThrownBy(() -> jwtService.extractUserId(tokenAlterado))
                .isInstanceOf(JwtException.class);
    }

    @Test
    void extractUserId_conTokenFirmadoConOtraClave_lanzaJwtException() {
        String otraClave = Base64.getEncoder()
                .encodeToString("otra-clave-completamente-distinta-para-el-test".getBytes(StandardCharsets.UTF_8));
        JwtService jwtServiceEmisor = new JwtService(otraClave, UN_DIA_MS);
        JwtService jwtServiceValidador = jwtServiceConExpiracion(UN_DIA_MS);

        String tokenFirmadoConOtraClave = jwtServiceEmisor.generateToken(1);

        assertThatThrownBy(() -> jwtServiceValidador.extractUserId(tokenFirmadoConOtraClave))
                .isInstanceOf(JwtException.class);
    }

    @Test
    void extractUserId_conCadenaQueNoEsUnToken_lanzaJwtException() {
        JwtService jwtService = jwtServiceConExpiracion(UN_DIA_MS);

        assertThatThrownBy(() -> jwtService.extractUserId("esto-no-es-un-token-jwt"))
                .isInstanceOf(JwtException.class);
    }
}
