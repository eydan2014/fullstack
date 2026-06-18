package com.example.ms_pedidos.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PedidoResponseDTO {

    private Long id;

    private Long usuarioId;

    private Double total;

    private String estado;
}
