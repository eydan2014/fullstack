package com.example.fidelidad.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.security.access.AccessDeniedException; 

import jakarta.persistence.EntityNotFoundException;
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 🔴 400 - VALIDACIÓN
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errores = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errores.put(error.getField(), error.getDefaultMessage());
        }

        return ResponseEntity.badRequest().body(
                ApiResponse.<Object>builder() 
                        .success(false)
                        .message("Validación fallida")
                        .error(errores)
                        .build()
        );
    }

    // 🔐 403 - ACCESO DENEGADO
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Object>> handle403(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                ApiResponse.<Object>builder() 
                        .success(false)
                        .message("Acceso denegado")
                        .build()
        );
    }

    // 🔎 401 - CREDENCIALES INVÁLIDAS
    @ExceptionHandler(org.springframework.security.authentication.BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Object>> handleBadCredentials(org.springframework.security.authentication.BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                ApiResponse.<Object>builder() 
                        .success(false)
                        .message("Credenciales inválidas")
                        .build()
        );
    }

    // 🔎 404 - RECURSO NO ENCONTRADO (CORREGIDO: Ya no es genérico para todo Runtime)
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleNotFound(EntityNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiResponse.<Object>builder() 
                        .success(false)
                        .message(ex.getMessage())
                        .build()
        );
    }

    // 💥 500 - ERROR GENERAL INTERNO
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGeneral(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponse.<Object>builder() // ◄ CORREGIDO: Diamante genérico asignado
                        .success(false)
                        .message("Error interno controlado del servidor")
                        .build()
        );
    }
}