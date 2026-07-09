package com.example.resena.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.example.resena.dto.ResenaRequestDTO;
import com.example.resena.service.ResenaService;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ResenaControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private ResenaService resenaService;

    @InjectMocks
    private ResenaController resenaController;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(resenaController).build();
    }

    @Test
    void debePublicarResenaExitosamente() throws Exception {
        ResenaRequestDTO req = new ResenaRequestDTO();
        req.setIdProducto(1L);
        req.setUsuario("Carlos");
        req.setCalificacion(5);
        req.setComentario("Excelente servicio y café.");

        doNothing().when(resenaService).registrarResena(any(ResenaRequestDTO.class));

        mockMvc.perform(post("/api/resenas/crear")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Reseña publicada con éxito"));

        verify(resenaService).registrarResena(any(ResenaRequestDTO.class));
    }
}