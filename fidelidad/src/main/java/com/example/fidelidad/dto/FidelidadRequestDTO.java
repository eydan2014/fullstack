package com.example.fidelidad.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class FidelidadRequestDTO {
private String usuario; // El username que viene del JWT
private BigDecimal monto; // El monto del pago que se usará para calcular los puntos
}
