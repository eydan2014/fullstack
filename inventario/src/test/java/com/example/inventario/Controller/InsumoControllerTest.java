package com.example.inventario.Controller;

import com.example.inventario.controller.InsumoController;
import com.example.inventario.dto.InsumoRequestDTO;
import com.example.inventario.service.InsumoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class InsumoControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private InsumoService insumoService;

    @InjectMocks
    private InsumoController insumoController;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(insumoController).build();
    }

    @Test
    void debeActualizarInventarioExitosamente() throws Exception {
        // 1. GIVEN
        InsumoRequestDTO dto = new InsumoRequestDTO();
        dto.setNombre("Vasos 12oz");
        dto.setStock(500);

        doNothing().when(insumoService).registrarOActualizarInsumo(any(InsumoRequestDTO.class));

        // 2. WHEN & 3. THEN
        mockMvc.perform(post("/api/inventario/actualizar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Inventario actualizado exitosamente"));

        verify(insumoService).registrarOActualizarInsumo(any(InsumoRequestDTO.class));
    }
}