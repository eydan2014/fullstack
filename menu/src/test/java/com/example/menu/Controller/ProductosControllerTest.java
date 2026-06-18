package com.example.menu.Controller;



import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean; // ◄ Obligatorio para Spring Boot 3.4 / 4.0+
import org.springframework.test.web.servlet.MockMvc;

import com.example.menu.dto.ProductosDTO;
import com.example.menu.model.Productos;
import com.example.menu.security.JwtUtil;
import com.example.menu.service.ProductosService;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductosController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ProductosControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean // ◄ CORREGIDO: Adaptado para la compatibilidad nativa con Spring Boot 4.0
    private ProductosService service;

    @MockitoBean // ◄ CORREGIDO: Ambos unificados con la nueva arquitectura de mocks
    private JwtUtil jwtUtil;

    @Test
    void debeListarProductos() throws Exception {
        Productos p = new Productos();
        p.setId(1L);
        p.setNombre("Café Latte");
        p.setPrecio(new BigDecimal("2500"));
        p.setIsHot(true);

        when(service.listar()).thenReturn(List.of(p));

        mockMvc.perform(get("/api/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.respuesta").value(true)) // Validamos directo sin métodos auxiliares abstractos
                .andExpect(jsonPath("$.mensaje").value("Listado obtenido"))
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].nombre").value("Café Latte"));
    }

    @Test
    void debeObtenerProductoPorId() throws Exception {
        Productos p = new Productos();
        p.setId(1L);
        p.setNombre("Espresso");
        p.setPrecio(new BigDecimal("1500"));
        p.setIsHot(true);

        when(service.obtener(1L)).thenReturn(p);

        mockMvc.perform(get("/api/productos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.respuesta").value(true))
                .andExpect(jsonPath("$.mensaje").value("Producto obtenido"))
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
        ProductosDTO dto = new ProductosDTO();
        dto.setNombre("Café Premium");
        dto.setPrecio(new BigDecimal("3500"));
        dto.setHot(false);

        Productos actualizado = new Productos();
        actualizado.setId(1L);
        actualizado.setNombre("Café Premium");

        when(service.actualizar(eq(1L), any(ProductosDTO.class))).thenReturn(actualizado);

        mockMvc.perform(put("/api/productos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.respuesta").value(true))
                .andExpect(jsonPath("$.mensaje").value("Producto actualizado"));
    }

    @Test
    void debeEliminarProducto() throws Exception {
        doNothing().when(service).eliminar(1L);

        mockMvc.perform(delete("/api/productos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.respuesta").value(true))
                .andExpect(jsonPath("$.mensaje").value("Producto eliminado"));
    }

    @Test
    void debeObtenerPrecioDelProducto() throws Exception {
        Productos p = new Productos();
        p.setId(1L);
        p.setPrecio(new BigDecimal("3500"));

        when(service.obtener(1L)).thenReturn(p);

        mockMvc.perform(get("/api/productos/1/precio"))
                .andExpect(status().isOk())
                .andExpect(content().string("3500"));
    }
}