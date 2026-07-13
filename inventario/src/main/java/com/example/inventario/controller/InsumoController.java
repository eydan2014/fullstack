package com.example.inventario.controller;

import com.example.inventario.dto.InsumoRequestDTO;
import com.example.inventario.service.InsumoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/inventario")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Control de Inventario", description = 
"Endpoints para la gestion y auditoria del stock de insumos de la cafetería")
public class InsumoController {

    private final InsumoService insumoService;

    @PostMapping("/actualizar")
    @Operation(
        summary = "Actualizar o Registrar Insumo", 
        description = "Permite registrar un nuevo insumo (vasos, leche, etc.) o actualizar el stock existente si el insumo ya se encuentra en la base de datos."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "El inventario se actualizo exitosamente en la base de datos"),
        @ApiResponse(responseCode = "400", description = "Datos de peticion invalidos o formato JSON incorrecto"),
        @ApiResponse(responseCode = "401", description = "No autorizado. Token JWT faltante, invalido o expirado")
    })
    public ResponseEntity<Map<String, Object>> actualizarStock(@Valid @RequestBody InsumoRequestDTO dto) {
        log.info("[CONTROLLER] Peticion recibida para actualizar inventario del insumo: {}", dto.getNombre());
        
        insumoService.registrarOActualizarInsumo(dto);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Inventario actualizado exitosamente");
        
        return ResponseEntity.ok(response);
    }
}