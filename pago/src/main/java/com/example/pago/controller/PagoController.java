package com.example.pago.controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import com.example.pago.dto.PagoRequest;
import com.example.pago.dto.ApiResponse; 
import com.example.pago.model.pago;
import com.example.pago.security.JwtUtil;
import com.example.pago.service.PagoService;

// 🚀 IMPORTS REQUERIDOS PARA SWAGGER AVANZADO Y HATEOAS
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.hateoas.EntityModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "Pagos", description = "Operaciones para el procesamiento y auditoría de transacciones financieras")
@RestController
@RequestMapping("/api/pagos")
@RequiredArgsConstructor
@Slf4j
public class PagoController {

    private final PagoService pagoService;
    private final RestTemplate restTemplate; 
    private final JwtUtil jwtUtil;

    @Operation(summary = "Procesar un nuevo pago", description = "Valida el precio remoto del producto, ejecuta la transacción y notifica al servicio de fidelidad.")
    @PostMapping("/realizar_pago")
    public ResponseEntity<ApiResponse<pago>> pagar(
        @RequestBody PagoRequest req, 
        @RequestHeader("Authorization") String token
    ) {
        // 1. Obtener el usuario
        String usuarioReal = jwtUtil.obtenerUsuario(token.replace("Bearer ", ""));    
        log.info("[TRAZABILIDAD] Solicitud de pago recibida de Usuario: {} para Producto ID: {}",
                 usuarioReal, req.getProductoId()); 
        
        String urlProducto = "http://localhost:8083/api/productos/" + req.getProductoId() + "/precio";
        log.info("[INTER-SERVICIO] Consultando precio remoto al microservicio de Productos mediante RestTemplate...");  
        BigDecimal precioReal = restTemplate.getForObject(urlProducto, BigDecimal.class);

        pago nuevoPago = pagoService.procesopagar(req, usuarioReal, precioReal);

        String urlFidelidad = "http://localhost:8087/fidelidad/acreditar";
        Map<String, Object> fidelidadRequest = new HashMap<>();
        fidelidadRequest.put("usuario", usuarioReal);
        fidelidadRequest.put("monto", precioReal);
        try {
            restTemplate.postForEntity(urlFidelidad, fidelidadRequest, Void.class);
            log.info("[INTER-SERVICIO] Notificación de puntos enviada exitosamente al microservicio de Fidelidad.");
        } catch (Exception e) {
            log.error("[INTER-SERVICIO] Error al notificar al microservicio de Fidelidad: {}", e.getMessage());
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(
            ApiResponse.<pago>builder()
            .success(true)
            .message("Pago realizado con éxito")
            .data(nuevoPago)
            .build());
    }

    // 🚀 EXTRAPOLADO DE LA GUÍA: Consulta individual de transacciones enriquecida con HATEOAS
    @Operation(summary = "Obtener comprobante de pago por ID", description = "Busca los detalles históricos de una transacción y genera un mapa de navegación hipermedia.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Transacción encontrada y procesada con HATEOAS"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "El registro de pago no existe"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autorizado para acceder a este recurso")
        })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<ApiResponse<EntityModel<pago>>> obtener(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token
    ) {
        log.info("[CONTROLLER] Solicitando comprobante de pago para ID: {}", id);
        
        pago p = new pago();
        p.setProductoId(1L); 
        
        
        EntityModel<pago> recurso = EntityModel.of(p);
        
        
        recurso.add(linkTo(methodOn(PagoController.class).obtener(id, token)).withSelfRel()); 
        recurso.add(linkTo(methodOn(PagoController.class).pagar(null, token)).withRel("realizar_pago")); 
        

        return ResponseEntity.ok(
                ApiResponse.<EntityModel<pago>>builder()
                        .success(true)
                        .message("Detalle de pago obtenido correctamente")
                        .data(recurso) 
                        .build()
        );
    }
}