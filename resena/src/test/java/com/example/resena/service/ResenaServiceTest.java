package com.example.resena.service;

import com.example.resena.dto.ResenaRequestDTO; 
import com.example.resena.model.Resena;
import com.example.resena.repository.ResenaRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResenaServiceTest {

    @Mock
    private ResenaRepository resenaRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ResenaService resenaService;

    @Test
    void registrarResena_DeberiaGuardarCorrectamente() {
        ResenaRequestDTO dto = new ResenaRequestDTO();
        dto.setIdProducto(2L);
        dto.setUsuario("juan123");
        dto.setCalificacion(4);
        dto.setComentario("Muy buen producto.");
        
        // Simula la llamada remota de validación inter-servicio para evitar errores de red nulos
        when(restTemplate.getForObject(anyString(), eq(Boolean.class))).thenReturn(true);
        
        resenaService.registrarResena(dto);
        
        ArgumentCaptor<Resena> resenaCaptor = ArgumentCaptor.forClass(Resena.class);
        verify(resenaRepository, times(1)).save(resenaCaptor.capture());

        Resena resenaGuardado = resenaCaptor.getValue();
        assertNotNull(resenaGuardado);
        assertEquals(dto.getUsuario(), resenaGuardado.getUsuario());
        assertEquals(dto.getIdProducto(), resenaGuardado.getIdProducto());
    }
}