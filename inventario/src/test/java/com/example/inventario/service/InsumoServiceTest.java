package com.example.inventario.service;


import com.example.inventario.dto.InsumoRequestDTO;
import com.example.inventario.model.Insumo;
import com.example.inventario.repository.InsumoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InsumoServiceTest {

    @Mock
    private InsumoRepository insumoRepository;

    @InjectMocks
    private InsumoService insumoService;

    @Test
    void registrarOActualizarInsumo_DeberiaGuardarCorrectamente() {
        // 1. GIVEN
        InsumoRequestDTO dto = new InsumoRequestDTO();
        dto.setNombre("Leche Entera");
        dto.setStock(48);

        when(insumoRepository.findByNombre(dto.getNombre())).thenReturn(Optional.empty());

        insumoService.registrarOActualizarInsumo(dto);

        ArgumentCaptor<Insumo> insumoCaptor = ArgumentCaptor.forClass(Insumo.class);
        verify(insumoRepository, times(1)).save(insumoCaptor.capture());

        Insumo insumoGuardado = insumoCaptor.getValue();
        assertNotNull(insumoGuardado);
        assertEquals(dto.getNombre(), insumoGuardado.getNombre());
        assertEquals(dto.getStock(), insumoGuardado.getStock());
    }
}