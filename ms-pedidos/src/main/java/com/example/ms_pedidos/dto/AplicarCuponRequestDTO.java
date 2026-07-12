package com.example.ms_pedidos.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AplicarCuponRequestDTO {

    private String codigo;

    private BigDecimal montoPedido;
}

