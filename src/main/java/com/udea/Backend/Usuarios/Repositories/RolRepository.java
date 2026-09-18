package com.udea.Backend.Usuarios.Repositories;

import com.udea.Backend.Usuarios.Entities.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RolRepository extends JpaRepository<Rol, Integer> {
    
    /**
     * Busca un rol por su nombre exacto.
     * @param nombreRol El nombre del rol a buscar
     * @return Un Optional conteniendo el Rol si existe
     */
    Optional<Rol> findByNombreRol(String nombreRol);
}
