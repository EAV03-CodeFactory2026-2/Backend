package com.udea.Backend.CatalogoServicios.Repositories;

import com.udea.Backend.CatalogoServicios.Entities.ModalidadServicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ModalidadServicioRepository extends JpaRepository<ModalidadServicio, Integer> {

    /**
     * Busca una modalidad por su nombre exacto.
     */
    Optional<ModalidadServicio> findByNombreModalidad(String nombreModalidad);
}
