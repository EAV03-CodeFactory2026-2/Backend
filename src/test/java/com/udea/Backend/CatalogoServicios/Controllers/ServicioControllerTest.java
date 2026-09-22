package com.udea.Backend.CatalogoServicios.Controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.udea.Backend.CatalogoServicios.Controllers.DTOs.ServicioCreateRequest;
import com.udea.Backend.CatalogoServicios.Entities.Servicio;
import com.udea.Backend.CatalogoServicios.Services.IServicioService;
import com.udea.Backend.Plataforma.Exceptions.RecursoDuplicadoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.math.BigDecimal;
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
 * Pruebas unitarias de {@link ServicioController}, verificando que las reglas
 * de negocio del service se traduzcan en los códigos HTTP y mensajes esperados
 * por los escenarios de registro de servicio.
 */
@ExtendWith(MockitoExtension.class)
class ServicioControllerTest {

    @Mock
    private IServicioService servicioService;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Principal principal = () -> "1";

    @BeforeEach
    void setUp() {
        ServicioController controller = new ServicioController(servicioService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setValidator(new LocalValidatorFactoryBean())
                .build();
    }

    private ServicioCreateRequest requestValido() {
        ServicioCreateRequest request = new ServicioCreateRequest();
        request.setNegocioId(10);
        request.setNombre("Corte de cabello");
        request.setDescripcion("Corte clásico");
        request.setDuracionMinutos(30);
        request.setPrecio(new BigDecimal("15.50"));
        request.setModalidadId(1);
        return request;
    }

    // ---------- Escenario 1: Registro exitoso ----------

    @Test
    void crearServicio_conDatosValidos_retorna201ConIdDelServicio() throws Exception {
        Servicio servicioCreado = Servicio.builder().idServicio(99).estado("No Asignado").build();
        when(servicioService.crearServicio(any(ServicioCreateRequest.class), anyInt())).thenReturn(servicioCreado);

        mockMvc.perform(post("/api/v1/servicios")
                        .principal(principal)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestValido())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idServicio").value(99))
                .andExpect(jsonPath("$.mensaje").value("Servicio creado exitosamente con estado 'No Asignado'."));
    }

    // ---------- Escenario 2: Nombre duplicado ----------

    @Test
    void crearServicio_conNombreDuplicado_retorna409ConMensajeDeError() throws Exception {
        when(servicioService.crearServicio(any(ServicioCreateRequest.class), anyInt()))
                .thenThrow(new RecursoDuplicadoException("Ya existe un servicio con ese nombre en este negocio."));

        mockMvc.perform(post("/api/v1/servicios")
                        .principal(principal)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestValido())))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Ya existe un servicio con ese nombre en este negocio."));
    }

    // ---------- Autorización / negocio ajeno ----------

    @Test
    void crearServicio_conNegocioQueNoPerteneceAlUsuario_retorna400ConMensajeDeError() throws Exception {
        when(servicioService.crearServicio(any(ServicioCreateRequest.class), anyInt()))
                .thenThrow(new IllegalArgumentException("No tienes permisos para agregar servicios a este negocio."));

        mockMvc.perform(post("/api/v1/servicios")
                        .principal(principal)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestValido())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("No tienes permisos para agregar servicios a este negocio."));
    }

    // ---------- Escenario 3: Duración inválida ----------

    @Test
    void crearServicio_conDuracionEnCero_retorna400ConErrorDeValidacion() throws Exception {
        ServicioCreateRequest request = requestValido();
        request.setDuracionMinutos(0);

        mockMvc.perform(post("/api/v1/servicios")
                        .principal(principal)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.duracionMinutos")
                        .value("La duración en minutos debe ser estrictamente mayor a cero"));
    }

    // ---------- Escenario 4: Precio inválido ----------

    @Test
    void crearServicio_conPrecioNegativo_retorna400ConErrorDeValidacion() throws Exception {
        ServicioCreateRequest request = requestValido();
        request.setPrecio(new BigDecimal("-1.00"));

        mockMvc.perform(post("/api/v1/servicios")
                        .principal(principal)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.precio").value("El precio no acepta valores negativos"));
    }

    // ---------- Escenario 5: Campos obligatorios ----------

    @Test
    void crearServicio_sinModalidadSeleccionada_retorna400ConErrorDeValidacion() throws Exception {
        ServicioCreateRequest request = requestValido();
        request.setModalidadId(null);

        mockMvc.perform(post("/api/v1/servicios")
                        .principal(principal)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.modalidadId").value("La modalidad es obligatoria"));
    }

    // ---------- Escenario 6: Longitud de campos de texto ----------

    @Test
    void crearServicio_conNombreQueExcedeLongitudMaxima_retorna400ConErrorDeValidacion() throws Exception {
        ServicioCreateRequest request = requestValido();
        request.setNombre("A".repeat(101));

        mockMvc.perform(post("/api/v1/servicios")
                        .principal(principal)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.nombre").value("El nombre no puede superar los 100 caracteres"));
    }

    // ---------- Listado de servicios ----------

    @Test
    void listarPorNegocio_retorna200ConElListadoDelService() throws Exception {
        Servicio servicio = Servicio.builder().idServicio(1).estado("No Asignado").build();
        when(servicioService.listarServiciosPorNegocio(10)).thenReturn(List.of(servicio));

        mockMvc.perform(get("/api/v1/servicios/negocio/{negocioId}", 10))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idServicio").value(1));
    }

    @Test
    void listarPorNegocio_sinServicios_retorna200ConListaVacia() throws Exception {
        // Caso límite: negocio válido pero sin servicios registrados en su catálogo
        when(servicioService.listarServiciosPorNegocio(10)).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/servicios/negocio/{negocioId}", 10))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
