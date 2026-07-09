package com.example.aviso.service;

import com.example.aviso.dto.AvisoRequestDTO;
import com.example.aviso.model.AvisoModel;
import com.example.aviso.repository.AvisoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AvisoService {

    private final AvisoRepository avisoRepository;

    public void crearAviso(AvisoRequestDTO dto) {
        AvisoModel aviso = new AvisoModel();
        aviso.setUsuario(dto.getUsuario());
        aviso.setMensaje(dto.getMensaje());
        aviso.setTipo(dto.getTipo());
        avisoRepository.save(aviso);

        log.info("[SERVICE] Notificación registrada para el usuario: {}. Mensaje: {}",
                dto.getUsuario(), dto.getMensaje());
    }
}
