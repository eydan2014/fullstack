package com.example.resena.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ResenaRequestDTO {

    @NotNull(message = "El ID del producto es obligatorio")
    private Long idProducto;

    @NotBlank(message = "El nombre de usuario es obligatorio")
    private String usuario;

    @NotNull(message = "La calificación no puede ser nula")
    @Min(value = 1, message = "La calificación mínima es 1 estrella")
    @Max(value = 5, message = "La calificación máxima es 5 estrellas")
    private Integer calificacion;

    @Size(max = 500, message = "El comentario no puede superar los 500 caracteres")
    private String comentario;
}