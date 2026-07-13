package com.example.aviso.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AvisoRequestDTO {
    
    @NotBlank(message = "El usuario es obligatorio")
    private String usuario;
    
    @NotBlank(message = "El mensaje no puede estar vacío")
    private String mensaje;
    
    @NotBlank(message = "El tipo de aviso es obligatorio")
    private String tipo;

}
