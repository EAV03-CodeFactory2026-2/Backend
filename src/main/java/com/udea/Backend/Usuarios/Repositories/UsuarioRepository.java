package com.udea.Backend.Usuarios.Repositories;

import com.udea.Backend.Usuarios.Entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    
    /**
     * Busca un usuario por su correo electrónico.
     * @param correo El correo electrónico a buscar
     * @return Un Optional conteniendo el Usuario si existe
     */
    Optional<Usuario> findByCorreo(String correo);
}
