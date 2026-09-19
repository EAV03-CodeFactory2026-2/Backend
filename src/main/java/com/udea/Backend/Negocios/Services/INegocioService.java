package com.udea.Backend.Negocios.Services;

import com.udea.Backend.Negocios.Controllers.DTOs.NegocioCreateRequest;
import com.udea.Backend.Negocios.Entities.Negocio;

import java.util.List;

public interface INegocioService {
    Negocio crearNegocio(NegocioCreateRequest request, Integer usuarioIdAutenticado);

    /**
     * Lista todos los negocios que pertenecen al usuario autenticado.
     * Valida que el usuario exista y tenga el rol de Propietario.
     *
     * @param usuarioIdAutenticado ID del usuario extraído del JWT.
     * @return Lista de negocios del propietario.
     */
    List<Negocio> listarMisNegocios(Integer usuarioIdAutenticado);
}
