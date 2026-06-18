package com.example.menu.ServiceTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.menu.dto.ProductosDTO;
import com.example.menu.model.Productos;
import com.example.menu.repository.ProductoRepository;
import com.example.menu.service.ProductosService;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
public class ProductosServiceTest {

    @Mock 
    private ProductoRepository repo;

    @InjectMocks 
    private ProductosService service;

    // Test 1 - Obtener producto por ID
    @Test
    void obtenerRespuestaProducto(){
        Productos producto = new Productos();
        producto.setId(1L);
        producto.setNombre("Café Viejo");
        producto.setPrecio(new BigDecimal("1000"));
        
        producto.setIsHot(true); 
    
        when(repo.findById(1L)).thenReturn(Optional.of(producto)); 
        Productos resultado = service.obtener(1L);
    
        assertNotNull(resultado); 
        assertEquals(1L, resultado.getId()); 
        assertEquals("Café Viejo", resultado.getNombre()); 
        verify(repo).findById(1L);
    } 

    // Test 2 - Obtener producto por ID que no existe
    @Test
    void deberiaLanzarExcepcionCuandoProductoNoExiste(){
        when(repo.findById(99L)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(
                EntityNotFoundException.class,
                () -> service.obtener(99L));
    
        assertEquals("Producto no encontrado", ex.getMessage());
        verify(repo).findById(99L);
    }

    // Test 3 - Listar productos
    @Test
    void deberiaRetornarListaProductos() {
        Productos producto = new Productos();
        producto.setId(1L);
        producto.setNombre("experso");
        producto.setPrecio(new BigDecimal("3500"));
        producto.setStock(10);
        producto.setIsHot(false); 

        when(repo.findAll()).thenReturn(List.of(producto));
        List<Productos> resultado = service.listar();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("experso", resultado.get(0).getNombre());
        verify(repo).findAll();
    }

    // Test 4 - Crear productos
    @Test 
    void deberiaCrearProducto() {
        ProductosDTO dto = new ProductosDTO();
        dto.setNombre("Café Americano");
        dto.setDescripcion("Café filtrado, ideal para los amantes del café suave.");
        dto.setPrecio(new BigDecimal("3000"));
        dto.setStock(20);
        dto.setHot(true); 
        
        when(repo.save(any(Productos.class))).thenAnswer(invocation -> {
            Productos productoPasadoAlSave = invocation.getArgument(0);
            productoPasadoAlSave.setId(1L); 
            return productoPasadoAlSave; 
        });
        
        Productos resultado = service.crear(dto);
        
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Café Americano", resultado.getNombre());
        assertEquals("Café filtrado, ideal para los amantes del café suave.", resultado.getDescripcion());
        assertEquals(new BigDecimal("3000"), resultado.getPrecio());
        assertEquals(20, resultado.getStock());
        assertTrue(resultado.getIsHot(), "El producto debería ser caliente");
        
        verify(repo).save(any(Productos.class)); 
    }

    // Test 5 - Actualizar productos correctamente
    @Test
    void deberiaActualizarProductoCorrectamente() {
        Productos existente = new Productos();
        existente.setId(1L);
        existente.setNombre("Café Viejo");
        existente.setPrecio(new BigDecimal("1000"));
        existente.setStock(2);
        existente.setIsHot(true);
        
        ProductosDTO dto = new ProductosDTO();
        dto.setNombre("Café Premium");
        dto.setPrecio(new BigDecimal("3500"));
        dto.setStock(5);
        dto.setHot(false); 

        when(repo.findById(1L)).thenReturn(Optional.of(existente)); 
        when(repo.save(any(Productos.class))).thenAnswer(invocation -> invocation.getArgument(0)); 

        Productos resultado = service.actualizar(1L, dto);

        assertEquals(1L, resultado.getId()); 
        assertEquals("Café Premium", resultado.getNombre()); 
        assertFalse(resultado.getIsHot()); 
        verify(repo).findById(1L); 
        verify(repo).save(existente); 
    }

    // Test 6 - Eliminar producto correctamente
    @Test
    void deberiaEliminarProductoCorrectamente() {
        when(repo.existsById(1L)).thenReturn(true);
        doNothing().when(repo).deleteById(1L);

        service.eliminar(1L);
        
        verify(repo).existsById(1L);
        verify(repo).deleteById(1L);
    }

    // 🚀 : Test 7 - Eliminar producto que no existe (Evita huecos de cobertura en JaCoCo)
    @Test
    void deberiaLanzarExcepcionAlEliminarInexistente() {
        when(repo.existsById(99L)).thenReturn(false);

        EntityNotFoundException ex = assertThrows(
                EntityNotFoundException.class,
                () -> service.eliminar(99L)
        );

        assertEquals("No se puede eliminar: No existe", ex.getMessage());
        verify(repo).existsById(99L);
        verify(repo, never()).deleteById(99L);
    }

    // Test 8 - Actualizar producto inexistente
    @Test
    void deberiaLanzarExcepcionAlActualizarProductoInexistente() {
        ProductosDTO dto = new ProductosDTO();
        dto.setNombre("Producto fantasma");
        dto.setPrecio(new BigDecimal("9999"));
        dto.setHot(true); 

        when(repo.findById(99L)).thenReturn(Optional.empty()); 

        assertThrows(
                EntityNotFoundException.class,
                () -> service.actualizar(99L, dto)
        ); 

        verify(repo).findById(99L); 
        verify(repo, never()).save(any(Productos.class)); 
    }
}
