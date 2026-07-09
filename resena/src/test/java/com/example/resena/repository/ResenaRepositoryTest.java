package com.example.resena.repository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.resena.model.Resena;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ResenaRepositoryTest {

    @Mock
    private ResenaRepository resenaRepository; 

    @Test
    void debeGuardarYBuscarResenasExitosamente() {
        // 1. GIVEN
        Resena resena = new Resena();
        resena.setId(1L);
        resena.setIdProducto(1L);
        resena.setUsuario("user-789");
        resena.setCalificacion(5);
        resena.setComentario("¡Excelente servicio!");
        resena.setFechaCreacion(LocalDateTime.now());

        // Simulamos los comportamientos de la interfaz de JPA
        when(resenaRepository.save(any(Resena.class))).thenReturn(resena);
        when(resenaRepository.findByIdProducto(1L)).thenReturn(List.of(resena));

        // 2. WHEN
        Resena resenaGuardada = resenaRepository.save(resena);
        List<Resena> listaEncontrada = resenaRepository.findByIdProducto(1L);

        // 3. THEN
        assertNotNull(resenaGuardada);
        assertNotNull(resenaGuardada.getId());
        assertFalse(listaEncontrada.isEmpty());
        assertEquals("user-789", listaEncontrada.get(0).getUsuario());
        
        // Verificamos que se invocaron correctamente
        verify(resenaRepository, times(1)).save(any(Resena.class));
        verify(resenaRepository, times(1)).findByIdProducto(1L);
    }
}