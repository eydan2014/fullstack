package com.example.ms_pedidos.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class PedidoRequestDTO {

    @NotNull(message = "El usuario es obligatorio")
    private Integer usuarioId;

    @NotEmpty(message = "El pedido no puede estar vacio")
    private List<DetalleDTO> detalles;

    private String codigoCupon;

    @Data
    public static class DetalleDTO {

        @NotNull
        private Integer productoId;

        @Min(value = 1, message = "La cantidad debe ser mayor a 0")
        private Integer cantidad;

        @Min(value = 1, message = "El precio debe ser mayor a 0")
        private Double precio;
    }
}
