package com.udea.Backend.CatalogoServicios.Services;

import com.udea.Backend.CatalogoServicios.Controllers.DTOs.ServicioCreateRequest;
import com.udea.Backend.CatalogoServicios.Entities.ModalidadServicio;
import com.udea.Backend.CatalogoServicios.Entities.Servicio;
import com.udea.Backend.CatalogoServicios.Repositories.ModalidadServicioRepository;
import com.udea.Backend.CatalogoServicios.Repositories.ServicioRepository;
import com.udea.Backend.Negocios.Entities.Negocio;
import com.udea.Backend.Negocios.Repositories.NegocioRepository;
import com.udea.Backend.Plataforma.Exceptions.RecursoDuplicadoException;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Pruebas unitarias de {@link ServicioService#crearServicio} y
 * {@link ServicioService#listarServiciosPorNegocio}, cubriendo el registro
 * exitoso de un servicio y las reglas de negocio de los escenarios de
 * autorización, unicidad y modalidad.
 */
@ExtendWith(MockitoExtension.class)
class ServicioServiceTest {

    @Mock
    private ServicioRepository servicioRepository;
    @Mock
    private NegocioRepository negocioRepository;
    @Mock
    private ModalidadServicioRepository modalidadRepository;
    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private ServicioService servicioService;

    private static final Integer USUARIO_ID = 1;
    private static final Integer NEGOCIO_ID = 10;
    private static final Integer MODALIDAD_ID = 100;

    private Usuario propietario;
    private Negocio negocio;
    private ModalidadServicio modalidad;
    private ServicioCreateRequest request;

    @BeforeEach
    void setUp() {
        propietario = Usuario.builder()
                .idUsuario(USUARIO_ID)
                .roles(Set.of(Rol.builder().idRol(1).nombreRol(TipoRol.PROPIETARIO.getNombre()).build()))
                .build();

        negocio = Negocio.builder()
                .idNegocio(NEGOCIO_ID)
                .propietario(propietario)
                .build();

        modalidad = ModalidadServicio.builder()
                .idModalidad(MODALIDAD_ID)
                .nombreModalidad("Presencial")
                .build();

        request = new ServicioCreateRequest();
        request.setNegocioId(NEGOCIO_ID);
        request.setNombre("Corte de cabello");
        request.setDescripcion("Corte clásico");
        request.setDuracionMinutos(30);
        request.setPrecio(new BigDecimal("15.50"));
        request.setModalidadId(MODALIDAD_ID);
    }

    // ---------- Escenario 1: Registro exitoso ----------

    @Test
    void crearServicio_conDatosValidos_guardaServicioConEstadoNoAsignado() {
        when(usuarioRepository.findById(USUARIO_ID)).thenReturn(Optional.of(propietario));
        when(negocioRepository.findById(NEGOCIO_ID)).thenReturn(Optional.of(negocio));
        when(servicioRepository.existsByNegocioIdNegocioAndNombreIgnoreCase(NEGOCIO_ID, request.getNombre()))
                .thenReturn(false);
        when(modalidadRepository.findById(MODALIDAD_ID)).thenReturn(Optional.of(modalidad));
        when(servicioRepository.save(any(Servicio.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Servicio resultado = servicioService.crearServicio(request, USUARIO_ID);

        assertThat(resultado.getEstado()).isEqualTo("No Asignado");
        assertThat(resultado.getNegocio()).isEqualTo(negocio);
        assertThat(resultado.getModalidad()).isEqualTo(modalidad);
        assertThat(resultado.getNombre()).isEqualTo("Corte de cabello");
        assertThat(resultado.getDescripcion()).isEqualTo("Corte clásico");
        assertThat(resultado.getDuracionMinutos()).isEqualTo(30);
        assertThat(resultado.getPrecio()).isEqualByComparingTo("15.50");

        ArgumentCaptor<Servicio> captor = ArgumentCaptor.forClass(Servicio.class);
        verify(servicioRepository).save(captor.capture());
        assertThat(captor.getValue().getEstado()).isEqualTo("No Asignado");
    }

    @Test
    void crearServicio_conValoresLimiteValidos_persisteLosValoresSinAlterarlos() {
        // Caso límite: duración mínima permitida (1) y precio en el borde inferior permitido (0.00)
        request.setDuracionMinutos(1);
        request.setPrecio(new BigDecimal("0.00"));

        when(usuarioRepository.findById(USUARIO_ID)).thenReturn(Optional.of(propietario));
        when(negocioRepository.findById(NEGOCIO_ID)).thenReturn(Optional.of(negocio));
        when(servicioRepository.existsByNegocioIdNegocioAndNombreIgnoreCase(NEGOCIO_ID, request.getNombre()))
                .thenReturn(false);
        when(modalidadRepository.findById(MODALIDAD_ID)).thenReturn(Optional.of(modalidad));
        when(servicioRepository.save(any(Servicio.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Servicio resultado = servicioService.crearServicio(request, USUARIO_ID);

        assertThat(resultado.getDuracionMinutos()).isEqualTo(1);
        assertThat(resultado.getPrecio()).isEqualByComparingTo("0.00");
    }

    // ---------- Autorización ----------

    @Test
    void crearServicio_usuarioNoEncontrado_lanzaIllegalArgumentException() {
        when(usuarioRepository.findById(USUARIO_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> servicioService.crearServicio(request, USUARIO_ID))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Usuario no encontrado");

        verify(servicioRepository, never()).save(any());
    }

    @Test
    void crearServicio_usuarioSinRolPropietario_lanzaIllegalArgumentException() {
        Usuario cliente = Usuario.builder()
                .idUsuario(USUARIO_ID)
                .roles(Set.of(Rol.builder().idRol(2).nombreRol(TipoRol.CLIENTE.getNombre()).build()))
                .build();
        when(usuarioRepository.findById(USUARIO_ID)).thenReturn(Optional.of(cliente));

        assertThatThrownBy(() -> servicioService.crearServicio(request, USUARIO_ID))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Propietario");

        verify(negocioRepository, never()).findById(any());
        verify(servicioRepository, never()).save(any());
    }

    @Test
    void crearServicio_usuarioSinRoles_lanzaIllegalArgumentException() {
        // Caso límite: usuario existente pero sin ningún rol asignado
        Usuario sinRoles = Usuario.builder().idUsuario(USUARIO_ID).roles(Set.of()).build();
        when(usuarioRepository.findById(USUARIO_ID)).thenReturn(Optional.of(sinRoles));

        assertThatThrownBy(() -> servicioService.crearServicio(request, USUARIO_ID))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Propietario");
    }

    @Test
    void crearServicio_negocioNoEncontrado_lanzaIllegalArgumentException() {
        when(usuarioRepository.findById(USUARIO_ID)).thenReturn(Optional.of(propietario));
        when(negocioRepository.findById(NEGOCIO_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> servicioService.crearServicio(request, USUARIO_ID))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Negocio no encontrado");

        verify(servicioRepository, never()).save(any());
    }

    @Test
    void crearServicio_negocioNoPerteneceAlUsuarioAutenticado_lanzaIllegalArgumentException() {
        Usuario otroPropietario = Usuario.builder()
                .idUsuario(999)
                .roles(Set.of(Rol.builder().idRol(1).nombreRol(TipoRol.PROPIETARIO.getNombre()).build()))
                .build();
        Negocio negocioAjeno = Negocio.builder().idNegocio(NEGOCIO_ID).propietario(otroPropietario).build();

        when(usuarioRepository.findById(USUARIO_ID)).thenReturn(Optional.of(propietario));
        when(negocioRepository.findById(NEGOCIO_ID)).thenReturn(Optional.of(negocioAjeno));

        assertThatThrownBy(() -> servicioService.crearServicio(request, USUARIO_ID))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("permisos");

        verify(servicioRepository, never()).save(any());
    }

    // ---------- Escenario 2: Nombre duplicado (case-insensitive) ----------

    @Test
    void crearServicio_nombreDuplicadoEnElMismoNegocio_lanzaRecursoDuplicadoException() {
        when(usuarioRepository.findById(USUARIO_ID)).thenReturn(Optional.of(propietario));
        when(negocioRepository.findById(NEGOCIO_ID)).thenReturn(Optional.of(negocio));
        when(servicioRepository.existsByNegocioIdNegocioAndNombreIgnoreCase(NEGOCIO_ID, request.getNombre()))
                .thenReturn(true);

        assertThatThrownBy(() -> servicioService.crearServicio(request, USUARIO_ID))
                .isInstanceOf(RecursoDuplicadoException.class)
                .hasMessageContaining("Ya existe un servicio con ese nombre");

        verify(modalidadRepository, never()).findById(any());
        verify(servicioRepository, never()).save(any());
    }

    @Test
    void crearServicio_delegaLaComparacionCaseInsensitiveAlRepositorio() {
        // La insensibilidad a mayúsculas/minúsculas la implementa la query derivada
        // (sufijo IgnoreCase); el service solo debe delegar el nombre tal cual fue ingresado.
        request.setNombre("CORTE de Cabello");
        when(usuarioRepository.findById(USUARIO_ID)).thenReturn(Optional.of(propietario));
        when(negocioRepository.findById(NEGOCIO_ID)).thenReturn(Optional.of(negocio));
        when(servicioRepository.existsByNegocioIdNegocioAndNombreIgnoreCase(NEGOCIO_ID, "CORTE de Cabello"))
                .thenReturn(true);

        assertThatThrownBy(() -> servicioService.crearServicio(request, USUARIO_ID))
                .isInstanceOf(RecursoDuplicadoException.class);

        verify(servicioRepository).existsByNegocioIdNegocioAndNombreIgnoreCase(NEGOCIO_ID, "CORTE de Cabello");
    }

    // ---------- Modalidad ----------

    @Test
    void crearServicio_modalidadNoExiste_lanzaIllegalArgumentException() {
        when(usuarioRepository.findById(USUARIO_ID)).thenReturn(Optional.of(propietario));
        when(negocioRepository.findById(NEGOCIO_ID)).thenReturn(Optional.of(negocio));
        when(servicioRepository.existsByNegocioIdNegocioAndNombreIgnoreCase(NEGOCIO_ID, request.getNombre()))
                .thenReturn(false);
        when(modalidadRepository.findById(MODALIDAD_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> servicioService.crearServicio(request, USUARIO_ID))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Modalidad no válida");

        verify(servicioRepository, never()).save(any());
    }

    // ---------- listarServiciosPorNegocio ----------

    @Test
    void listarServiciosPorNegocio_conServiciosExistentes_retornaListaDelRepositorio() {
        Servicio servicio = Servicio.builder().idServicio(1).negocio(negocio).modalidad(modalidad)
                .nombre("Corte").descripcion("desc").duracionMinutos(30).precio(BigDecimal.TEN).build();
        when(servicioRepository.findByNegocioIdNegocio(NEGOCIO_ID)).thenReturn(List.of(servicio));

        List<Servicio> resultado = servicioService.listarServiciosPorNegocio(NEGOCIO_ID);

        assertThat(resultado).containsExactly(servicio);
    }

    @Test
    void listarServiciosPorNegocio_sinServiciosRegistrados_retornaListaVacia() {
        // Caso límite: negocio existente pero sin servicios en su catálogo
        when(servicioRepository.findByNegocioIdNegocio(NEGOCIO_ID)).thenReturn(List.of());

        List<Servicio> resultado = servicioService.listarServiciosPorNegocio(NEGOCIO_ID);

        assertThat(resultado).isEmpty();
    }
}
