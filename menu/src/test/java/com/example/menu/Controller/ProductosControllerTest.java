package com.example.menu.Controller;

import com.example.menu.dto.ProductosDTO;
import com.example.menu.model.Productos;
import com.example.menu.security.JwtUtil;
import com.example.menu.service.ProductosService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest(ProductosController.class)
@org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
public class ProductosControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean // ◄ Mock del servicio inyectado de forma segura en la arquitectura del contexto
    private ProductosService service;

    @MockitoBean 
    private JwtUtil jwtUtil;

    @Test
    void debeListarProductos() throws Exception {
        // 1. GIVEN
        Productos p = new Productos();
        p.setId(1L);
        p.setNombre("Café Latte");
        p.setPrecio(new BigDecimal("2500"));
        p.setIsHot(true);

        when(service.listar()).thenReturn(List.of(p));

        // 2. WHEN & 3. THEN
        mockMvc.perform(get("/api/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.respuesta").value(true)) 
                .andExpect(jsonPath("$.mensaje").value("Listado obtenido"))
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].nombre").value("Café Latte"));
    }

    @Test
    void debeObtenerProductoPorId() throws Exception {
        // 1. GIVEN
        Productos p = new Productos();
        p.setId(1L);
        p.setNombre("Espresso");
        p.setPrecio(new BigDecimal("1500"));

        when(service.obtener(1L)).thenReturn(p);

        // 2. WHEN & 3. THEN
        mockMvc.perform(get("/api/productos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.respuesta").value(true))
                .andExpect(jsonPath("$.mensaje").value("Producto obtenido")) // 🚀 Sincronizado en español con tu ApiResponse
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.nombre").value("Espresso"));
    }

    @Test
    void debeCrearProducto() throws Exception {
        
        ProductosDTO dto = new ProductosDTO();
        dto.setNombre("Capuccino");
        dto.setDescripcion("Café con espuma");
        dto.setPrecio(new BigDecimal("2800"));
        dto.setHot(true);
        dto.setStock(10);

        Productos creado = new Productos();
        creado.setId(1L);
        creado.setNombre("Capuccino");

        when(service.crear(any(ProductosDTO.class))).thenReturn(creado);


        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.respuesta").value(true))
                .andExpect(jsonPath("$.mensaje").value("Producto creado"));
    }

    @Test
    void debeActualizarProducto() throws Exception {
        // 1. GIVEN: Objeto DTO completo con restricciones @NotBlank satisfechas para saltar el validador del controlador
        ProductosDTO dto = new ProductosDTO();
        dto.setNombre("Café Premium");
        dto.setDescripcion("Café seleccionado de alta calidad"); 
        dto.setPrecio(new BigDecimal("3500"));
        dto.setHot(false);
        dto.setStock(20);

        Productos actualizado = new Productos();
        actualizado.setId(1L);
        actualizado.setNombre("Café Premium");

        when(service.actualizar(eq(1L), any(ProductosDTO.class))).thenReturn(actualizado);

        // 2. WHEN & 3. THEN
        mockMvc.perform(put("/api/productos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk()) // 🚀 Retorna exitosamente HTTP 200 OK
                .andExpect(jsonPath("$.respuesta").value(true))
                .andExpect(jsonPath("$.mensaje").value("Producto actualizado"));
    }

    @Test
    void debeEliminarProducto() throws Exception {
        // 1. GIVEN
        doNothing().when(service).eliminar(1L);

        // 2. WHEN & 3. THEN
        mockMvc.perform(delete("/api/productos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.respuesta").value(true))
                .andExpect(jsonPath("$.mensaje").value("Producto eliminado"));
    }

    @Test
    void debeObtenerPrecioDelProducto() throws Exception {
        // 1. GIVEN
        Productos p = new Productos();
        p.setId(1L);
        p.setPrecio(new BigDecimal("3500"));

        when(service.obtener(1L)).thenReturn(p);

        // 2. WHEN & 3. THEN
        mockMvc.perform(get("/api/productos/1/precio"))
                .andExpect(status().isOk())
                .andExpect(content().string("3500"));
    }
}