package com.example.aviso.service;

import com.example.aviso.dto.AvisoRequestDTO; 
import com.example.aviso.model.AvisoModel;
import com.example.aviso.repository.AvisoRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AvisoServiceTest {

    @Mock
    private AvisoRepository avisoRepository;

    @InjectMocks
    private AvisoService avisoService;

    @Test
    void crearAviso_DeberiaGuardarCorrectamente() {
        AvisoRequestDTO dto = new AvisoRequestDTO();
        dto.setUsuario("juan123");
        dto.setMensaje("Compra exitosa por un total de $19.98");
        dto.setTipo("PAGO");
        
        avisoService.crearAviso(dto);
        ArgumentCaptor<AvisoModel> avisoCaptor = ArgumentCaptor.forClass(AvisoModel.class);
        verify(avisoRepository, times(1)).save(avisoCaptor.capture());

        AvisoModel avisoGuardado = avisoCaptor.getValue();
        assertNotNull(avisoGuardado, "El objeto guardado no debería ser nulo");
        assertEquals(dto.getUsuario(), avisoGuardado.getUsuario());
        assertEquals(dto.getMensaje(), avisoGuardado.getMensaje());
        assertEquals(dto.getTipo(), avisoGuardado.getTipo());
    }
}