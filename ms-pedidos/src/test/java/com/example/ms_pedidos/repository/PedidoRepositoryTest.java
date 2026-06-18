package com.example.ms_pedidos.repository;

import com.example.ms_pedidos.model.Pedido;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
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
class PedidoRepositoryTest {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Test
    void guardarPedido_debeGuardarCorrectamente() {

        Pedido pedido = new Pedido();
        pedido.setUsuarioId(1);
        pedido.setTotal(6500.0);
        pedido.setEstado("PENDIENTE");

        Pedido guardado = pedidoRepository.save(pedido);

        assertNotNull(guardado.getId());
        assertEquals(1, guardado.getUsuarioId());
        assertEquals(6500.0, guardado.getTotal(), 0.001);
        assertEquals("PENDIENTE", guardado.getEstado());
    }

    @Test
    void buscarPedidoPorId_cuandoExiste_debeRetornarPedido() {

        Pedido pedido = new Pedido();
        pedido.setUsuarioId(1);
        pedido.setTotal(6500.0);
        pedido.setEstado("PENDIENTE");

        Pedido guardado = pedidoRepository.save(pedido);

        Optional<Pedido> resultado = pedidoRepository.findById(guardado.getId());

        assertTrue(resultado.isPresent());
        assertEquals(guardado.getId(), resultado.get().getId());
        assertEquals("PENDIENTE", resultado.get().getEstado());
    }

    @Test
    void listarPedidos_debeRetornarTodosLosPedidos() {

        Pedido pedido1 = new Pedido();
        pedido1.setUsuarioId(1);
        pedido1.setTotal(6500.0);
        pedido1.setEstado("PENDIENTE");

        Pedido pedido2 = new Pedido();
        pedido2.setUsuarioId(2);
        pedido2.setTotal(4000.0);
        pedido2.setEstado("PAGADO");

        pedidoRepository.save(pedido1);
        pedidoRepository.save(pedido2);

        List<Pedido> pedidos = pedidoRepository.findAll();

        assertEquals(2, pedidos.size());
    }

    @Test
    void eliminarPedido_debeEliminarCorrectamente() {

        Pedido pedido = new Pedido();
        pedido.setUsuarioId(1);
        pedido.setTotal(6500.0);
        pedido.setEstado("PENDIENTE");

        Pedido guardado = pedidoRepository.save(pedido);

        pedidoRepository.delete(guardado);

        Optional<Pedido> resultado = pedidoRepository.findById(guardado.getId());

        assertTrue(resultado.isEmpty());
    }
}