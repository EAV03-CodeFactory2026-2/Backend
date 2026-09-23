package com.udea.Backend.Negocios.Services;

import com.udea.Backend.Negocios.Controllers.DTOs.NegocioCreateRequest;
import com.udea.Backend.Negocios.Entities.Negocio;
import com.udea.Backend.Negocios.Repositories.NegocioRepository;
import com.udea.Backend.Plataforma.Entities.Moneda;
import com.udea.Backend.Plataforma.Exceptions.RecursoDuplicadoException;
import com.udea.Backend.Plataforma.Repositories.MonedaRepository;
import com.udea.Backend.Usuarios.Entities.Rol;
import com.udea.Backend.Usuarios.Entities.TipoRol;
import com.udea.Backend.Usuarios.Entities.Usuario;
import com.udea.Backend.Usuarios.Repositories.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Pruebas unitarias de {@link NegocioService#crearNegocio} y
 * {@link NegocioService#listarMisNegocios}, cubriendo el registro exitoso
 * (incluido el caso multi-tenant), la identificación fiscal duplicada y las
 * reglas de autorización del rol Propietario.
 */
@ExtendWith(MockitoExtension.class)
class NegocioServiceTest {

    @Mock
    private NegocioRepository negocioRepository;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private MonedaRepository monedaRepository;

    @InjectMocks
    private NegocioService negocioService;

    private static final Integer USUARIO_ID = 1;
    private static final String MONEDA_CODIGO = "COP";

    private Usuario propietario;
    private Moneda moneda;
    private NegocioCreateRequest request;

    @BeforeEach
    void setUp() {
        propietario = Usuario.builder()
                .idUsuario(USUARIO_ID)
                .roles(Set.of(Rol.builder().idRol(1).nombreRol(TipoRol.PROPIETARIO.getNombre()).build()))
                .build();

        moneda = Moneda.builder()
                .monedaCodigoIso(MONEDA_CODIGO)
                .nombreMoneda("Peso Colombiano")
                .build();

        request = new NegocioCreateRequest();
        request.setNombreNegocio("Salón Bella Vista");
        request.setDireccionNegocio("Calle 10 # 20-30");
        request.setIdentificacionFiscal("900123456");
        request.setMonedaCodigoIso(MONEDA_CODIGO);
        request.setTelefono("3001234567");
    }

    // ---------- Escenario 1: Creación exitosa del primer negocio ----------

    @Test
    void crearNegocio_conDatosValidos_guardaNegocioConEstadoActivo() {
        when(negocioRepository.existsByIdentificacionFiscalIgnoreCase(request.getIdentificacionFiscal()))
                .thenReturn(false);
        when(usuarioRepository.findById(USUARIO_ID)).thenReturn(Optional.of(propietario));
        when(monedaRepository.findById(MONEDA_CODIGO)).thenReturn(Optional.of(moneda));
        when(negocioRepository.save(any(Negocio.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Negocio resultado = negocioService.crearNegocio(request, USUARIO_ID);

        assertThat(resultado.getEstado()).isEqualTo("Activo");
        assertThat(resultado.getPropietario()).isEqualTo(propietario);
        assertThat(resultado.getMoneda()).isEqualTo(moneda);
        assertThat(resultado.getNombreNegocio()).isEqualTo("Salón Bella Vista");
        assertThat(resultado.getDireccionNegocio()).isEqualTo("Calle 10 # 20-30");
        assertThat(resultado.getIdentificacionFiscal()).isEqualTo("900123456");

        ArgumentCaptor<Negocio> captor = ArgumentCaptor.forClass(Negocio.class);
        verify(negocioRepository).save(captor.capture());
        assertThat(captor.getValue().getEstado())
                .as("el negocio debe activarse de inmediato, sin aprobación")
                .isEqualTo("Activo");
    }

    // ---------- Escenario 2: Creación de un negocio adicional (multi-tenant) ----------

    @Test
    void crearNegocio_propietarioConNegocioExistente_creaUnSegundoNegocioAisladoParaLaMismaCuenta() {
        // Caso multi-tenant: no hay restricción que impida a un propietario
        // registrar más de un negocio con la misma cuenta.
        request.setIdentificacionFiscal("800987654");
        when(negocioRepository.existsByIdentificacionFiscalIgnoreCase("800987654")).thenReturn(false);
        when(usuarioRepository.findById(USUARIO_ID)).thenReturn(Optional.of(propietario));
        when(monedaRepository.findById(MONEDA_CODIGO)).thenReturn(Optional.of(moneda));
        when(negocioRepository.save(any(Negocio.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Negocio negocioAdicional = negocioService.crearNegocio(request, USUARIO_ID);

        assertThat(negocioAdicional.getPropietario()).isEqualTo(propietario);
        assertThat(negocioAdicional.getEstado()).isEqualTo("Activo");
        verify(negocioRepository).save(any(Negocio.class));
    }

    // ---------- Escenario 3: Identificación fiscal duplicada ----------

    @Test
    void crearNegocio_identificacionFiscalYaRegistrada_lanzaRecursoDuplicadoException() {
        when(negocioRepository.existsByIdentificacionFiscalIgnoreCase(request.getIdentificacionFiscal()))
                .thenReturn(true);

        assertThatThrownBy(() -> negocioService.crearNegocio(request, USUARIO_ID))
                .isInstanceOf(RecursoDuplicadoException.class)
                .hasMessageContaining("identificación fiscal ya está en uso");

        // La validación de unicidad ocurre antes de tocar usuario o moneda.
        verify(usuarioRepository, never()).findById(any());
        verify(negocioRepository, never()).save(any());
    }

    @Test
    void crearNegocio_delegaLaComparacionCaseInsensitiveAlRepositorio() {
        request.setIdentificacionFiscal("Ab123Xy99");
        when(negocioRepository.existsByIdentificacionFiscalIgnoreCase("Ab123Xy99")).thenReturn(true);

        assertThatThrownBy(() -> negocioService.crearNegocio(request, USUARIO_ID))
                .isInstanceOf(RecursoDuplicadoException.class);

        verify(negocioRepository).existsByIdentificacionFiscalIgnoreCase("Ab123Xy99");
    }

    // ---------- Autorización ----------

    @Test
    void crearNegocio_usuarioNoEncontrado_lanzaIllegalArgumentException() {
        when(negocioRepository.existsByIdentificacionFiscalIgnoreCase(request.getIdentificacionFiscal()))
                .thenReturn(false);
        when(usuarioRepository.findById(USUARIO_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> negocioService.crearNegocio(request, USUARIO_ID))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Usuario no encontrado");

        verify(negocioRepository, never()).save(any());
    }

    @Test
    void crearNegocio_usuarioSinRolPropietario_lanzaIllegalArgumentException() {
        Usuario cliente = Usuario.builder()
                .idUsuario(USUARIO_ID)
                .roles(Set.of(Rol.builder().idRol(2).nombreRol(TipoRol.CLIENTE.getNombre()).build()))
                .build();
        when(negocioRepository.existsByIdentificacionFiscalIgnoreCase(request.getIdentificacionFiscal()))
                .thenReturn(false);
        when(usuarioRepository.findById(USUARIO_ID)).thenReturn(Optional.of(cliente));

        assertThatThrownBy(() -> negocioService.crearNegocio(request, USUARIO_ID))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Propietario");

        verify(monedaRepository, never()).findById(any());
        verify(negocioRepository, never()).save(any());
    }

    @Test
    void crearNegocio_usuarioSinRoles_lanzaIllegalArgumentException() {
        // Caso límite: usuario existente pero sin ningún rol asignado.
        Usuario sinRoles = Usuario.builder().idUsuario(USUARIO_ID).roles(Set.of()).build();
        when(negocioRepository.existsByIdentificacionFiscalIgnoreCase(request.getIdentificacionFiscal()))
                .thenReturn(false);
        when(usuarioRepository.findById(USUARIO_ID)).thenReturn(Optional.of(sinRoles));

        assertThatThrownBy(() -> negocioService.crearNegocio(request, USUARIO_ID))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Propietario");
    }

    // ---------- Moneda ----------

    @Test
    void crearNegocio_monedaNoValida_lanzaIllegalArgumentException() {
        when(negocioRepository.existsByIdentificacionFiscalIgnoreCase(request.getIdentificacionFiscal()))
                .thenReturn(false);
        when(usuarioRepository.findById(USUARIO_ID)).thenReturn(Optional.of(propietario));
        when(monedaRepository.findById(MONEDA_CODIGO)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> negocioService.crearNegocio(request, USUARIO_ID))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Moneda no válida");

        verify(negocioRepository, never()).save(any());
    }

    // ---------- listarMisNegocios ----------

    @Test
    void listarMisNegocios_propietarioConNegociosExistentes_retornaListaDelRepositorio() {
        Negocio negocio = Negocio.builder().idNegocio(10).propietario(propietario)
                .nombreNegocio("Salón Bella Vista").estado("Activo").build();
        when(usuarioRepository.findById(USUARIO_ID)).thenReturn(Optional.of(propietario));
        when(negocioRepository.findByPropietarioIdUsuario(USUARIO_ID)).thenReturn(List.of(negocio));

        List<Negocio> resultado = negocioService.listarMisNegocios(USUARIO_ID);

        assertThat(resultado).containsExactly(negocio);
    }

    @Test
    void listarMisNegocios_propietarioSinNegociosRegistrados_retornaListaVacia() {
        // Caso límite: primera visita del propietario, lista de negocios vacía.
        when(usuarioRepository.findById(USUARIO_ID)).thenReturn(Optional.of(propietario));
        when(negocioRepository.findByPropietarioIdUsuario(USUARIO_ID)).thenReturn(List.of());

        List<Negocio> resultado = negocioService.listarMisNegocios(USUARIO_ID);

        assertThat(resultado).isEmpty();
    }

    @Test
    void listarMisNegocios_usuarioNoEncontrado_lanzaIllegalArgumentException() {
        when(usuarioRepository.findById(USUARIO_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> negocioService.listarMisNegocios(USUARIO_ID))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Usuario no encontrado");

        verify(negocioRepository, never()).findByPropietarioIdUsuario(any());
    }

    @Test
    void listarMisNegocios_usuarioSinRolPropietario_lanzaIllegalArgumentException() {
        Usuario cliente = Usuario.builder()
                .idUsuario(USUARIO_ID)
                .roles(Set.of(Rol.builder().idRol(2).nombreRol(TipoRol.CLIENTE.getNombre()).build()))
                .build();
        when(usuarioRepository.findById(USUARIO_ID)).thenReturn(Optional.of(cliente));

        assertThatThrownBy(() -> negocioService.listarMisNegocios(USUARIO_ID))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Propietario");

        verify(negocioRepository, never()).findByPropietarioIdUsuario(any());
    }
}
