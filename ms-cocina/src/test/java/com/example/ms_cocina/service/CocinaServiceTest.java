package com.example.ms_cocina.service;

import com.example.ms_cocina.client.PedidoClient;
import com.example.ms_cocina.dto.CocinaRequestDTO;
import com.example.ms_cocina.exception.ResourceNotFoundException;
import com.example.ms_cocina.Model.TicketCocina;
import com.example.ms_cocina.Repository.TicketCocinaRepository;
import com.example.ms_cocina.Service.CocinaService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class CocinaServiceTest {

    @Mock
    private TicketCocinaRepository ticketCocinaRepository;

    @Mock
    private PedidoClient pedidoClient;

    @InjectMocks
    private CocinaService cocinaService;

    @Test
    void crearTicket_cuandoPedidoExiste_debeCrearTicket() {

        CocinaRequestDTO dto = new CocinaRequestDTO();
        dto.setPedidoId(1);
        dto.setObservacion("Sin azúcar");

        when(pedidoClient.existePedido(1)).thenReturn(true);

        when(ticketCocinaRepository.save(any(TicketCocina.class))).thenAnswer(invocation -> {
            TicketCocina ticket = invocation.getArgument(0);
            ticket.setId(1);
            return ticket;
        });

        TicketCocina resultado = cocinaService.crearTicket(dto);

        assertNotNull(resultado);
        assertEquals(1, resultado.getId());
        assertEquals(1, resultado.getPedidoId());
        assertEquals("PENDIENTE", resultado.getEstado());
        assertEquals("Sin azúcar", resultado.getObservacion());

        verify(pedidoClient, times(1)).existePedido(1);
        verify(ticketCocinaRepository, times(1)).save(any(TicketCocina.class));
    }

    @Test
    void crearTicket_cuandoPedidoNoExiste_debeLanzarResourceNotFoundException() {

        CocinaRequestDTO dto = new CocinaRequestDTO();
        dto.setPedidoId(999);
        dto.setObservacion("Sin azúcar");

        when(pedidoClient.existePedido(999)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> {
            cocinaService.crearTicket(dto);
        });

        verify(pedidoClient, times(1)).existePedido(999);
        verify(ticketCocinaRepository, never()).save(any(TicketCocina.class));
    }

    @Test
    void listarTickets_debeRetornarLista() {

        TicketCocina ticket = crearTicket();

        when(ticketCocinaRepository.findAll()).thenReturn(List.of(ticket));

        List<TicketCocina> resultado = cocinaService.listarTickets();

        assertEquals(1, resultado.size());
        assertEquals("PENDIENTE", resultado.get(0).getEstado());

        verify(ticketCocinaRepository, times(1)).findAll();
    }

    @Test
    void obtenerTicket_cuandoExiste_debeRetornarTicket() {

        TicketCocina ticket = crearTicket();

        when(ticketCocinaRepository.findById(1)).thenReturn(Optional.of(ticket));

        TicketCocina resultado = cocinaService.obtenerTicket(1);

        assertNotNull(resultado);
        assertEquals(1, resultado.getId());
        assertEquals(1, resultado.getPedidoId());

        verify(ticketCocinaRepository, times(1)).findById(1);
    }

    @Test
    void obtenerTicket_cuandoNoExiste_debeLanzarResourceNotFoundException() {

        when(ticketCocinaRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            cocinaService.obtenerTicket(99);
        });

        verify(ticketCocinaRepository, times(1)).findById(99);
    }

    @Test
    void actualizarEstado_debeCambiarEstadoDelTicket() {

        TicketCocina ticket = crearTicket();

        when(ticketCocinaRepository.findById(1)).thenReturn(Optional.of(ticket));
        when(ticketCocinaRepository.save(any(TicketCocina.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TicketCocina resultado = cocinaService.actualizarEstado(1, "PREPARANDO");

        assertEquals("PREPARANDO", resultado.getEstado());

        verify(ticketCocinaRepository, times(1)).findById(1);
        verify(ticketCocinaRepository, times(1)).save(ticket);
    }

    @Test
    void eliminarTicket_debeEliminarTicketExistente() {

        TicketCocina ticket = crearTicket();

        when(ticketCocinaRepository.findById(1)).thenReturn(Optional.of(ticket));

        cocinaService.eliminarTicket(1);

        verify(ticketCocinaRepository, times(1)).findById(1);
        verify(ticketCocinaRepository, times(1)).delete(ticket);
    }

    private TicketCocina crearTicket() {

        TicketCocina ticket = new TicketCocina();
        ticket.setId(1);
        ticket.setPedidoId(1);
        ticket.setEstado("PENDIENTE");
        ticket.setObservacion("Sin azúcar");

        return ticket;
    }
}

