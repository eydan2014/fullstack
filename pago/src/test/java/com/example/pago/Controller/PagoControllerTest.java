package com.example.pago.Controller;

import java.math.BigDecimal;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;

import com.example.pago.controller.PagoController;
import com.example.pago.dto.PagoRequest;
import com.example.pago.model.pago;
import com.example.pago.security.JwtUtil;
import com.example.pago.service.PagoService;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class PagoControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private PagoService pagoService;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private PagoController pagoController;

    private final String tokenSimulado = "Bearer token.súper.secreto";
    private final String tokenLimpio = "token.súper.secreto";

    @BeforeEach
    void setUp() {
        // Inicialización manual aislada para omitir configuraciones pesadas del contexto web
        this.mockMvc = MockMvcBuilders.standaloneSetup(pagoController).build();
    }

    @Test
    void debeRealizarPagoExitosamente() throws Exception {
        // 1. GIVEN (Datos de entrada)
        PagoRequest req = new PagoRequest();
        req.setProductoId(5L);
        req.setCantidad(2);
        req.setMetodoPago("TARJETA");

        pago pagoCreado = new pago();
        pagoCreado.setId(1L);
        pagoCreado.setUsuarioId("user-test");
        pagoCreado.setProductoId(5L);
        pagoCreado.setCantidad(2);
        pagoCreado.setMontoTotal(new BigDecimal("5000"));

        // Comportamientos de los mocks
        when(jwtUtil.obtenerUsuario(tokenLimpio)).thenReturn("user-test");
        
        String urlProducto = "http://localhost:8083/api/productos/5/precio";
        when(restTemplate.getForObject(urlProducto, BigDecimal.class)).thenReturn(new BigDecimal("2500"));
        
        when(pagoService.procesopagar(any(PagoRequest.class), eq("user-test"), any(BigDecimal.class)))
                .thenReturn(pagoCreado);

        // Simular que la llamada interservicio a fidelidad responde correctamente
        String urlFidelidad = "http://localhost:8087/fidelidad/acreditar";
        when(restTemplate.postForEntity(eq(urlFidelidad), any(Map.class), eq(Void.class)))
                .thenReturn(ResponseEntity.ok().build());

        // 2. WHEN & 3. THEN
        mockMvc.perform(post("/api/pagos/realizar_pago")
                        .header("Authorization", tokenSimulado)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Pago realizado con éxito"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.usuarioId").value("user-test"))
                .andExpect(jsonPath("$.data.montoTotal").value(5000));

        // Verificaciones de interacciones
        verify(jwtUtil).obtenerUsuario(tokenLimpio);
        verify(restTemplate).getForObject(urlProducto, BigDecimal.class);
        verify(pagoService).procesopagar(any(PagoRequest.class), eq("user-test"), any(BigDecimal.class));
        verify(restTemplate).postForEntity(eq(urlFidelidad), any(Map.class), eq(Void.class));
    }

    @Test
    void debeRealizarPagoAunSiFallaFidelidad() throws Exception {
        // 1. GIVEN
        PagoRequest req = new PagoRequest();
        req.setProductoId(5L);
        req.setCantidad(1);

        pago pagoCreado = new pago();
        pagoCreado.setId(2L);

        when(jwtUtil.obtenerUsuario(tokenLimpio)).thenReturn("user-test");
        when(restTemplate.getForObject(any(String.class), eq(BigDecimal.class))).thenReturn(new BigDecimal("1000"));
        when(pagoService.procesopagar(any(PagoRequest.class), any(String.class), any(BigDecimal.class))).thenReturn(pagoCreado);
        
        // Forzar captura del bloque try-catch simulando excepción en Fidelidad
        when(restTemplate.postForEntity(any(String.class), any(Map.class), eq(Void.class)))
                .thenThrow(new RuntimeException("Servicio de fidelidad caído temporalmente"));

        // 2. WHEN & 3. THEN
        mockMvc.perform(post("/api/pagos/realizar_pago")
                        .header("Authorization", tokenSimulado)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated()) // Sigue retornando 201 debido a tu control del bloque try-catch log.error
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(2));
    }

    @Test
    void debeObtenerComprobanteConHateoas() throws Exception {
        // 1. GIVEN & WHEN & 3. THEN
        mockMvc.perform(get("/api/pagos/1")
                        .header("Authorization", tokenSimulado))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Detalle de pago obtenido correctamente"))
                .andExpect(jsonPath("$.data.productoId").value(1))
                // Valida que HATEOAS esté inyectando correctamente los enlaces auto-descriptivos hipermedia (_links)
                .andExpect(jsonPath("$.data._links.self.href").exists())
                .andExpect(jsonPath("$.data._links.realizar_pago.href").exists());
    }
}