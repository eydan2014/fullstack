package com.example.ms_cupones.service;

import com.example.ms_cupones.dto.AplicarCuponRequestDTO;
import com.example.ms_cupones.dto.AplicarCuponResponseDTO;
import com.example.ms_cupones.dto.CuponRequestDTO;
import com.example.ms_cupones.exception.ResourceNotFoundException;
import com.example.ms_cupones.model.Cupon;
import com.example.ms_cupones.repository.CuponRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class CuponServiceTest {

    @Mock
    private CuponRepository cuponRepository;

    @InjectMocks
    private CuponService cuponService;

    @Test
    void crearCupon_debeGuardarCuponCorrectamente() {

        CuponRequestDTO dto = crearCuponRequest();

        when(cuponRepository.save(any(Cupon.class))).thenAnswer(invocation -> {
            Cupon cupon = invocation.getArgument(0);
            cupon.setId(1);
            return cupon;
        });

        Cupon resultado = cuponService.crearCupon(dto);

        assertNotNull(resultado);
        assertEquals(1, resultado.getId());
        assertEquals("CAFE10", resultado.getCodigo());
        assertEquals("PORCENTAJE", resultado.getTipoDescuento());
        assertEquals(new BigDecimal("10"), resultado.getValor());
        assertTrue(resultado.getActivo());
        assertEquals(0, resultado.getUsosActuales());

        verify(cuponRepository, times(1)).save(any(Cupon.class));
    }

    @Test
    void aplicarCuponPorcentaje_debeCalcularDescuentoYTotalFinal() {

        Cupon cupon = crearCuponPorcentaje();

        AplicarCuponRequestDTO dto = new AplicarCuponRequestDTO();
        dto.setCodigo("CAFE10");
        dto.setMontoPedido(new BigDecimal("6500"));

        when(cuponRepository.findByCodigo("CAFE10")).thenReturn(Optional.of(cupon));
        when(cuponRepository.save(any(Cupon.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AplicarCuponResponseDTO resultado = cuponService.aplicarCupon(dto);

        assertNotNull(resultado);
        assertEquals("CAFE10", resultado.getCodigo());
        assertTrue(resultado.getValido());
        assertEquals(new BigDecimal("650.00"), resultado.getDescuento());
        assertEquals(new BigDecimal("5850.00"), resultado.getTotalFinal());
        assertEquals(1, cupon.getUsosActuales());

        verify(cuponRepository, times(1)).findByCodigo("CAFE10");
        verify(cuponRepository, times(1)).save(cupon);
    }

    @Test
    void aplicarCuponMonto_debeRestarMontoFijo() {

        Cupon cupon = crearCuponMonto();

        AplicarCuponRequestDTO dto = new AplicarCuponRequestDTO();
        dto.setCodigo("DESC2000");
        dto.setMontoPedido(new BigDecimal("7000"));

        when(cuponRepository.findByCodigo("DESC2000")).thenReturn(Optional.of(cupon));
        when(cuponRepository.save(any(Cupon.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AplicarCuponResponseDTO resultado = cuponService.aplicarCupon(dto);

        assertNotNull(resultado);
        assertEquals("DESC2000", resultado.getCodigo());
        assertEquals(0, new BigDecimal("2000.00").compareTo(resultado.getDescuento()));
        assertEquals(0, new BigDecimal("5000.00").compareTo(resultado.getTotalFinal()));

        verify(cuponRepository, times(1)).findByCodigo("DESC2000");
        verify(cuponRepository, times(1)).save(cupon);
    }

    @Test
    void aplicarCupon_cuandoNoExiste_debeLanzarResourceNotFoundException() {

        AplicarCuponRequestDTO dto = new AplicarCuponRequestDTO();
        dto.setCodigo("NOEXISTE");
        dto.setMontoPedido(new BigDecimal("6500"));

        when(cuponRepository.findByCodigo("NOEXISTE")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            cuponService.aplicarCupon(dto);
        });

        verify(cuponRepository, times(1)).findByCodigo("NOEXISTE");
        verify(cuponRepository, never()).save(any(Cupon.class));
    }

    @Test
    void aplicarCupon_cuandoMontoNoCumpleMinimo_debeLanzarIllegalArgumentException() {

        Cupon cupon = crearCuponPorcentaje();

        AplicarCuponRequestDTO dto = new AplicarCuponRequestDTO();
        dto.setCodigo("CAFE10");
        dto.setMontoPedido(new BigDecimal("1000"));

        when(cuponRepository.findByCodigo("CAFE10")).thenReturn(Optional.of(cupon));

        assertThrows(IllegalArgumentException.class, () -> {
            cuponService.aplicarCupon(dto);
        });

        verify(cuponRepository, times(1)).findByCodigo("CAFE10");
        verify(cuponRepository, never()).save(any(Cupon.class));
    }

    @Test
    void obtenerCupon_cuandoExiste_debeRetornarCupon() {

        Cupon cupon = crearCuponPorcentaje();
        cupon.setId(1);

        when(cuponRepository.findById(1)).thenReturn(Optional.of(cupon));

        Cupon resultado = cuponService.obtenerCupon(1);

        assertNotNull(resultado);
        assertEquals(1, resultado.getId());
        assertEquals("CAFE10", resultado.getCodigo());

        verify(cuponRepository, times(1)).findById(1);
    }

    @Test
    void listarCupones_debeRetornarLista() {

        Cupon cupon = crearCuponPorcentaje();

        when(cuponRepository.findAll()).thenReturn(List.of(cupon));

        List<Cupon> resultado = cuponService.listarCupones();

        assertEquals(1, resultado.size());
        assertEquals("CAFE10", resultado.get(0).getCodigo());

        verify(cuponRepository, times(1)).findAll();
    }

    @Test
    void actualizarEstado_debeCambiarActivo() {

        Cupon cupon = crearCuponPorcentaje();
        cupon.setId(1);
        cupon.setActivo(true);

        when(cuponRepository.findById(1)).thenReturn(Optional.of(cupon));
        when(cuponRepository.save(any(Cupon.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Cupon resultado = cuponService.cambiarEstado(1, false);

        assertFalse(resultado.getActivo());

        verify(cuponRepository, times(1)).findById(1);
        verify(cuponRepository, times(1)).save(cupon);
    }

    @Test
    void eliminarCupon_debeEliminarCuponExistente() {

        Cupon cupon = crearCuponPorcentaje();
        cupon.setId(1);

        when(cuponRepository.findById(1)).thenReturn(Optional.of(cupon));

        cuponService.eliminarCupon(1);

        verify(cuponRepository, times(1)).findById(1);
        verify(cuponRepository, times(1)).delete(cupon);
    }

    private CuponRequestDTO crearCuponRequest() {

        CuponRequestDTO dto = new CuponRequestDTO();
        dto.setCodigo("CAFE10");
        dto.setDescripcion("Descuento de 10%");
        dto.setTipoDescuento("PORCENTAJE");
        dto.setValor(new BigDecimal("10"));
        dto.setMontoMinimo(new BigDecimal("3000"));
        dto.setFechaInicio(LocalDate.of(2026, 1, 1));
        dto.setFechaFin(LocalDate.of(2026, 12, 31));
        dto.setUsosMaximos(100);

        return dto;
    }

    private Cupon crearCuponPorcentaje() {

        Cupon cupon = new Cupon();
        cupon.setId(1);
        cupon.setCodigo("CAFE10");
        cupon.setDescripcion("Descuento de 10%");
        cupon.setTipoDescuento("PORCENTAJE");
        cupon.setValor(new BigDecimal("10"));
        cupon.setMontoMinimo(new BigDecimal("3000"));
        cupon.setFechaInicio(LocalDate.of(2026, 1, 1));
        cupon.setFechaFin(LocalDate.of(2026, 12, 31));
        cupon.setActivo(true);
        cupon.setUsosMaximos(100);
        cupon.setUsosActuales(0);

        return cupon;
    }

    private Cupon crearCuponMonto() {

        Cupon cupon = new Cupon();
        cupon.setId(2);
        cupon.setCodigo("DESC2000");
        cupon.setDescripcion("Descuento fijo de 2000");
        cupon.setTipoDescuento("MONTO");
        cupon.setValor(new BigDecimal("2000"));
        cupon.setMontoMinimo(new BigDecimal("5000"));
        cupon.setFechaInicio(LocalDate.of(2026, 1, 1));
        cupon.setFechaFin(LocalDate.of(2026, 12, 31));
        cupon.setActivo(true);
        cupon.setUsosMaximos(50);
        cupon.setUsosActuales(0);

        return cupon;
    }
}


