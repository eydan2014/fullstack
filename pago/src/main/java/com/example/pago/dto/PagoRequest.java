package com.example.pago.dto;

import lombok.Data;
@Data
public class PagoRequest {

    // 🔓 Sin JwtUtil: el usuario que realiza el pago viene explícito en el body
    // (antes se extraía del token JWT).
    private String usuario;

    private Long productoId;
    private Integer cantidad;
    private String metodoPago;

    // 🔗 Opcional: cuando el pago corresponde a un pedido ya creado en
    // ms-pedidos (PAGOS.id_orden -> PEDIDOS del modelo ER). Si se envía,
    // se valida contra ms-pedidos antes de procesar el pago.
    private Integer pedidoId;

}
