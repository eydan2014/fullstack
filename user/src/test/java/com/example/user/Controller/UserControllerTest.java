package com.example.user.Controller; // 🚀 Corregido a minúsculas para coincidir con tu arquitectura principal

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.example.user.dto.LoginRequest;
import com.example.user.dto.RefreshRequest;
import com.example.user.dto.RegisterRequest;
import com.example.user.dto.UsuarioResponse;
import com.example.user.service.UsuarioService;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest { // 🚀 Nombre de clase unificado con el estándar del proyecto

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private UsuarioService service;

    @InjectMocks
    private UsuarioController UsuarioController;

    @BeforeEach
    void setUp() {
        // Inicialización manual aislada para saltar validaciones y filtros del contexto real de Spring Security
        this.mockMvc = MockMvcBuilders.standaloneSetup(UsuarioController).build();
    }

    @Test
    void debeRegistrarUsuarioExitosamente() throws Exception {
        // 1. GIVEN
        RegisterRequest req = new RegisterRequest();
        req.setUsername("nuevoUsuario");
        req.setPassword("securePass123");

        UsuarioResponse responseMock = new UsuarioResponse();
        responseMock.setUsername("nuevoUsuario");
        responseMock.setRole("USER");

        when(service.register(any(RegisterRequest.class))).thenReturn(responseMock);

        // 2. WHEN & 3. THEN
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated()) // Valida HTTP 201 CREATED
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Usuario registrado exitosamente"))
                .andExpect(jsonPath("$.data.username").value("nuevoUsuario"))
                .andExpect(jsonPath("$.data.role").value("USER"));

        verify(service).register(any(RegisterRequest.class));
    }

    @Test
    void debeIniciarSesionExitosamente() throws Exception {
        // 1. GIVEN
        LoginRequest req = new LoginRequest();
        req.setUsername("usuarioLogueado");
        req.setPassword("miClaveFuerte");

        UsuarioResponse responseMock = new UsuarioResponse();
        responseMock.setUsername("usuarioLogueado");
        responseMock.setAccessToken("jwt-access-token-example");
        responseMock.setRefreshToken("jwt-refresh-token-example");

        when(service.login(any(LoginRequest.class))).thenReturn(responseMock);

        // 2. WHEN & 3. THEN
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk()) // Valida HTTP 200 OK
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Login exitoso"))
                .andExpect(jsonPath("$.data.username").value("usuarioLogueado"))
                .andExpect(jsonPath("$.data.accessToken").value("jwt-access-token-example"))
                .andExpect(jsonPath("$.data.refreshToken").value("jwt-refresh-token-example"));

        verify(service).login(any(LoginRequest.class));
    }

    @Test
    void debeRenovarTokenExitosamente() throws Exception {
        // 1. GIVEN
        RefreshRequest req = new RefreshRequest();
        req.setRefreshToken("token-refresh-valido");

        UsuarioResponse responseMock = new UsuarioResponse();
        responseMock.setAccessToken("nuevo-jwt-access-token");
        responseMock.setRefreshToken("token-refresh-valido");

        when(service.refresh(anyString())).thenReturn(responseMock);

        // 2. WHEN & 3. THEN
        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk()) // Valida HTTP 200 OK
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Token renovado exitosamente"))
                .andExpect(jsonPath("$.data.accessToken").value("nuevo-jwt-access-token"));

        verify(service).refresh("token-refresh-valido");
    }
}