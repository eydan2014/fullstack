package com.example.pago.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import com.example.pago.dto.PagoRequest;
import com.example.pago.model.pago;
import com.example.pago.repository.PagoRepository;
@Service
@RequiredArgsConstructor
public class PagoService {
    private final PagoRepository pagoRepo;
     public pago procesopagar(PagoRequest request, String usuarioId, BigDecimal precioUnitario) {
        
        pago nuevoPago = new pago();
        nuevoPago.setUsuarioId(usuarioId);
        nuevoPago.setProductoId(request.getProductoId());
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