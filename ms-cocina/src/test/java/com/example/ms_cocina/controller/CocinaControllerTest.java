package com.example.ms_cocina.controller;

import com.example.ms_cocina.dto.CocinaRequestDTO;
import com.example.ms_cocina.Controller.CocinaController;
import com.example.ms_cocina.Model.TicketCocina;
import com.example.ms_cocina.Service.CocinaService;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest(CocinaController.class)
@org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc(addFilters = false)
class CocinaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CocinaService cocinaService;

    @Test
    void listarTickets_debeRetornarStatus200() throws Exception {

        TicketCocina ticket = crearTicket();

        when(cocinaService.listarTickets()).thenReturn(List.of(ticket));

        mockMvc.perform(get("/api/cocina"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].pedidoId").value(1))
                .andExpect(jsonPath("$.data[0].estado").value("PENDIENTE"))
                .andExpect(jsonPath("$.data[0].observacion").value("Sin azúcar"));

        verify(cocinaService, times(1)).listarTickets();
    }

    @Test
    void obtenerTicket_debeRetornarStatus200() throws Exception {

        TicketCocina ticket = crearTicket();

        when(cocinaService.obtenerTicket(1)).thenReturn(ticket);

        mockMvc.perform(get("/api/cocina/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.pedidoId").value(1))
                .andExpect(jsonPath("$.data.estado").value("PENDIENTE"));

        verify(cocinaService, times(1)).obtenerTicket(1);
    }

    @Test
    void crearTicket_debeRetornarStatus201() throws Exception {

        CocinaRequestDTO request = crearCocinaRequest();
        TicketCocina ticketCreado = crearTicket();

        when(cocinaService.crearTicket(any(CocinaRequestDTO.class))).thenReturn(ticketCreado);

        mockMvc.perform(post("/api/cocina")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.pedidoId").value(1))
                .andExpect(jsonPath("$.data.estado").value("PENDIENTE"))
                .andExpect(jsonPath("$.data.observacion").value("Sin azúcar"));

        verify(cocinaService, times(1)).crearTicket(any(CocinaRequestDTO.class));
    }

    @Test
    void crearTicketConDatosInvalidos_debeRetornarStatus400() throws Exception {

        CocinaRequestDTO request = new CocinaRequestDTO();
        request.setPedidoId(null);
        request.setObservacion("Sin azúcar");

        mockMvc.perform(post("/api/cocina")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(cocinaService, never()).crearTicket(any(CocinaRequestDTO.class));
    }

    @Test
    void actualizarEstado_debeRetornarStatus200() throws Exception {

        TicketCocina ticket = crearTicket();
        ticket.setEstado("PREPARANDO");

        when(cocinaService.actualizarEstado(eq(1), eq("PREPARANDO"))).thenReturn(ticket);

        mockMvc.perform(put("/api/cocina/1/estado")
                        .param("estado", "PREPARANDO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.estado").value("PREPARANDO"));

        verify(cocinaService, times(1)).actualizarEstado(1, "PREPARANDO");
    }

    @Test
    void eliminarTicket_debeRetornarStatus204() throws Exception {

        doNothing().when(cocinaService).eliminarTicket(1);

        mockMvc.perform(delete("/api/cocina/1"))
                .andExpect(status().isOk());

        verify(cocinaService, times(1)).eliminarTicket(1);
    }

    private TicketCocina crearTicket() {

        TicketCocina ticket = new TicketCocina();
        ticket.setId(1);
        ticket.setPedidoId(1);
        ticket.setEstado("PENDIENTE");
        ticket.setObservacion("Sin azúcar");

        return ticket;
    }

    private CocinaRequestDTO crearCocinaRequest() {

        CocinaRequestDTO dto = new CocinaRequestDTO();
        dto.setPedidoId(1);
        dto.setObservacion("Sin azúcar");

        return dto;
    }
}


