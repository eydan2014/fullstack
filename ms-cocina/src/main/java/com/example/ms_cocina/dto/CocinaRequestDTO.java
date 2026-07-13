package com.example.ms_cocina.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CocinaRequestDTO {

    @NotNull(message = "El id del pedido es obligatorio")
    private Integer pedidoId;

    private String observacion;
}