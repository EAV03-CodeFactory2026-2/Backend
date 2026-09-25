package com.udea.Backend.Usuarios.Services;

import com.udea.Backend.Usuarios.Entities.Rol;
import com.udea.Backend.Usuarios.Entities.TipoRol;
import com.udea.Backend.Usuarios.Entities.Usuario;
import com.udea.Backend.Usuarios.Repositories.RolRepository;
import com.udea.Backend.Usuarios.Repositories.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Pruebas unitarias de {@link UsuarioService#asignarRolAUsuario} y
 * {@link UsuarioService#removerRolDeUsuario}, cubriendo la asignación y
 * remoción exitosa de roles y el rechazo cuando el usuario o el rol no existen.
 */
@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private RolRepository rolRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    private static final Integer USUARIO_ID = 1;
    private static final String NOMBRE_ROL = TipoRol.PROPIETARIO.getNombre();
    private static final String ROL_INEXISTENTE = "RolInexistente";

    private Rol rolPropietario;
    private Rol rolCliente;

    @BeforeEach
    void setUp() {
        rolPropietario = Rol.builder().idRol(1).nombreRol(NOMBRE_ROL).build();
        rolCliente = Rol.builder().idRol(2).nombreRol(TipoRol.CLIENTE.getNombre()).build();
    }

    private Usuario usuarioConRoles(Rol... roles) {
        // El servicio modifica el set de roles, por eso no puede ser inmutable
        return Usuario.builder()
                .idUsuario(USUARIO_ID)
                .roles(new HashSet<>(Set.of(roles)))
                .build();
    }

    // ---------- asignarRolAUsuario ----------

    @Test
    void asignarRolAUsuario_conUsuarioYRolExistentes_agregaElRolYGuardaElUsuario() {
        // Arrange
        Usuario usuario = usuarioConRoles();
        when(usuarioRepository.findById(USUARIO_ID)).thenReturn(Optional.of(usuario));
        when(rolRepository.findByNombreRol(NOMBRE_ROL)).thenReturn(Optional.of(rolPropietario));

        // Act
        usuarioService.asignarRolAUsuario(USUARIO_ID, NOMBRE_ROL);

        // Assert
        assertThat(usuario.getRoles()).containsExactly(rolPropietario);
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void asignarRolAUsuario_usuarioNoEncontrado_lanzaIllegalArgumentException() {
        // Arrange
        when(usuarioRepository.findById(USUARIO_ID)).thenReturn(Optional.empty());

        // Act
        Throwable error = catchThrowable(() -> usuarioService.asignarRolAUsuario(USUARIO_ID, NOMBRE_ROL));

        // Assert
        assertThat(error)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Usuario no encontrado");
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void asignarRolAUsuario_rolNoEncontrado_lanzaIllegalArgumentException() {
        // Arrange
        Usuario usuario = usuarioConRoles();
        when(usuarioRepository.findById(USUARIO_ID)).thenReturn(Optional.of(usuario));
        when(rolRepository.findByNombreRol(ROL_INEXISTENTE)).thenReturn(Optional.empty());

        // Act
        Throwable error = catchThrowable(() -> usuarioService.asignarRolAUsuario(USUARIO_ID, ROL_INEXISTENTE));

        // Assert
        assertThat(error)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Rol no encontrado");
        verify(usuarioRepository, never()).save(any());
    }

    // ---------- removerRolDeUsuario ----------

    @Test
    void removerRolDeUsuario_conUsuarioYRolExistentes_quitaSoloEseRolYGuardaElUsuario() {
        // Arrange
        Usuario usuario = usuarioConRoles(rolPropietario, rolCliente);
        when(usuarioRepository.findById(USUARIO_ID)).thenReturn(Optional.of(usuario));
        when(rolRepository.findByNombreRol(NOMBRE_ROL)).thenReturn(Optional.of(rolPropietario));

        // Act
        usuarioService.removerRolDeUsuario(USUARIO_ID, NOMBRE_ROL);

        // Assert
        assertThat(usuario.getRoles()).containsExactly(rolCliente);
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void removerRolDeUsuario_usuarioNoEncontrado_lanzaIllegalArgumentException() {
        // Arrange
        when(usuarioRepository.findById(USUARIO_ID)).thenReturn(Optional.empty());

        // Act
        Throwable error = catchThrowable(() -> usuarioService.removerRolDeUsuario(USUARIO_ID, NOMBRE_ROL));

        // Assert
        assertThat(error)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Usuario no encontrado");
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void removerRolDeUsuario_rolNoEncontrado_lanzaIllegalArgumentException() {
        // Arrange
        Usuario usuario = usuarioConRoles(rolPropietario);
        when(usuarioRepository.findById(USUARIO_ID)).thenReturn(Optional.of(usuario));
        when(rolRepository.findByNombreRol(ROL_INEXISTENTE)).thenReturn(Optional.empty());

        // Act
        Throwable error = catchThrowable(() -> usuarioService.removerRolDeUsuario(USUARIO_ID, ROL_INEXISTENTE));

        // Assert
        assertThat(error)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Rol no encontrado");
        verify(usuarioRepository, never()).save(any());
    }
}
