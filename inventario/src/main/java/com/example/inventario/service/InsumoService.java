package com.example.inventario.service;

import com.example.inventario.dto.InsumoRequestDTO;
import com.example.inventario.model.Insumo;
import com.example.inventario.repository.InsumoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class InsumoService {

    private final InsumoRepository insumoRepository;

    @Transactional
    public void registrarOActualizarInsumo(InsumoRequestDTO dto) {
        log.info("[INVENTARIO] Procesando actualización para el insumo: {}", dto.getNombre());
        
        Insumo insumo = insumoRepository.findByNombre(dto.getNombre())
                .orElse(new Insumo());
        
        insumo.setNombre(dto.getNombre());
        insumo.setStock(dto.getStock());
        
        insumoRepository.save(insumo);
        log.info("[INVENTARIO] Stock actualizado con éxito. Insumo: {}, Total: {}", insumo.getNombre(), insumo.getStock());
    }
}