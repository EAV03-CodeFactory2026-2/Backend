package com.udea.Backend.Plataforma.Repositories;

import com.udea.Backend.Plataforma.Entities.Moneda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MonedaRepository extends JpaRepository<Moneda, String> {
}
