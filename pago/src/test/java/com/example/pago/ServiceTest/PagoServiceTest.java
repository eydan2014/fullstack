package com.example.pago.ServiceTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.pago.dto.PagoRequest;
import com.example.pago.model.pago;
import com.example.pago.repository.PagoRepository;
import com.example.pago.service.PagoService;

@ExtendWith(MockitoExtension.class)
public class PagoServiceTest {

    @Mock
    private PagoRepository pagoRepo;

    @InjectMocks
    private PagoService service;

    @Test
    void deberiaProcesarPagoExitosamente() {
        // 1. GIVEN (Preparación de datos de entrada)

        PagoRequest request = new PagoRequest();
        request.setProductoId(10L);
        request.setCantidad(3);
        request.setMetodoPago("WEBPAY");

        String usuarioId = "user-123";
        BigDecimal precioUnitario = new BigDecimal("2500");
        

        BigDecimal montoTotalEsperado = new BigDecimal("7500");

                
        when(pagoRepo.save(any(pago.class))).thenAnswer(invocation -> {
            pago pagoGuardado = invocation.getArgument(0);
            pagoGuardado.setId(1L); 
            return pagoGuardado;
        });

        
        pago resultado = service.procesopagar(request, usuarioId, precioUnitario);

      
        assertNotNull(resultado, "El objeto de pago retornado no debería ser nulo");
        assertEquals(1L, resultado.getId(), "El ID debería haber sido asignado por el repositorio mock");
        assertEquals("user-123", resultado.getUsuarioId());
        assertEquals(10L, resultado.getProductoId());
        assertEquals(3, resultado.getCantidad());
        assertEquals("WEBPAY", resultado.getMetodoPago());
        assertEquals(montoTotalEsperado, resultado.getMontoTotal(), "El cálculo del monto total (precio * cantidad) es incorrecto");

       
        verify(pagoRepo).save(any(pago.class));
    }
}