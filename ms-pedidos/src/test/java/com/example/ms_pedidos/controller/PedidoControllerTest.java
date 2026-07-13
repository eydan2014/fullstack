package com.example.ms_pedidos.controller;

import com.example.ms_pedidos.dto.PedidoRequestDTO;
import com.example.ms_pedidos.model.Pedido;
import com.example.ms_pedidos.service.PedidoService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest(
        controllers = PedidoController.class
)
@org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
(addFilters = false)
@ActiveProfiles("test")
class PedidoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PedidoService pedidoService;

    @Test
    void listarPedidos_debeRetornarStatus200() throws Exception {

        Pedido pedido = crearPedido();

        when(pedidoService.listarPedidos()).thenReturn(List.of(pedido));

        mockMvc.perform(get("/api/pedidos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].usuarioId").value(1))
                .andExpect(jsonPath("$.data[0].total").value(6500.0))
                .andExpect(jsonPath("$.data[0].estado").value("PENDIENTE"));

        verify(pedidoService, times(1)).listarPedidos();
    }

    @Test
    void obtenerPedido_debeRetornarStatus200() throws Exception {

        Pedido pedido = crearPedido();

        when(pedidoService.obtenerPedido(1)).thenReturn(pedido);

        mockMvc.perform(get("/api/pedidos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.estado").value("PENDIENTE"));

        verify(pedidoService, times(1)).obtenerPedido(1);
    }

    @Test
    void crearPedido_debeRetornarStatus201() throws Exception {

        PedidoRequestDTO request = crearPedidoRequest();
        Pedido pedidoCreado = crearPedido();

        when(pedidoService.crearPedido(any(PedidoRequestDTO.class))).thenReturn(pedidoCreado);

        mockMvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.total").value(6500.0))
                .andExpect(jsonPath("$.data.estado").value("PENDIENTE"));

        verify(pedidoService, times(1)).crearPedido(any(PedidoRequestDTO.class));
    }

    @Test
    void crearPedidoConDatosInvalidos_debeRetornarStatus400() throws Exception {

        PedidoRequestDTO request = new PedidoRequestDTO();
        request.setUsuarioId(null);
        request.setDetalles(List.of());

        mockMvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(pedidoService, never()).crearPedido(any(PedidoRequestDTO.class));
    }

    @Test
    void actualizarEstado_debeRetornarStatus200() throws Exception {

        Pedido pedido = crearPedido();
        pedido.setEstado("PAGADO");

        when(pedidoService.actualizarEstado(eq(1), eq("PAGADO"))).thenReturn(pedido);

        mockMvc.perform(put("/api/pedidos/1/estado")
                        .param("estado", "PAGADO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.estado").value("PAGADO"));

        verify(pedidoService, times(1)).actualizarEstado(1, "PAGADO");
    }

    @Test
    void eliminarPedido_debeRetornarStatus204() throws Exception {

        doNothing().when(pedidoService).eliminarPedido(1);

        mockMvc.perform(delete("/api/pedidos/1"))
                .andExpect(status().isNoContent());

        verify(pedidoService, times(1)).eliminarPedido(1);
    }

    private Pedido crearPedido() {

        Pedido pedido = new Pedido();
        pedido.setId(1);
        pedido.setUsuarioId(1);
        pedido.setTotal(6500.0);
        pedido.setEstado("PENDIENTE");

        return pedido;
    }

    private PedidoRequestDTO crearPedidoRequest() {

        PedidoRequestDTO dto = new PedidoRequestDTO();
        dto.setUsuarioId(1);

        PedidoRequestDTO.DetalleDTO detalle1 = new PedidoRequestDTO.DetalleDTO();
        detalle1.setProductoId(1);
        detalle1.setCantidad(2);
        detalle1.setPrecio(2500.0);

        PedidoRequestDTO.DetalleDTO detalle2 = new PedidoRequestDTO.DetalleDTO();
        detalle2.setProductoId(2);
        detalle2.setCantidad(1);
        detalle2.setPrecio(1500.0);

        dto.setDetalles(List.of(detalle1, detalle2));

        return dto;
    }
}


