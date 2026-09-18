package com.udea.Backend.Usuarios.Services;

import com.udea.Backend.Usuarios.Entities.Rol;
import com.udea.Backend.Usuarios.Entities.Usuario;
import com.udea.Backend.Usuarios.Repositories.RolRepository;
import com.udea.Backend.Usuarios.Repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;

    /**
     * Asigna un rol específico a un usuario.
     * 
     * @param idUsuario ID del usuario al que se le asignará el rol.
     * @param nombreRol Nombre del rol a asignar.
     */
    @Transactional
    public void asignarRolAUsuario(Integer idUsuario, String nombreRol) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + idUsuario));

        Rol rol = rolRepository.findByNombreRol(nombreRol)
                .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado con nombre: " + nombreRol));

        usuario.getRoles().add(rol);
        // Al guardar, JPA se encarga de insertar en la tabla intermedia 'asignacion_rol'
        usuarioRepository.save(usuario);
    }

    /**
     * Remueve un rol específico de un usuario.
     * 
     * @param idUsuario ID del usuario al que se le removerá el rol.
     * @param nombreRol Nombre del rol a remover.
     */
    @Transactional
    public void removerRolDeUsuario(Integer idUsuario, String nombreRol) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + idUsuario));

        Rol rol = rolRepository.findByNombreRol(nombreRol)
                .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado con nombre: " + nombreRol));

        usuario.getRoles().remove(rol);
        // Al guardar, JPA se encarga de eliminar el registro de la tabla intermedia 'asignacion_rol'
        usuarioRepository.save(usuario);
    }
}
