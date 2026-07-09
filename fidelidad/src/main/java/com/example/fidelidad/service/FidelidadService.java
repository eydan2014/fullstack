package com.example.fidelidad.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.example.fidelidad.model.Fidelidad;
import com.example.fidelidad.repository.FidelidadRepository;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class FidelidadService {


        private final FidelidadRepository fidelidadRepo;
    private final RestTemplate restTemplate;

    private static final String URL_USER = "http://user/api/auth/{usuario}/existe";

    
    @Transactional
    public void agregarPuntos(String usuario, BigDecimal montoCompra) {
        validarUsuarioExiste(usuario);

        int puntosNuevos = montoCompra.multiply(new BigDecimal(1000)).intValue();
        log.info("[SERVICE] Calculando puntos para el usuario: {}. Monto de compra: ${}. Puntos a acreditar: {}",
        usuario, montoCompra, puntosNuevos);

        Fidelidad fidelidad = fidelidadRepo.findByUsuario(usuario)
            .orElse(new Fidelidad());
            if (fidelidad.getUsuario()==null){
                log.info("[SERVICE] No se encontró registro de fidelidad para el usuario: {}. Creando nuevo registro.", usuario);
                fidelidad.setUsuario(usuario);
                fidelidad.setPuntosTotales(0);
            } 

          fidelidad.setPuntosTotales(fidelidad.getPuntosTotales() + puntosNuevos);
          fidelidadRepo.save(fidelidad);  

          log.info("[SERVICE] Puntos acreditados exitosamente para el usuario: {}. Total de puntos: {}",
          usuario, fidelidad.getPuntosTotales());

        }

    // Consulta el balance real de puntos de un usuario
    public Fidelidad obtenerPuntos(String usuario) {
        return fidelidadRepo.findByUsuario(usuario)
            .orElseThrow(() -> new RuntimeException("No existe registro de fidelidad para el usuario: " + usuario));
    }

    private void validarUsuarioExiste(String usuario) {
        log.info("[INTER-SERVICIO] Consultando la existencia del usuario al microservicio 'user'...");
        try {
            Boolean existe = restTemplate.getForObject(URL_USER, Boolean.class, usuario);

            if (existe == null || !existe) {
                log.error("[INTER-SERVICIO] Validación fallida: El usuario {} no existe.", usuario);
                throw new RuntimeException("No se pueden acreditar puntos. El usuario no existe.");
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
