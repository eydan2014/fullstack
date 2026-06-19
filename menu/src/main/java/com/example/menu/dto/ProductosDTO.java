package com.example.menu.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProductosDTO {
@NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "La descripción es obligatoria")
    private String descripcion;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.1")
    private BigDecimal precio;

    @NotNull(message = "El stock es obligatorio")
    @Min(0)
    private Integer stock;

    @NotNull
    private boolean isHot; // bebida caliente o no
}