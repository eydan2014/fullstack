package com.example.pago.Repository;

import com.example.pago.model.pago;
import com.example.pago.repository.PagoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class PagoRepositoryTest {

    @Autowired
    private PagoRepository repository;

    @Test
    void debeGuardarPago() {
        // 1. GIVEN
        pago nuevoPago = new pago();
        nuevoPago.setUsuarioId("user-100");
        nuevoPago.setProductoId(15L);
        nuevoPago.setCantidad(2);
        nuevoPago.setMetodoPago("WEBPAY");
        nuevoPago.setMontoTotal(new BigDecimal("5000"));

        // 2. WHEN
        pago guardado = repository.save(nuevoPago);

        // 3. THEN
        assertNotNull(guardado.getId(), "El ID autoincremental debería haberse generado");
        assertEquals("user-100", guardado.getUsuarioId());
        assertEquals(new BigDecimal("5000"), guardado.getMontoTotal());
    }

    @Test
    void debeBuscarPagoPorId() {
        // 1. GIVEN
        pago nuevoPago = new pago();
        nuevoPago.setUsuarioId("user-200");
        nuevoPago.setProductoId(5L);
        nuevoPago.setCantidad(1);
        nuevoPago.setMetodoPago("EFECTIVO");
        nuevoPago.setMontoTotal(new BigDecimal("2500"));
        
        pago guardado = repository.save(nuevoPago);

        // 2. WHEN
        Optional<pago> resultado = repository.findById(guardado.getId());

        // 3. THEN
        assertTrue(resultado.isPresent(), "El pago debería existir");
        assertEquals("user-200", resultado.get().getUsuarioId());
    }

    // 🚀 TEST CLAVE: Verifica tu método personalizado findByUsuarioId
    @Test
    void debeBuscarPagosPorUsuarioId() {
        // 1. GIVEN: Registramos 2 pagos para el "usuario-frecuente" y 1 para otro usuario
        pago p1 = new pago();
        p1.setUsuarioId("usuario-frecuente");
        p1.setProductoId(1L);
        p1.setCantidad(1);
        p1.setMetodoPago("TARJETA");
        p1.setMontoTotal(new BigDecimal("3000"));
        repository.save(p1);

        pago p2 = new pago();
        p2.setUsuarioId("usuario-frecuente");
        p2.setProductoId(2L);
        p2.setCantidad(2);
        p2.setMetodoPago("WEBPAY");
        p2.setMontoTotal(new BigDecimal("6000"));
        repository.save(p2);

        pago p3 = new pago();
        p3.setUsuarioId("usuario-diferente");
        p3.setProductoId(3L);
        p3.setCantidad(1);
        p3.setMetodoPago("EFECTIVO");
        p3.setMontoTotal(new BigDecimal("1500"));
        repository.save(p3);

        // 2. WHEN: Buscamos usando tu método personalizado
        List<pago> pagosUsuario = repository.findByUsuarioId("usuario-frecuente");

        // 3. THEN: Comprobamos que solo traiga los del usuario consultado
        assertNotNull(pagosUsuario);
        assertEquals(2, pagosUsuario.size(), "Debería retornar exactamente los 2 pagos del usuario-frecuente");
        
        // Verificamos que todos los elementos pertenezcan de verdad a ese usuario
        assertTrue(pagosUsuario.stream().allMatch(p -> p.getUsuarioId().equals("usuario-frecuente")));
    }
}