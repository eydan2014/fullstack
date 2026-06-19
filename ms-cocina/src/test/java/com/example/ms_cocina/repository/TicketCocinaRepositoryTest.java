package com.example.ms_cocina.repository;

import org.junit.jupiter.api.Test;
import com.example.ms_cocina.Model.TicketCocina;
import com.example.ms_cocina.Repository.TicketCocinaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb;MODE=MySQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.flyway.enabled=false"
})
@ActiveProfiles("test")
class TicketCocinaRepositoryTest {

    @Autowired
    private TicketCocinaRepository ticketCocinaRepository;

    @Test
    void guardarTicket_debeGuardarCorrectamente() {

        TicketCocina ticket = new TicketCocina();
        ticket.setPedidoId(1);
        ticket.setEstado("PENDIENTE");
        ticket.setObservacion("Sin azúcar");

        TicketCocina guardado = ticketCocinaRepository.save(ticket);

        assertNotNull(guardado.getId());
        assertEquals(1, guardado.getPedidoId());
        assertEquals("PENDIENTE", guardado.getEstado());
        assertEquals("Sin azúcar", guardado.getObservacion());
    }

    @Test
    void buscarPorId_cuandoExiste_debeRetornarTicket() {

        TicketCocina ticket = new TicketCocina();
        ticket.setPedidoId(1);
        ticket.setEstado("PENDIENTE");
        ticket.setObservacion("Sin azúcar");

        TicketCocina guardado = ticketCocinaRepository.save(ticket);

        Optional<TicketCocina> resultado = ticketCocinaRepository.findById(guardado.getId());

        assertTrue(resultado.isPresent());
        assertEquals(guardado.getId(), resultado.get().getId());
        assertEquals("PENDIENTE", resultado.get().getEstado());
    }

    @Test
    void listarTickets_debeRetornarTodosLosTickets() {

        TicketCocina ticket1 = new TicketCocina();
        ticket1.setPedidoId(1);
        ticket1.setEstado("PENDIENTE");
        ticket1.setObservacion("Sin azúcar");

        TicketCocina ticket2 = new TicketCocina();
        ticket2.setPedidoId(2);
        ticket2.setEstado("PREPARANDO");
        ticket2.setObservacion("Con leche");

        ticketCocinaRepository.save(ticket1);
        ticketCocinaRepository.save(ticket2);

        List<TicketCocina> tickets = ticketCocinaRepository.findAll();

        assertEquals(2, tickets.size());
    }

    @Test
    void eliminarTicket_debeEliminarCorrectamente() {

        TicketCocina ticket = new TicketCocina();
        ticket.setPedidoId(1);
        ticket.setEstado("PENDIENTE");
        ticket.setObservacion("Sin azúcar");

        TicketCocina guardado = ticketCocinaRepository.save(ticket);

        ticketCocinaRepository.delete(guardado);

        Optional<TicketCocina> resultado = ticketCocinaRepository.findById(guardado.getId());

        assertTrue(resultado.isEmpty());
    }
}
