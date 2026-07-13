package com.example.menu.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.menu.dto.ProductosDTO;
import com.example.menu.model.Productos;
import com.example.menu.repository.ProductoRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static net.logstash.logback.argument.StructuredArguments.keyValue;
@Service
@Slf4j
@RequiredArgsConstructor
public class ProductosService {

    private final ProductoRepository repo;

    public Productos crear(ProductosDTO dto) {
        log.info("crear producto", keyValue("nombre", dto.getNombre()));
        
        Productos a = new Productos();
        a.setNombre(dto.getNombre());
        a.setDescripcion(dto.getDescripcion());
        a.setPrecio(dto.getPrecio());
        a.setStock(dto.getStock());
        a.setIsHot(dto.isHot()); 
        
        return repo.save(a); // Si tu repositorio se declara en ProductoRepository, esto compilará sin ningún error
    } 

    // Lista para ver los productos
    public List<Productos> listar() {
        log.info("listar productos");
        return repo.findAll();
    }

    // Obtener el café por ID
    public Productos obtener(Long id) {
        log.info("obtener producto", keyValue("id", id));
        return repo.findById(id).orElseThrow(() -> new EntityNotFoundException("Producto no encontrado"));
    }

    // Actualizar producto 
    public Productos actualizar(Long id, ProductosDTO dto) {
        log.info("actualizar producto", keyValue("id", id));
        Productos a = repo.findById(id).orElseThrow(() -> new EntityNotFoundException("Producto no encontrado"));
        
        a.setNombre(dto.getNombre());
        a.setDescripcion(dto.getDescripcion());
        a.setPrecio(dto.getPrecio());
        a.setStock(dto.getStock());
        a.setIsHot(dto.isHot()); 
        
        return repo.save(a);
    }

    // Eliminar producto
    public void eliminar(Long id){
        log.warn("eliminar producto ", keyValue("id", id));
        if (!repo.existsById(id)) {
            throw new EntityNotFoundException("No se puede eliminar: No existe");
        }
        repo.deleteById(id);
    }
}