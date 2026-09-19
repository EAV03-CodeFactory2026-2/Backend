package com.udea.Backend.CatalogoServicios.Services;

import com.udea.Backend.CatalogoServicios.Controllers.DTOs.ServicioCreateRequest;
import com.udea.Backend.CatalogoServicios.Entities.Servicio;

import java.util.List;

public interface IServicioService {
    /**
     * Crea un nuevo servicio dentro del catálogo de un negocio.
     *
     * @param request              Datos del servicio a crear.
     * @param usuarioIdAutenticado ID del usuario extraído del JWT.
     * @return La entidad Servicio persistida.
     */
    Servicio crearServicio(ServicioCreateRequest request, Integer usuarioIdAutenticado);

    /**
     * Lista todos los servicios del catálogo de un negocio específico.
     */
    List<Servicio> listarServiciosPorNegocio(Integer negocioId);
}
