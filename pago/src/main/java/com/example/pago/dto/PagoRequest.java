package com.example.pago.dto;

import lombok.Data;
@Data
public class PagoRequest {
    

    private Long productoId;
    private Integer cantidad;
    private String metodoPago;

}
