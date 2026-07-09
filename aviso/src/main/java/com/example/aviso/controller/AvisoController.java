package com.example.aviso.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.aviso.dto.AvisoRequestDTO;
import com.example.aviso.exception.ApiResponse;
import com.example.aviso.service.AvisoService;

// 🚀 IMPORTS DE OPENAPI / SWAGGER
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "Avisos", description = "Operaciones para la emisión y gestión de notificaciones del sistema")
@RestController
@RequestMapping("/api/avisos")
@RequiredArgsConstructor
@Slf4j
public class AvisoController {

    private final AvisoService avisoService;

    @Operation(
        summary = "Enviar un nuevo aviso o notificación", 
        description = "Recibe los detalles de un aviso y los procesa en el sistema para notificar al usuario correspondiente."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Aviso enviado y registrado exitosamente"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Estructura de la petición inválida o faltan parámetros requeridos"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autorizado - Token JWT inválido o ausente"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping("/enviar")
    public ResponseEntity<ApiResponse<Void>> enviarAviso(@Valid @RequestBody AvisoRequestDTO req) {
        log.info("[CONTROLLER] Petición entrante para enviar aviso. Usuario: {}, Tipo: {}", 
                 req.getUsuario(), req.getTipo());
        
        avisoService.crearAviso(req);
        
        log.info("[CONTROLLER] Proceso de notificación finalizado con éxito para el usuario: {}", req.getUsuario());
        
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Aviso enviado exitosamente")
                        .build()
        );
    }
}