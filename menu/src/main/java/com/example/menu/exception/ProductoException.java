package com.example.menu.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.menu.dto.ApiResponse;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j 
public class ProductoException {

    // 🔴 VALIDACIONES (Maneja @NotBlank, @Min, etc.)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errores = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(e -> errores.put(e.getField(), e.getDefaultMessage()));

        log.warn("[EXCEPCIÓN VALIDACIÓN] Parámetros de producto inválidos: {}", errores);

        return ResponseEntity.badRequest().body(
                ApiResponse.<Object>builder()
                        .respuesta(false)
                        .mensaje("Validación fallida")
                        .data(errores) 
                        .build()
        );
    }

    // 🔎 404 - CORREGIDO: Exclusivo para recursos no encontrados
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleNotFound(EntityNotFoundException ex) {
        log.error("[EXCEPCIÓN RECURSO] Intento de acceso fallido: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiResponse.<Object>builder()
                        .respuesta(false)
                        .mensaje(ex.getMessage())
                        .build()
        );
    }

    // 💥 500 - Maneja cualquier otro error inesperado de lógica (Runtime, NullPointer, etc.)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGeneral(Exception ex) {
        log.error("[FALLO CRÍTICO INTERNO] Error no mapeado en módulo Productos: ", ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponse.<Object>builder()
                        .respuesta(false)
                        .mensaje("Error interno del servidor controlado.")
                        .build()
        );
    }
}
