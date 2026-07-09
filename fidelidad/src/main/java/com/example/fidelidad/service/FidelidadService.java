package com.example.fidelidad.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.example.fidelidad.model.Fidelidad;
import com.example.fidelidad.repository.FidelidadRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class FidelidadService {


    private final FidelidadRepository  fidelidadRepo;
    //sistema de puntos por compra del usuario
    public void  agregarPuntos(String usuario, BigDecimal montoCompra){
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
}
