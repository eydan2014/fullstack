package com.example.ms_cocina.Service;

import com.example.ms_cocina.dto.CocinaRequestDTO;
import com.example.ms_cocina.exception.ResourceNotFoundException;
import com.example.ms_cocina.Model.TicketCocina;
import com.example.ms_cocina.Repository.TicketCocinaRepository;
import com.example.ms_cocina.client.PedidoClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CocinaService {

    private final TicketCocinaRepository ticketCocinaRepository;
    private final PedidoClient pedidoClient;

    public List<TicketCocina> listarTickets() {

        log.info("Listando tickets de cocina");

        return ticketCocinaRepository.findAll();
    }

    public TicketCocina obtenerTicket(Integer id) {

        log.info("Buscando ticket de cocina con id {}", id);

        return ticketCocinaRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Ticket de cocina no encontrado con id {}", id);
                    return new ResourceNotFoundException("Ticket de cocina no encontrado");
                });
    }

    public TicketCocina obtenerPorPedidoId(Integer pedidoId) {

        log.info("Buscando ticket de cocina por pedidoId {}", pedidoId);

        return ticketCocinaRepository.findByPedidoId(pedidoId)
                .orElseThrow(() -> {
                    log.warn("No existe ticket de cocina para el pedido {}", pedidoId);
                    return new ResourceNotFoundException("No existe ticket para ese pedido");
                });
    }

    public List<TicketCocina> listarPorEstado(String estado) {

        validarEstado(estado);

        String estadoUpper = estado.toUpperCase();

        log.info("Listando tickets de cocina con estado {}", estadoUpper);

        return ticketCocinaRepository.findByEstado(estadoUpper);
    }

    public TicketCocina crearTicket(CocinaRequestDTO dto) {

    log.info("Creando ticket de cocina para pedido {}", dto.getPedidoId());

    if (!pedidoClient.existePedido(dto.getPedidoId())) {
        throw new ResourceNotFoundException("El pedido no existe en ms_pedidos");
    }

    TicketCocina ticket = new TicketCocina();

    ticket.setPedidoId(dto.getPedidoId());
    ticket.setEstado("PENDIENTE");
    ticket.setObservacion(dto.getObservacion());

    TicketCocina ticketGuardado = ticketCocinaRepository.save(ticket);

    log.info("Ticket de cocina creado correctamente con id {}", ticketGuardado.getId());

    return ticketGuardado;
}

    public TicketCocina actualizarEstado(Integer id, String estado) {

        validarEstado(estado);

        String estadoUpper = estado.toUpperCase();

        log.info("Actualizando estado del ticket {} a {}", id, estadoUpper);

        TicketCocina ticket = obtenerTicket(id);

        ticket.setEstado(estadoUpper);

        TicketCocina ticketActualizado = ticketCocinaRepository.save(ticket);

        log.info("Estado actualizado correctamente para ticket {}", id);

        return ticketActualizado;
    }

    public TicketCocina actualizarObservacion(Integer id, String observacion) {

        log.info("Actualizando observación del ticket {}", id);

        TicketCocina ticket = obtenerTicket(id);

        ticket.setObservacion(observacion);

        TicketCocina ticketActualizado = ticketCocinaRepository.save(ticket);

        log.info("Observación actualizada correctamente para ticket {}", id);

        return ticketActualizado;
    }

    public void eliminarTicket(Integer id) {

        log.info("Eliminando ticket de cocina con id {}", id);

        TicketCocina ticket = obtenerTicket(id);

        ticketCocinaRepository.delete(ticket);

        log.info("Ticket de cocina eliminado correctamente con id {}", id);
    }

    private void validarEstado(String estado) {

        if (estado == null || estado.isBlank()) {
            log.warn("Estado inválido: vacío o nulo");
            throw new IllegalArgumentException("El estado es obligatorio");
        }

        String estadoUpper = estado.toUpperCase();

        if (!estadoUpper.equals("PENDIENTE")
                && !estadoUpper.equals("PREPARANDO")
                && !estadoUpper.equals("LISTO")
                && !estadoUpper.equals("ENTREGADO")) {

            log.warn("Estado inválido recibido: {}", estado);

            throw new IllegalArgumentException(
                    "Estado inválido. Estados permitidos: PENDIENTE, PREPARANDO, LISTO, ENTREGADO"
            );
        }
    }
}