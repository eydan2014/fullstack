package com.example.menu.Controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.hateoas.EntityModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import com.example.menu.dto.ApiResponse;
import com.example.menu.dto.ProductosDTO;
import com.example.menu.model.Productos;
import com.example.menu.service.ProductosService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "Productos", description = "Controlador para la gestion del catalogo de cafe (Bebidas frias/calientes)")
@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
@Slf4j
public class ProductosController {

    private final ProductosService service;

    @Operation(summary = "Crear un nuevo producto", description = 
    "Permite registrar un nuevo item en el catalogo de cafeteria. Requiere rol ADMIN.")
    @PostMapping
    public ResponseEntity<ApiResponse<Productos>> crear(@Valid @RequestBody ProductosDTO dto) {
        Productos p = service.crear(dto);
        return ResponseEntity.status(201).body(
                ApiResponse.<Productos>builder()
                        .respuesta(true)
                        .mensaje("Producto creado")
                        .data(p)
                        .build()
        );
    }
    @Operation(summary = "Listar productos", description = "Obtiene la lista de todos los productos en el catalogo.")
    @GetMapping
    public ResponseEntity<ApiResponse<List<Productos>>> listar() {
        return ResponseEntity.ok(
                ApiResponse.<List<Productos>>builder()
                        .respuesta(true)
                        .mensaje("Listado obtenido")
                        .data(service.listar())
                        .build()
        );
    }

    @Operation(summary = "Obtener un producto por ID", description = 
    "Retorna los detalles de un producto especifico segun su ID.")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EntityModel<Productos>>> obtener(@PathVariable Long id) {
        Productos p = service.obtener(id);
        EntityModel<Productos> recurso = EntityModel.of(p);

        recurso.add(linkTo(methodOn(ProductosController.class).obtener(id)).withSelfRel());
        recurso.add(linkTo(methodOn(ProductosController.class).listar()).withRel("all")); 
        recurso.add(linkTo(methodOn(ProductosController.class).actualizar(id, null)).withRel("update")); 
        recurso.add(linkTo(methodOn(ProductosController.class).eliminar(id)).withRel("delete")); 

        return ResponseEntity.ok(
                ApiResponse.<EntityModel<Productos>>builder()
                        .respuesta(true)
                        .mensaje("Producto obtenido")
                        .data(recurso)
                        .build()
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Productos>> actualizar(@PathVariable Long id, @Valid @RequestBody ProductosDTO dto) {
        Productos p = service.actualizar(id, dto);        
        return ResponseEntity.ok(
                ApiResponse.<Productos>builder()
                        .respuesta(true)
                        .mensaje("Producto actualizado")
                        .data(p)
                        .build()
        );   
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.ok(
                ApiResponse.<Object>builder()
                        .respuesta(true)
                        .mensaje("Producto eliminado")
                        .build()
        );
    }

    @GetMapping("/{id}/precio")
    public BigDecimal obtenerPrecio(@PathVariable Long id) {
        Productos p = service.obtener(id);
        return p.getPrecio();
    }
@Operation(summary = "Verificar si un producto existe", description =
 "Endpoint inter-servicio utilizado por otros modulos para comprobar la validez de un ID.")
    @GetMapping("/{id}/existe")
    public boolean existeProducto(@PathVariable Long id) {
        log.info("[INTER-SERVICIO] Recibida validación remota de existencia para Producto ID: {}", id);
        try {
            service.obtener(id);
            return true;
        } catch (jakarta.persistence.EntityNotFoundException e) {
            return false;
        }
    }

}
