package com.example.pago.controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import com.example.pago.dto.PagoRequest;
import com.example.pago.dto.ApiResponse; 
import com.example.pago.model.pago;
import com.example.pago.service.PagoService;

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

    @Operation(summary = "Procesar un nuevo pago", description = "Valida el precio remoto del producto, ejecuta la transacción, y notifica a fidelidad y avisos.")
    @PostMapping("/realizar_pago")
    public ResponseEntity<ApiResponse<pago>> pagar(
        @RequestBody PagoRequest req, 
        @RequestHeader(value = "Authorization", required = false) String token
    ) {
        // 🔓 Sin JwtUtil: el usuario viene directo en el body de la petición
        // (antes se extraía decodificando el token JWT).
        String usuarioReal = req.getUsuario();
        log.info("[TRAZABILIDAD] Solicitud de pago recibida de Usuario: {} para Producto ID: {}",
                 usuarioReal, req.getProductoId()); 
        //menu
        String urlProducto = "http://menu/api/productos/" + req.getProductoId() + "/precio";
        log.info("[INTER-SERVICIO] Consultando precio remoto al microservicio de Productos mediante RestTemplate...");  
        BigDecimal precioReal = restTemplate.getForObject(urlProducto, BigDecimal.class);

        pago nuevoPago = pagoService.procesopagar(req, usuarioReal, precioReal);

        HttpHeaders headers = new HttpHeaders();
        if (token != null) {
            headers.set("Authorization", token);
        }
        HttpEntity<Map<String, Object>> requestConToken;

        // FIDELIDAD
        String urlFidelidad = "http://fidelidad/fidelidad/acreditar";
        Map<String, Object> fidelidadRequest = new HashMap<>();
        fidelidadRequest.put("usuario", usuarioReal);
        fidelidadRequest.put("monto", precioReal);
        try {
            requestConToken = new HttpEntity<>(fidelidadRequest, headers);
            restTemplate.postForEntity(urlFidelidad, requestConToken, Void.class);
            log.info("[INTER-SERVICIO] Notificación de puntos enviada exitosamente al microservicio de Fidelidad.");
        } catch (Exception e) {
            log.error("[INTER-SERVICIO] Error al notificar al microservicio de Fidelidad: {}", e.getMessage());
        }

        // AVISO 
        String urlAviso = "http://aviso/api/avisos/enviar";
        Map<String, Object> avisoRequest = new HashMap<>();
        avisoRequest.put("usuario", usuarioReal);
        avisoRequest.put("mensaje", "Compra exitosa del producto ID: " + req.getProductoId() + " por un total de $" + nuevoPago.getMontoTotal());
        avisoRequest.put("tipo", "PAGO");
        try {
            requestConToken = new HttpEntity<>(avisoRequest, headers);
            restTemplate.postForEntity(urlAviso, requestConToken, Void.class);
            log.info("[INTER-SERVICIO] Notificación enviada exitosamente al microservicio de Avisos.");
        } catch (Exception e) {
            log.error("[INTER-SERVICIO] Error al notificar al microservicio de Avisos: {}", e.getMessage());
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(
            ApiResponse.<pago>builder()
            .success(true)
            .message("Pago realizado con éxito")
            .data(nuevoPago)
            .build());
    }

    @Operation(summary = "Obtener comprobante de pago por ID", description = "Busca los detalles históricos de una transacción y genera un mapa de navegación hipermedia.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Transacción encontrada y procesada con HATEOAS"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "El registro de pago no existe"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autorizado para acceder a este recurso")
        })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EntityModel<pago>>> obtener(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String token
    ) {
        log.info("[CONTROLLER] Solicitando comprobante de pago para ID: {}", id);
        
        
        pago p = pagoService.obtenerPagoPorId(id);
        
        EntityModel<pago> recurso = EntityModel.of(p);
        
        recurso.add(linkTo(methodOn(PagoController.class).obtener(id, token)).withSelfRel()); 
        recurso.add(linkTo(methodOn(PagoController.class).pagar(new PagoRequest(), token)).withRel("realizar_pago")); 

        return ResponseEntity.ok(
                ApiResponse.<EntityModel<pago>>builder()
                        .success(true)
                        .message("Detalle de pago obtenido correctamente")
                        .data(recurso) 
                        .build()
        );
    }
}