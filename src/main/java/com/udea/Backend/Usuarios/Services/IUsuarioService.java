package com.udea.Backend.Usuarios.Services;

public interface IUsuarioService {
    /**
     * Asigna un rol específico a un usuario.
     * 
     * @param idUsuario ID del usuario al que se le asignará el rol.
     * @param nombreRol Nombre del rol a asignar.
     */
    void asignarRolAUsuario(Integer idUsuario, String nombreRol);

    /**
     * Remueve un rol específico de un usuario.
     * 
     * @param idUsuario ID del usuario al que se le removerá el rol.
     * @param nombreRol Nombre del rol a remover.
     */
    void removerRolDeUsuario(Integer idUsuario, String nombreRol);
}
