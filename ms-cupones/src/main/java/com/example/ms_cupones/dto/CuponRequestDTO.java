package com.example.ms_cupones.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CuponRequestDTO {

    @NotBlank(message = "El código del cupón es obligatorio")
    private String codigo;

    private String descripcion;

    @NotBlank(message = "El tipo de descuento es obligatorio")
    private String tipoDescuento;

    @NotNull(message = "El valor del descuento es obligatorio")
    @Positive(message = "El valor debe ser mayor a 0")
    private BigDecimal valor;

    @NotNull(message = "El monto mínimo es obligatorio")
    @PositiveOrZero(message = "El monto mínimo no puede ser negativo")
    private BigDecimal montoMinimo;

    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDate fechaInicio;

    @NotNull(message = "La fecha de fin es obligatoria")
    private LocalDate fechaFin;

    @NotNull(message = "Los usos máximos son obligatorios")
    @Min(value = 1, message = "El cupón debe tener al menos 1 uso máximo")
    private Integer usosMaximos;
}
