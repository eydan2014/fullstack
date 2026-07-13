package com.example.ms_cupones.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AplicarCuponRequestDTO {

    @NotBlank(message = "El código del cupón es obligatorio")
    private String codigo;

    @NotNull(message = "El monto del pedido es obligatorio")
    @Positive(message = "El monto del pedido debe ser mayor a 0")
    private BigDecimal montoPedido;
}
