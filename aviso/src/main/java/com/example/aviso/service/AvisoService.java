package com.example.aviso.service;

import com.example.aviso.dto.AvisoRequestDTO;
import com.example.aviso.model.AvisoModel;
import com.example.aviso.repository.AvisoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AvisoService {

    private final AvisoRepository avisoRepository;
    private final RestTemplate restTemplate;

    private static final String URL_USER = "http://user/api/auth/{usuario}/existe";

    @Transactional
    public void crearAviso(AvisoRequestDTO dto) {
        validarUsuarioExiste(dto.getUsuario());

        AvisoModel aviso = new AvisoModel();
        aviso.setUsuario(dto.getUsuario());
        aviso.setMensaje(dto.getMensaje());
        aviso.setTipo(dto.getTipo());
        avisoRepository.save(aviso);

        log.info("[SERVICE] Notificación registrada para el usuario: {}. Mensaje: {}",
                dto.getUsuario(), dto.getMensaje());
    }

    private void validarUsuarioExiste(String usuario) {
        log.info("[INTER-SERVICIO] Consultando la existencia del usuario al microservicio 'user'...");
        try {
            Boolean existe = restTemplate.getForObject(URL_USER, Boolean.class, usuario);

            if (existe == null || !existe) {
                log.error("[INTER-SERVICIO] Validación fallida: El usuario {} no existe.", usuario);
                throw new RuntimeException("No se puede crear el aviso. El usuario no existe.");
            }

            log.info("[INTER-SERVICIO] Usuario validado correctamente en el microservicio de usuarios.");
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            log.error("[INTER-SERVICIO] Error de conexión con el microservicio de usuarios: {}", e.getMessage());
            throw new RuntimeException("Error en la validación remota del usuario: " + e.getMessage());
        }
    }
}
