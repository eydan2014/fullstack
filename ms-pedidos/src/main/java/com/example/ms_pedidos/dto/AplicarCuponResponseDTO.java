package com.example.ms_pedidos.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AplicarCuponResponseDTO {

    private String codigo;

    private Boolean valido;

    private BigDecimal descuento;

    private BigDecimal totalFinal;

    private String mensaje;
}
