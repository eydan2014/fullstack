package com.example.pago.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import com.example.pago.client.PedidoClient;
import com.example.pago.dto.PagoRequest;
import com.example.pago.model.pago;
import com.example.pago.repository.PagoRepository;
@Service
@RequiredArgsConstructor
public class PagoService {
    private final PagoRepository pagoRepo;
    private final PedidoClient pedidoClient;

     public pago procesopagar(PagoRequest request, String usuarioId, BigDecimal precioUnitario) {

        // 🔗 INTER-SERVICIO: si el pago referencia un pedido de ms-pedidos,
        // validamos que exista antes de registrar la transacción (PAGOS.id_orden -> PEDIDOS).
        if (request.getPedidoId() != null && !pedidoClient.existePedido(request.getPedidoId())) {
            throw new RuntimeException("El pedido " + request.getPedidoId() + " no existe");
        }

        pago nuevoPago = new pago();
        nuevoPago.setUsuarioId(usuarioId);
        nuevoPago.setProductoId(request.getProductoId());
        nuevoPago.setPedidoId(request.getPedidoId());
        nuevoPago.setCantidad(request.getCantidad());
        nuevoPago.setMetodoPago(request.getMetodoPago());
        
       BigDecimal total = precioUnitario.multiply(BigDecimal.valueOf(request.getCantidad()));
        nuevoPago.setMontoTotal(total);
        
        return pagoRepo.save(nuevoPago);

}
     public pago obtenerPagoPorId(Long id) {
        return pagoRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("Pago no encontrado con ID: " + id));
     }
}