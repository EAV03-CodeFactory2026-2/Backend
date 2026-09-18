package com.udea.Backend.Negocios.Repositories;

import com.udea.Backend.Negocios.Entities.Negocio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NegocioRepository extends JpaRepository<Negocio, Integer> {
    
    // Método para validar identificación fiscal duplicada ignorando mayúsculas/minúsculas
    boolean existsByIdentificacionFiscalIgnoreCase(String identificacionFiscal);

    // Lista todos los negocios que pertenecen a un propietario específico
    List<Negocio> findByPropietarioIdUsuario(Integer propietarioId);
}
