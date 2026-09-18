package com.udea.Backend.CatalogoServicios.Repositories;

import com.udea.Backend.CatalogoServicios.Entities.Servicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServicioRepository extends JpaRepository<Servicio, Integer> {

    /**
     * Verifica si ya existe un servicio con el mismo nombre dentro del mismo negocio,
     * ignorando mayúsculas/minúsculas (case-insensitive).
     */
    boolean existsByNegocioIdNegocioAndNombreIgnoreCase(Integer negocioId, String nombre);

    /**
     * Lista todos los servicios de un negocio específico.
     */
    List<Servicio> findByNegocioIdNegocio(Integer negocioId);
}
