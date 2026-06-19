package com.example.user.Controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.user.dto.ApiResponse;
import com.example.user.dto.LoginRequest;
import com.example.user.dto.RefreshRequest;
import com.example.user.dto.RegisterRequest;
import com.example.user.dto.UsuarioResponse;
import com.example.user.service.UsuarioService;

// 🚀 IMPORTS DE DOCUMENTACIÓN SWAGGER
import io.swagger.v3.oas.annotations.Operation; //
import io.swagger.v3.oas.annotations.responses.ApiResponses; //
import io.swagger.v3.oas.annotations.tags.Tag; //

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;



@Tag(name = "Autenticación", description = "registro, login y renovación de tokens JWT")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class UsuarioController {

    private final UsuarioService service;

    @Operation(summary = "Registrar un nuevo usuario", description = "Crea una nueva cuenta con rol base (USER).")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Usuario registrado exitosamente"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Datos de registro inválidos (ej. username ya existe, password débil)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })




    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UsuarioResponse>> register(@Valid @RequestBody RegisterRequest req) {
        log.info("POST /auth/register - usuario: {}", req.getUsername());

        UsuarioResponse res = service.register(req);

        // 🔹 IE 2.4.2: Convención REST semántica correcta (HttpStatus.CREATED = 201)
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<UsuarioResponse>builder()
                        .success(true)
                        .message("Usuario registrado exitosamente")
                        .data(res)
                        .build()
        );
    }
    @Operation(summary = "Iniciar sesión", description = "Valida las credenciales de acceso y retorna un accessToken firmado junto a un refreshToken.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Login exitoso"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Credenciales incorrectas o inválidas"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UsuarioResponse>> login(@Valid @RequestBody LoginRequest req) {
        log.info("POST /auth/login - usuario: {}", req.getUsername());
        UsuarioResponse res = service.login(req);

        return ResponseEntity.ok(
                ApiResponse.<UsuarioResponse>builder()
                        .success(true)
                        .message("Login exitoso")
                        .data(res)
                        .build()
        );
    }

    @Operation(summary = "Iniciar sesión", description = "Recibe un Refresh Token criptográfico válido y genera un nuevo Access Token sin requerir credenciales básicas.") //
        @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Token renovado exitosamente"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Refresh token expirado, inválido o no registrado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
        })

    @PostMapping("/refresh")
    // 🔹 IE 2.2.2: Añadido @Valid para que ataje campos nulos o en blanco
    public ResponseEntity<ApiResponse<UsuarioResponse>> refresh(@Valid @RequestBody RefreshRequest req) {
        log.info("POST /auth/refresh - Solicitud de renovación de fichas");
        
        UsuarioResponse res = service.refresh(req.getRefreshToken());

        return ResponseEntity.ok(
                ApiResponse.<UsuarioResponse>builder()
                        .success(true)
                        .message("Token renovado exitosamente")
                        .data(res)
                        .build()
        );
    }
}