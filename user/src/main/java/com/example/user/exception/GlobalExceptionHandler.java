package com.example.user.exception;

import com.example.user.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j; // 🔹 1. IMPORTANTE: Revisa que tengas esta importación
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j // 🔹 2. SOLUCIÓN CORRECCIÓN: Esta anotación faltaba y por eso te daba error en las líneas 24 y 33
public class GlobalExceptionHandler {

    // Captura errores de validación de DTOs (@NotBlank)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(err -> errors.put(err.getField(), err.getDefaultMessage()));
        
        log.warn("[VALIDACIÓN] Datos inválidos interceptados: {}", errors);
        
        return ResponseEntity.badRequest().body(
            ApiResponse.<Object>builder()
                .success(false)
                .message("Campos inválidos")
                .error(errors)
                .build()
        );
    }

    // Captura errores de lógica/credenciales (IllegalArgumentException)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Object>> handleBusiness(IllegalArgumentException ex) {
        log.error("[NEGOCIO ERROR] Restricción violada: {}", ex.getMessage());
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
            ApiResponse.<Object>builder()
                .success(false)
                .message(ex.getMessage())
                .build()
        );
    }
}