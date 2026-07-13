package com.example.resena.controller;

import com.example.resena.service.ResenaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.resena.dto.ResenaRequestDTO;
import com.example.resena.exception.ApiResponse;
import com.example.resena.model.Resena;

import java.util.List;

@RestController
@RequestMapping("/api/resenas")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Reseñas de Clientes", description = "Endpoints para dejar calificaciones y comentarios de productos")
public class ResenaController {

    private final ResenaService resenaService;

    @PostMapping("/crear")
    @Operation(summary = "Crear Reseña", description = "Permite a un cliente autenticado calificar un producto de 1 a 5 estrellas con un comentario opcional.")
    public ResponseEntity<ApiResponse<Void>> publicarResena(@Valid @RequestBody ResenaRequestDTO dto) {
        log.info("[CONTROLLER] Solicitud para crear reseña del usuario: {}", dto.getUsuario());
        resenaService.registrarResena(dto);

        // 🚀 CORREGIDO: Usamos ApiResponse en lugar de un Map genérico
        return ResponseEntity.status(HttpStatus.CREATED).body(
            ApiResponse.<Void>builder()
                .success(true)
                .message("Reseña publicada con éxito")
                .build()
        );
    }

    @GetMapping("/producto/{idProducto}")
    @Operation(summary = "Obtener Reseñas por Producto", description = "Devuelve el listado completo de opiniones de la comunidad para un producto específico.")
    public ResponseEntity<ApiResponse<List<Resena>>> listarPorProducto(@PathVariable Long idProducto) {
        log.info("[CONTROLLER] Solicitando listado de reseñas para producto ID: {}", idProducto);
        List<Resena> lista = resenaService.obtenerPorProducto(idProducto);
        
        // 🚀 CORREGIDO: Envolvemos la lista dentro del campo .data() de tu ApiResponse
        return ResponseEntity.ok(
            ApiResponse.<List<Resena>>builder()
                .success(true)
                .message("Listado de opiniones obtenido correctamente")
                .data(lista)
                .build()
        );
    }



    
}