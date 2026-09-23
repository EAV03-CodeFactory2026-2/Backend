package com.udea.Backend.Negocios.Controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.udea.Backend.Negocios.Controllers.DTOs.NegocioCreateRequest;
import com.udea.Backend.Negocios.Entities.Negocio;
import com.udea.Backend.Negocios.Services.INegocioService;
import com.udea.Backend.Plataforma.Exceptions.RecursoDuplicadoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.security.Principal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Pruebas unitarias de {@link NegocioController}, verificando que las reglas
 * de negocio del service se traduzcan en los códigos HTTP y mensajes esperados
 * por los escenarios de creación de negocio (HU-0042).
 */
@ExtendWith(MockitoExtension.class)
class NegocioControllerTest {

    @Mock
    private INegocioService negocioService;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Principal principal = () -> "1";

    @BeforeEach
    void setUp() {
        NegocioController controller = new NegocioController(negocioService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setValidator(new LocalValidatorFactoryBean())
                .build();
    }

    private NegocioCreateRequest requestValido() {
        NegocioCreateRequest request = new NegocioCreateRequest();
        request.setNombreNegocio("Salón Bella Vista");
        request.setDireccionNegocio("Calle 10 # 20-30");
        request.setIdentificacionFiscal("900123456");
        request.setMonedaCodigoIso("COP");
        request.setTelefono("3001234567");
        return request;
    }

    // ---------- Escenario 1: Creación exitosa del primer negocio ----------

    @Test
    void crearNegocio_conDatosValidos_retorna201ConIdYMensaje() throws Exception {
        Negocio negocioCreado = Negocio.builder().idNegocio(55).estado("Activo").build();
        when(negocioService.crearNegocio(any(NegocioCreateRequest.class), anyInt())).thenReturn(negocioCreado);

        mockMvc.perform(post("/api/v1/negocios")
                        .principal(principal)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestValido())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idNegocio").value(55))
                .andExpect(jsonPath("$.mensaje").value("Negocio creado y activado exitosamente."));
    }

    // ---------- Escenario 3: Identificación fiscal duplicada ----------

    @Test
    void crearNegocio_conIdentificacionFiscalDuplicada_retorna409ConMensajeDeError() throws Exception {
        when(negocioService.crearNegocio(any(NegocioCreateRequest.class), anyInt()))
                .thenThrow(new RecursoDuplicadoException("La identificación fiscal ya está en uso en la plataforma."));

        mockMvc.perform(post("/api/v1/negocios")
                        .principal(principal)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestValido())))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("La identificación fiscal ya está en uso en la plataforma."));
    }

    // ---------- Autorización ----------

    @Test
    void crearNegocio_conUsuarioSinRolPropietario_retorna400ConMensajeDeError() throws Exception {
        when(negocioService.crearNegocio(any(NegocioCreateRequest.class), anyInt()))
                .thenThrow(new IllegalArgumentException("El usuario especificado no tiene el rol de Propietario."));

        mockMvc.perform(post("/api/v1/negocios")
                        .principal(principal)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestValido())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("El usuario especificado no tiene el rol de Propietario."));
    }

    // ---------- Escenario 4: Campos obligatorios vacíos ----------

    @Test
    void crearNegocio_conNombreEnBlanco_retorna400ConErrorDeValidacion() throws Exception {
        NegocioCreateRequest request = requestValido();
        request.setNombreNegocio("   ");

        mockMvc.perform(post("/api/v1/negocios")
                        .principal(principal)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.nombreNegocio").value("El nombre del negocio es obligatorio"));
    }

    @Test
    void crearNegocio_conMonedaSinSeleccionar_retorna400ConErrorDeValidacion() throws Exception {
        NegocioCreateRequest request = requestValido();
        request.setMonedaCodigoIso("");

        mockMvc.perform(post("/api/v1/negocios")
                        .principal(principal)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.monedaCodigoIso").value("El código de moneda es obligatorio"));
    }

    // ---------- Escenario 5: Longitudes o formatos inválidos ----------

    @Test
    void crearNegocio_conIdentificacionFiscalMasCortaQueElMinimo_retorna400ConErrorDeValidacion() throws Exception {
        NegocioCreateRequest request = requestValido();
        request.setIdentificacionFiscal("1234567"); // 7 caracteres, mínimo permitido es 9

        mockMvc.perform(post("/api/v1/negocios")
                        .principal(principal)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.identificacionFiscal")
                        .value("La identificación fiscal debe tener entre 9 y 20 caracteres"));
    }

    @Test
    void crearNegocio_conIdentificacionFiscalConCaracteresNoAlfanumericos_retorna400ConErrorDeValidacion() throws Exception {
        NegocioCreateRequest request = requestValido();
        request.setIdentificacionFiscal("900-123-456");

        mockMvc.perform(post("/api/v1/negocios")
                        .principal(principal)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.identificacionFiscal")
                        .value("La identificación fiscal debe contener solo caracteres alfanuméricos"));
    }

    @Test
    void crearNegocio_conNombreQueExcedeLongitudMaxima_retorna400ConErrorDeValidacion() throws Exception {
        NegocioCreateRequest request = requestValido();
        request.setNombreNegocio("A".repeat(251));

        mockMvc.perform(post("/api/v1/negocios")
                        .principal(principal)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.nombreNegocio").value("El nombre debe tener entre 3 y 250 caracteres"));
    }

    // ---------- Listado de negocios ----------

    @Test
    void listarMisNegocios_retorna200ConElListadoDelService() throws Exception {
        Negocio negocio = Negocio.builder().idNegocio(1).nombreNegocio("Salón Bella Vista").estado("Activo").build();
        when(negocioService.listarMisNegocios(1)).thenReturn(List.of(negocio));

        mockMvc.perform(get("/api/v1/negocios").principal(principal))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idNegocio").value(1));
    }

    @Test
    void listarMisNegocios_sinNegociosRegistrados_retorna200ConListaVacia() throws Exception {
        // Caso límite: propietario recién registrado, aún sin negocios creados.
        when(negocioService.listarMisNegocios(1)).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/negocios").principal(principal))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void listarMisNegocios_conUsuarioSinRolPropietario_retorna400ConMensajeDeError() throws Exception {
        when(negocioService.listarMisNegocios(1))
                .thenThrow(new IllegalArgumentException("El usuario no tiene el rol de Propietario."));

        mockMvc.perform(get("/api/v1/negocios").principal(principal))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("El usuario no tiene el rol de Propietario."));
    }
}
