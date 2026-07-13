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

        private static final String URL_MENU = "http://menu/api/productos/{idProducto}/existe";
        private static final String URL_USER = "http://user/api/auth/{usuario}/existe";

    @Transactional
    public void registrarResena(ResenaRequestDTO dto) {
        log.info("[TRAZABILIDAD] Procesando nueva reseña del usuario: {} para el producto ID: {}",
                 dto.getUsuario(), dto.getIdProducto());

        validarUsuarioExiste(dto.getUsuario());
        validarProductoExiste(dto.getIdProducto());

        Resena resena = new Resena();
        resena.setIdProducto(dto.getIdProducto());
        resena.setUsuario(dto.getUsuario());
        resena.setCalificacion(dto.getCalificacion());
        resena.setComentario(dto.getComentario());
        resena.setFechaCreacion(LocalDateTime.now());

        resenaRepository.save(resena);
        log.info("[RESEÑAS] Opinión guardada con éxito en la base de datos.");
    }

    private void validarProductoExiste(Long idProducto) {
        log.info("[INTER-SERVICIO] Consultando la existencia del producto al microservicio 'menu'...");
        try {
            Boolean existe = restTemplate.getForObject(URL_MENU, Boolean.class, idProducto);

            if (existe == null || !existe) {
                log.error("[INTER-SERVICIO] Validación fallida: El producto ID {} no existe en el menú.", idProducto);
                throw new RuntimeException("No se puede publicar la reseña. El producto no existe en el catálogo.");
            }

            log.info("[INTER-SERVICIO] Producto validado correctamente en el microservicio de menú.");
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            log.error("[INTER-SERVICIO] Error de conexión con el microservicio de menú: {}", e.getMessage());
            throw new RuntimeException("Error en la validación remota del producto: " + e.getMessage());
        }
    }

    private void validarUsuarioExiste(String usuario) {
        log.info("[INTER-SERVICIO] Consultando la existencia del usuario al microservicio 'user'...");
        try {
            Boolean existe = restTemplate.getForObject(URL_USER, Boolean.class, usuario);

            if (existe == null || !existe) {
                log.error("[INTER-SERVICIO] Validación fallida: El usuario {} no existe.", usuario);
                throw new RuntimeException("No se puede publicar la reseña. El usuario no existe.");
            }

            log.info("[INTER-SERVICIO] Usuario validado correctamente en el microservicio de usuarios.");
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            log.error("[INTER-SERVICIO] Error de conexión con el microservicio de usuarios: {}", e.getMessage());
            throw new RuntimeException("Error en la validación remota del usuario: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public java.util.List<Resena> obtenerPorProducto(Long idProducto) {
        return resenaRepository.findByIdProducto(idProducto);
    }
}