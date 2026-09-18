package com.udea.Backend.CatalogoServicios.Services;

import com.udea.Backend.CatalogoServicios.Controllers.DTOs.ServicioCreateRequest;
import com.udea.Backend.CatalogoServicios.Entities.ModalidadServicio;
import com.udea.Backend.CatalogoServicios.Entities.Servicio;
import com.udea.Backend.CatalogoServicios.Repositories.ModalidadServicioRepository;
import com.udea.Backend.CatalogoServicios.Repositories.ServicioRepository;
import com.udea.Backend.Negocios.Entities.Negocio;
import com.udea.Backend.Negocios.Repositories.NegocioRepository;
import com.udea.Backend.Usuarios.Entities.TipoRol;
import com.udea.Backend.Usuarios.Entities.Usuario;
import com.udea.Backend.Usuarios.Repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Servicio que orquesta la lógica de negocio para la gestión del catálogo de servicios.
 */
@Service
@RequiredArgsConstructor
public class ServicioService {

    private final ServicioRepository servicioRepository;
    private final NegocioRepository negocioRepository;
    private final ModalidadServicioRepository modalidadRepository;
    private final UsuarioRepository usuarioRepository;

    /**
     * Crea un nuevo servicio dentro del catálogo de un negocio.
     *
     * @param request              Datos del servicio a crear.
     * @param usuarioIdAutenticado ID del usuario extraído del JWT.
     * @return La entidad Servicio persistida.
     */
    @Transactional
    public Servicio crearServicio(ServicioCreateRequest request, Integer usuarioIdAutenticado) {

        // Verificar que el usuario autenticado exista y tenga rol de PROPIETARIO
        Usuario propietario = usuarioRepository.findById(usuarioIdAutenticado)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado."));

        boolean esPropietario = propietario.getRoles().stream()
                .anyMatch(rol -> rol.getNombreRol().equals(TipoRol.PROPIETARIO.getNombre()));

        if (!esPropietario) {
            throw new IllegalArgumentException("El usuario no tiene el rol de Propietario.");
        }

        // Verificar que el negocio exista y pertenezca al propietario autenticado
        Negocio negocio = negocioRepository.findById(request.getNegocioId())
                .orElseThrow(() -> new IllegalArgumentException("Negocio no encontrado."));

        if (!negocio.getPropietario().getIdUsuario().equals(usuarioIdAutenticado)) {
            throw new IllegalArgumentException("No tienes permisos para agregar servicios a este negocio.");
        }

        // Escenario 2: Validación de nombre duplicado (case-insensitive) dentro del mismo negocio
        if (servicioRepository.existsByNegocioIdNegocioAndNombreIgnoreCase(request.getNegocioId(), request.getNombre())) {
            throw new IllegalArgumentException("Ya existe un servicio con ese nombre en este negocio.");
        }

        // Buscar modalidad
        ModalidadServicio modalidad = modalidadRepository.findById(request.getModalidadId())
                .orElseThrow(() -> new IllegalArgumentException("Modalidad no válida."));

        // Escenario 1: Crear el servicio con estado "No Asignado" por defecto
        Servicio servicio = Servicio.builder()
                .negocio(negocio)
                .modalidad(modalidad)
                .nombre(request.getNombre())
                .descripcion(request.getDescripcion())
                .duracionMinutos(request.getDuracionMinutos())
                .precio(request.getPrecio())
                .estado("No Asignado")
                .build();

        return servicioRepository.save(servicio);
    }

    /**
     * Lista todos los servicios del catálogo de un negocio específico.
     */
    @Transactional(readOnly = true)
    public List<Servicio> listarServiciosPorNegocio(Integer negocioId) {
        return servicioRepository.findByNegocioIdNegocio(negocioId);
    }
}
