package com.example.menu.Repository;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import com.example.menu.model.Productos;
import com.example.menu.repository.ProductoRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class ProductoRepositoryTest {

    @Autowired
    private ProductoRepository repository;

    @Test
    void debeGuardarProducto() {
        Productos producto1 = new Productos();
        producto1.setNombre("Gabriel García Márquez Coffee");
        producto1.setDescripcion("Mezcla colombiana premium");
        producto1.setPrecio(new BigDecimal("2990"));
        producto1.setStock(50);
        producto1.setIsHot(true);

        Productos guardado = repository.save(producto1);

        assertNotNull(guardado.getId());
        assertEquals("Gabriel García Márquez Coffee", guardado.getNombre());
        assertEquals(new BigDecimal("2990"), guardado.getPrecio());
    }

    @Test
    void debeBuscarProductoPorId() {
        Productos producto = new Productos();
        producto.setNombre("Mario Vargas Llosa Espresso");
        producto.setDescripcion("Café fuerte e intenso");
        producto.setPrecio(new BigDecimal("1500"));
        producto.setStock(30);
        producto.setIsHot(true);
        
        Productos guardado = repository.save(producto);

        Optional<Productos> resultado = repository.findById(guardado.getId());

        assertTrue(resultado.isPresent());
        assertEquals("Mario Vargas Llosa Espresso", resultado.get().getNombre());
        assertEquals(new BigDecimal("1500"), resultado.get().getPrecio());
    }

    @Test
    void debeListarProductos() {
        Productos p1 = new Productos();
        p1.setNombre("Jorge Luis Borges Moka");
        p1.setPrecio(new BigDecimal("3500"));
        p1.setStock(10);
        p1.setIsHot(false);
        repository.save(p1);

        Productos p2 = new Productos();
        p2.setNombre("Pablo Neruda Latte");
        p2.setPrecio(new BigDecimal("2500"));
        p2.setStock(12);
        p2.setIsHot(true);
        repository.save(p2);

        List<Productos> resultado = repository.findAll();

        assertFalse(resultado.isEmpty());
        assertTrue(resultado.size() >= 2);
    }

    @Test
    void debeEliminarProducto() {
        Productos producto = new Productos();
        producto.setNombre("Isabel Allende Frappé");
        producto.setPrecio(new BigDecimal("3800"));
        producto.setStock(15);
        producto.setIsHot(false);
        
        Productos guardado = repository.save(producto);

        repository.deleteById(guardado.getId());

        Optional<Productos> resultado = repository.findById(guardado.getId());
        assertFalse(resultado.isPresent());
    }
}