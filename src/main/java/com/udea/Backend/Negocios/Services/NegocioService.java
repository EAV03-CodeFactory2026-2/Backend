package com.udea.Backend.Negocios.Services;

import com.udea.Backend.Negocios.Controllers.DTOs.NegocioCreateRequest;
import com.udea.Backend.Negocios.Entities.Negocio;
import com.udea.Backend.Negocios.Repositories.NegocioRepository;
import com.udea.Backend.Plataforma.Entities.Moneda;
import com.udea.Backend.Plataforma.Repositories.MonedaRepository;
import com.udea.Backend.Usuarios.Entities.TipoRol;
import com.udea.Backend.Usuarios.Entities.Usuario;
import com.udea.Backend.Usuarios.Repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NegocioService {

    private final NegocioRepository negocioRepository;
    private final UsuarioRepository usuarioRepository;
    private final MonedaRepository monedaRepository;

    @Transactional
    public Negocio crearNegocio(NegocioCreateRequest request, Integer usuarioIdAutenticado) {
        // Escenario 3: Validación de identificación fiscal duplicada (insensible a mayúsculas/minúsculas)
        if (negocioRepository.existsByIdentificacionFiscalIgnoreCase(request.getIdentificacionFiscal())) {
            throw new com.udea.Backend.Plataforma.Exceptions.RecursoDuplicadoException("La identificación fiscal ya está en uso en la plataforma.");
        }

        // Buscar propietario usando el ID extraído del token JWT
        Usuario propietario = usuarioRepository.findById(usuarioIdAutenticado)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado."));

        // Validar que el usuario tenga efectivamente el rol de PROPIETARIO
        boolean esPropietario = propietario.getRoles().stream()
                .anyMatch(rol -> rol.getNombreRol().equals(TipoRol.PROPIETARIO.getNombre()));

        if (!esPropietario) {
            throw new IllegalArgumentException("El usuario especificado no tiene el rol de Propietario.");
        }

        // Buscar moneda
        Moneda moneda = monedaRepository.findById(request.getMonedaCodigoIso())
                .orElseThrow(() -> new IllegalArgumentException("Moneda no válida."));

        // Escenario 1 y 2: Creación de negocio nuevo (o multi-tenant), asignado a la misma cuenta base
        Negocio negocio = Negocio.builder()
                .propietario(propietario)
                .nombreNegocio(request.getNombreNegocio())
                .direccionNegocio(request.getDireccionNegocio())
                .identificacionFiscal(request.getIdentificacionFiscal())
                .telefono(request.getTelefono())
                .moneda(moneda)
                .estado("Activo") // Se activa de inmediato sin aprobación
                .build();

        return negocioRepository.save(negocio);
    }

    /**
     * Lista todos los negocios que pertenecen al usuario autenticado.
     * Valida que el usuario exista y tenga el rol de Propietario.
     *
     * @param usuarioIdAutenticado ID del usuario extraído del JWT.
     * @return Lista de negocios del propietario.
     */
    @Transactional(readOnly = true)
    public List<Negocio> listarMisNegocios(Integer usuarioIdAutenticado) {
        Usuario propietario = usuarioRepository.findById(usuarioIdAutenticado)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado."));

        boolean esPropietario = propietario.getRoles().stream()
                .anyMatch(rol -> rol.getNombreRol().equals(TipoRol.PROPIETARIO.getNombre()));

        if (!esPropietario) {
            throw new IllegalArgumentException("El usuario no tiene el rol de Propietario.");
        }

        return negocioRepository.findByPropietarioIdUsuario(usuarioIdAutenticado);
    }
}
