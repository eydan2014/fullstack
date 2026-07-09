package com.example.inventario.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class InsumoRequestDTO {

    @NotBlank(message = "El nombre del insumo es obligatorio")
    private String nombre;

    @NotNull(message = "La cantidad de stock no puede ser nula")
    @Min(value = 0, message = "El stock inicial no puede ser negativo")
    private Integer stock;
}