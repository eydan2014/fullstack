package com.example.fidelidad.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class FidelidadRequestDTO {
    @NotBlank(message = "El usuario no puede estar en blanco")
    private String usuario; // El username que viene del JWT
    
    @NotNull(message = "El monto no puede ser nulo")
    @Positive(message = "El monto debe ser un número positivo")
    private BigDecimal monto; // El monto del pago que se usará para calcular los puntos
}
