package com.example.ms_pedidos.service;

import com.example.ms_pedidos.client.CuponClient;
import com.example.ms_pedidos.dto.AplicarCuponResponseDTO;
import com.example.ms_pedidos.dto.PedidoRequestDTO;
import com.example.ms_pedidos.exception.ResourceNotFoundException;
import com.example.ms_pedidos.model.DetallePedido;
import com.example.ms_pedidos.model.Pedido;
import com.example.ms_pedidos.repository.DetallePedidoRepository;
import com.example.ms_pedidos.repository.PedidoRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private DetallePedidoRepository detallePedidoRepository;

    @Mock
    private CuponClient cuponClient;

    @InjectMocks
    private PedidoService pedidoService;

    @Test
    void crearPedidoSinCupon_debeCalcularTotalYGuardarPedido() {

        PedidoRequestDTO dto = crearPedidoRequestSinCupon();

        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> {
            Pedido pedido = invocation.getArgument(0);
            pedido.setId(1);
            return pedido;
        });

        Pedido resultado = pedidoService.crearPedido(dto);

        assertNotNull(resultado);
        assertEquals(1, resultado.getId());
        assertEquals(1, resultado.getUsuarioId());
        assertEquals("PENDIENTE", resultado.getEstado());
        assertEquals(6500.0, resultado.getTotal(), 0.001);

        verify(pedidoRepository, times(1)).save(any(Pedido.class));
        verify(detallePedidoRepository, times(2)).save(any(DetallePedido.class));
        verify(cuponClient, never()).aplicarCupon(anyString(), any(BigDecimal.class));
    }

    @Test
    void crearPedidoConCupon_debeAplicarDescuentoYGuardarTotalFinal() {

        PedidoRequestDTO dto = crearPedidoRequestConCupon();

        AplicarCuponResponseDTO cuponResponse = new AplicarCuponResponseDTO();
        cuponResponse.setCodigo("CAFE10");
        cuponResponse.setValido(true);
        cuponResponse.setDescuento(new BigDecimal("650.00"));
        cuponResponse.setTotalFinal(new BigDecimal("5850.00"));
        cuponResponse.setMensaje("Cupón aplicado correctamente");

        when(cuponClient.aplicarCupon(eq("CAFE10"), any(BigDecimal.class)))
                .thenReturn(cuponResponse);

        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> {
            Pedido pedido = invocation.getArgument(0);
            pedido.setId(1);
            return pedido;
        });

        Pedido resultado = pedidoService.crearPedido(dto);

        assertNotNull(resultado);
        assertEquals(1, resultado.getId());
        assertEquals(5850.0, resultado.getTotal(), 0.001);
        assertEquals("PENDIENTE", resultado.getEstado());

        verify(cuponClient, times(1)).aplicarCupon(eq("CAFE10"), any(BigDecimal.class));
        verify(pedidoRepository, times(1)).save(any(Pedido.class));
        verify(detallePedidoRepository, times(2)).save(any(DetallePedido.class));
    }

    @Test
    void crearPedido_debeGuardarDetallesConPedidoId() {

        PedidoRequestDTO dto = crearPedidoRequestSinCupon();

        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> {
            Pedido pedido = invocation.getArgument(0);
            pedido.setId(10);
            return pedido;
        });

        pedidoService.crearPedido(dto);

        ArgumentCaptor<DetallePedido> captor = ArgumentCaptor.forClass(DetallePedido.class);

        verify(detallePedidoRepository, times(2)).save(captor.capture());

        List<DetallePedido> detallesGuardados = captor.getAllValues();

        assertEquals(10, detallesGuardados.get(0).getPedidoId());
        assertEquals(1, detallesGuardados.get(0).getProductoId());
        assertEquals(2, detallesGuardados.get(0).getCantidad());
        assertEquals(2500.0, detallesGuardados.get(0).getPrecio(), 0.001);

        assertEquals(10, detallesGuardados.get(1).getPedidoId());
        assertEquals(2, detallesGuardados.get(1).getProductoId());
        assertEquals(1, detallesGuardados.get(1).getCantidad());
        assertEquals(1500.0, detallesGuardados.get(1).getPrecio(), 0.001);
    }

    @Test
    void obtenerPedido_cuandoExiste_debeRetornarPedido() {

        Pedido pedido = new Pedido();
        pedido.setId(1);
        pedido.setUsuarioId(1);
        pedido.setTotal(6500.0);
        pedido.setEstado("PENDIENTE");

        when(pedidoRepository.findById(1)).thenReturn(Optional.of(pedido));

        Pedido resultado = pedidoService.obtenerPedido(1);

        assertNotNull(resultado);
        assertEquals(1, resultado.getId());
        assertEquals("PENDIENTE", resultado.getEstado());

        verify(pedidoRepository, times(1)).findById(1);
    }

    @Test
    void obtenerPedido_cuandoNoExiste_debeLanzarResourceNotFoundException() {

        when(pedidoRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            pedidoService.obtenerPedido(99);
        });

        verify(pedidoRepository, times(1)).findById(99);
    }

    @Test
    void actualizarEstado_debeCambiarEstadoDelPedido() {

        Pedido pedido = new Pedido();
        pedido.setId(1);
        pedido.setUsuarioId(1);
        pedido.setTotal(6500.0);
        pedido.setEstado("PENDIENTE");

        when(pedidoRepository.findById(1)).thenReturn(Optional.of(pedido));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Pedido resultado = pedidoService.actualizarEstado(1, "PAGADO");

        assertEquals("PAGADO", resultado.getEstado());

        verify(pedidoRepository, times(1)).findById(1);
        verify(pedidoRepository, times(1)).save(pedido);
    }

    @Test
    void eliminarPedido_debeEliminarPedidoExistente() {

        Pedido pedido = new Pedido();
        pedido.setId(1);
        pedido.setUsuarioId(1);
        pedido.setTotal(6500.0);
        pedido.setEstado("PENDIENTE");

        when(pedidoRepository.findById(1)).thenReturn(Optional.of(pedido));

        pedidoService.eliminarPedido(1);

        verify(pedidoRepository, times(1)).findById(1);
        verify(pedidoRepository, times(1)).delete(pedido);
    }

    private PedidoRequestDTO crearPedidoRequestSinCupon() {

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

    private PedidoRequestDTO crearPedidoRequestConCupon() {

        PedidoRequestDTO dto = crearPedidoRequestSinCupon();
        dto.setCodigoCupon("CAFE10");

        return dto;
    }
}

