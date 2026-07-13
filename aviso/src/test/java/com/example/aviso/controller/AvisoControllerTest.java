package com.example.aviso.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.example.aviso.dto.AvisoRequestDTO;
import com.example.aviso.service.AvisoService;
import com.fasterxml.jackson.databind.ObjectMapper;

// 🚀 IMPORTS ESTÁTICOS LIMPIOS Y UTILIZADOS
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class AvisoControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private AvisoService avisoService;

    @InjectMocks
    private AvisoController avisoController;

    @BeforeEach
    void setUp() {
        // Inicialización manual aislada para evitar contextos pesados de Spring Security
        this.mockMvc = MockMvcBuilders.standaloneSetup(avisoController).build();
    }

    @Test
    void debeEnviarAvisoExitosamente() throws Exception {
        // 1. GIVEN (Preparar datos)
        AvisoRequestDTO req = new AvisoRequestDTO();
        req.setUsuario("user-123");
        req.setMensaje("Compra exitosa del producto ID: 1 por un total de $19.98");
        req.setTipo("PAGO");

        // 🚀 MOCK CORREGIDO: Se usa el import 'any' de forma directa y limpia
        doNothing().when(avisoService).crearAviso(any(AvisoRequestDTO.class));

        // 2. WHEN & 3. THEN (Ejecutar y verificar)
        mockMvc.perform(post("/api/avisos/enviar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Aviso enviado exitosamente"));

        // Verificamos de forma limpia que el controlador invocó al servicio
        verify(avisoService).crearAviso(any(AvisoRequestDTO.class));
    }
}