package com.example.fidelidad.Controller;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.example.fidelidad.controller.FidelidadController;
import com.example.fidelidad.dto.FidelidadRequestDTO;
import com.example.fidelidad.service.FidelidadService;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class fidelidadControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private FidelidadService fidelidadService;

    @InjectMocks
    private FidelidadController fidelidadController;

    @BeforeEach
    void setUp() {
        // Inicialización manual aislada para evitar la carga de contextos pesados de Spring Security
        this.mockMvc = MockMvcBuilders.standaloneSetup(fidelidadController).build();
    }

    @Test
    void debeAcreditarPuntosExitosamente() throws Exception {
        // 1. GIVEN
        FidelidadRequestDTO req = new FidelidadRequestDTO();
        req.setUsuario("user-123");
        req.setMonto(new BigDecimal("3500"));

        // Comportamiento del mock del servicio
        doNothing().when(fidelidadService).agregarPuntos("user-123", new BigDecimal("3500"));

        // 2. WHEN & 3. THEN
        mockMvc.perform(post("/fidelidad/acreditar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Puntos acreditados exitosamente"));

        // Verificamos que se llamó al servicio con los parámetros correctos
        verify(fidelidadService).agregarPuntos("user-123", new BigDecimal("3500"));
    }

    @Test
    void debeObtenerBalanceConHateoas() throws Exception {
        // 1. GIVEN (ID de prueba)
        Long idUsuario = 99L;

        // 2. WHEN & 3. THEN
        mockMvc.perform(get("/fidelidad/{id}", idUsuario))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Balance obtenido con navegación hipermedia"))
                .andExpect(jsonPath("$.data.usuario").value("99"))
                // Valida que HATEOAS esté inyectando correctamente los links hipermedia autodescriptivos
                .andExpect(jsonPath("$.data._links.self.href").exists())
                .andExpect(jsonPath("$.data._links.update.href").exists());
    }
}