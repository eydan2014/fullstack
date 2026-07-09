package com.example.resena.service;

import com.example.resena.dto.ResenaRequestDTO;
import com.example.resena.model.Resena;
import com.example.resena.repository.ResenaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResenaService {

    private final ResenaRepository resenaRepository;
    private final RestTemplate restTemplate; 

    @Transactional
    public void registrarResena(ResenaRequestDTO dto) {
        log.info("[TRAZABILIDAD] Procesando nueva reseña del usuario: {} para el producto ID: {}", 
                 dto.getUsuario(), dto.getIdProducto());


        String urlMenu = "http://localhost:8083/api/productos/" + dto.getIdProducto() + "/existe";
        
        log.info("[INTER-SERVICIO] Consultando la existencia del producto al microservicio 'menu'...");
        
        try {
     
            Boolean productoExiste = restTemplate.getForObject(urlMenu, Boolean.class);

            if (productoExiste == null || !productoExiste) {
                log.error("[INTER-SERVICIO] Validación fallida: El producto ID {} no existe en el menú.", dto.getIdProducto());
                throw new RuntimeException("No se puede publicar la reseña. El producto no existe en el catálogo.");
            }
            
            log.info("[INTER-SERVICIO] Producto validado correctamente en el microservicio de menú.");

        } catch (Exception e) {
            log.error("[INTER-SERVICIO] Error de conexión con el microservicio de menú: {}", e.getMessage());
            throw new RuntimeException("Error en la validación remota del producto: " + e.getMessage());
        }

    
        Resena resena = new Resena();
        resena.setIdProducto(dto.getIdProducto());
        resena.setUsuario(dto.getUsuario());
        resena.setCalificacion(dto.getCalificacion());
        resena.setComentario(dto.getComentario());
        resena.setFechaCreacion(LocalDateTime.now());

        resenaRepository.save(resena);
        log.info("[RESEÑAS] Opinión guardada con éxito en la base de datos.");
    }

    @Transactional(readOnly = true)
    public java.util.List<Resena> obtenerPorProducto(Long idProducto) {
        return resenaRepository.findByIdProducto(idProducto);
    }
}