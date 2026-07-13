package com.example.ms_cupones.controller;

import com.example.ms_cupones.dto.AplicarCuponRequestDTO;
import com.example.ms_cupones.dto.AplicarCuponResponseDTO;
import com.example.ms_cupones.dto.CuponRequestDTO;
import com.example.ms_cupones.model.Cupon;
import com.example.ms_cupones.service.CuponService;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest(
        controllers = CuponController.class
)

@org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
(addFilters = false)
@ActiveProfiles("test")
class CuponControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CuponService cuponService;

    @Test
    void listarCupones_debeRetornarStatus200() throws Exception {

        Cupon cupon = crearCupon();

        when(cuponService.listarCupones()).thenReturn(List.of(cupon));

        mockMvc.perform(get("/api/cupones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].codigo").value("CAFE10"))
                .andExpect(jsonPath("$.data[0].tipoDescuento").value("PORCENTAJE"))
                .andExpect(jsonPath("$.data[0].activo").value(true));

        verify(cuponService, times(1)).listarCupones();
    }

    @Test
    void obtenerCupon_debeRetornarStatus200() throws Exception {

        Cupon cupon = crearCupon();

        when(cuponService.obtenerCupon(1)).thenReturn(cupon);

        mockMvc.perform(get("/api/cupones/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.codigo").value("CAFE10"));

        verify(cuponService, times(1)).obtenerCupon(1);
    }

    @Test
    void crearCupon_debeRetornarStatus201() throws Exception {

        CuponRequestDTO request = crearCuponRequest();
        Cupon cuponCreado = crearCupon();

        when(cuponService.crearCupon(any(CuponRequestDTO.class))).thenReturn(cuponCreado);

        mockMvc.perform(post("/api/cupones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.codigo").value("CAFE10"))
                .andExpect(jsonPath("$.data.tipoDescuento").value("PORCENTAJE"));

        verify(cuponService, times(1)).crearCupon(any(CuponRequestDTO.class));
    }

    @Test
    void crearCuponConDatosInvalidos_debeRetornarStatus400() throws Exception {

        CuponRequestDTO request = crearCuponRequest();
        request.setCodigo("");

        mockMvc.perform(post("/api/cupones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(cuponService, never()).crearCupon(any(CuponRequestDTO.class));
    }

    @Test
    void aplicarCupon_debeRetornarStatus200() throws Exception {

        AplicarCuponRequestDTO request = new AplicarCuponRequestDTO();
        request.setCodigo("CAFE10");
        request.setMontoPedido(new BigDecimal("6500"));

        AplicarCuponResponseDTO response = new AplicarCuponResponseDTO();
        response.setCodigo("CAFE10");
        response.setValido(true);
        response.setDescuento(new BigDecimal("650.00"));
        response.setTotalFinal(new BigDecimal("5850.00"));
        response.setMensaje("Cupón aplicado correctamente");

        when(cuponService.aplicarCupon(any(AplicarCuponRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/cupones/aplicar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.codigo").value("CAFE10"))
                .andExpect(jsonPath("$.data.valido").value(true))
                .andExpect(jsonPath("$.data.totalFinal").value(5850.00));

        verify(cuponService, times(1)).aplicarCupon(any(AplicarCuponRequestDTO.class));
    }

    @Test
    void actualizarEstado_debeRetornarStatus200() throws Exception {

        Cupon cupon = crearCupon();
        cupon.setActivo(false);

        when(cuponService.cambiarEstado(eq(1), eq(false))).thenReturn(cupon);
        mockMvc.perform(put("/api/cupones/1/estado")
                        .param("activo", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.activo").value(false));

        verify(cuponService, times(1)).cambiarEstado(1, false);
    }

    @Test
    void eliminarCupon_debeRetornarStatus204() throws Exception {

        doNothing().when(cuponService).eliminarCupon(1);

        mockMvc.perform(delete("/api/cupones/1"))
                .andExpect(status().isNoContent());

        verify(cuponService, times(1)).eliminarCupon(1);
    }

    private Cupon crearCupon() {

        Cupon cupon = new Cupon();
        cupon.setId(1);
        cupon.setCodigo("CAFE10");
        cupon.setDescripcion("Descuento de 10%");
        cupon.setTipoDescuento("PORCENTAJE");
        cupon.setValor(new BigDecimal("10"));
        cupon.setMontoMinimo(new BigDecimal("3000"));
        cupon.setFechaInicio(LocalDate.of(2026, 1, 1));
        cupon.setFechaFin(LocalDate.of(2026, 12, 31));
        cupon.setActivo(true);
        cupon.setUsosMaximos(100);
        cupon.setUsosActuales(0);

        return cupon;
    }

    private CuponRequestDTO crearCuponRequest() {

        CuponRequestDTO dto = new CuponRequestDTO();
        dto.setCodigo("CAFE10");
        dto.setDescripcion("Descuento de 10%");
        dto.setTipoDescuento("PORCENTAJE");
        dto.setValor(new BigDecimal("10"));
        dto.setMontoMinimo(new BigDecimal("3000"));
        dto.setFechaInicio(LocalDate.of(2026, 1, 1));
        dto.setFechaFin(LocalDate.of(2026, 12, 31));
        dto.setUsosMaximos(100);

        return dto;
    }
}
