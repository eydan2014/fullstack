package com.example.ms_cupones.service;

import com.example.ms_cupones.dto.AplicarCuponRequestDTO;
import com.example.ms_cupones.dto.AplicarCuponResponseDTO;
import com.example.ms_cupones.dto.CuponRequestDTO;
import com.example.ms_cupones.exception.ResourceNotFoundException;
import com.example.ms_cupones.model.Cupon;
import com.example.ms_cupones.repository.CuponRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CuponService {

    private final CuponRepository cuponRepository;

    public List<Cupon> listarCupones() {

        log.info("Listando cupones");

        return cuponRepository.findAll();
    }

    public Cupon obtenerCupon(Integer id) {

        log.info("Buscando cupón con id {}", id);

        return cuponRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Cupón no encontrado con id {}", id);
                    return new ResourceNotFoundException("Cupón no encontrado");
                });
    }

    public Cupon obtenerPorCodigo(String codigo) {

        String codigoNormalizado = normalizarCodigo(codigo);

        log.info("Buscando cupón con código {}", codigoNormalizado);

        return cuponRepository.findByCodigo(codigoNormalizado)
                .orElseThrow(() -> {
                    log.warn("Cupón no encontrado con código {}", codigoNormalizado);
                    return new ResourceNotFoundException("Cupón no encontrado");
                });
    }

    public Cupon crearCupon(CuponRequestDTO dto) {

        String codigoNormalizado = normalizarCodigo(dto.getCodigo());

        log.info("Creando cupón con código {}", codigoNormalizado);

        if (cuponRepository.existsByCodigo(codigoNormalizado)) {
            throw new IllegalArgumentException("Ya existe un cupón con ese código");
        }

        validarDatosCupon(dto);

        Cupon cupon = new Cupon();

        cupon.setCodigo(codigoNormalizado);
        cupon.setDescripcion(dto.getDescripcion());
        cupon.setTipoDescuento(dto.getTipoDescuento().toUpperCase());
        cupon.setValor(dto.getValor());
        cupon.setMontoMinimo(dto.getMontoMinimo());
        cupon.setFechaInicio(dto.getFechaInicio());
        cupon.setFechaFin(dto.getFechaFin());
        cupon.setActivo(true);
        cupon.setUsosMaximos(dto.getUsosMaximos());
        cupon.setUsosActuales(0);

        Cupon cuponGuardado = cuponRepository.save(cupon);

        log.info("Cupón creado correctamente con id {}", cuponGuardado.getId());

        return cuponGuardado;
    }

    public Cupon actualizarCupon(Integer id, CuponRequestDTO dto) {

        log.info("Actualizando cupón con id {}", id);

        Cupon cupon = obtenerCupon(id);

        String codigoNormalizado = normalizarCodigo(dto.getCodigo());

        cuponRepository.findByCodigo(codigoNormalizado)
                .ifPresent(cuponExistente -> {
                    if (!cuponExistente.getId().equals(id)) {
                        throw new IllegalArgumentException("Ya existe otro cupón con ese código");
                    }
                });

        validarDatosCupon(dto);

        cupon.setCodigo(codigoNormalizado);
        cupon.setDescripcion(dto.getDescripcion());
        cupon.setTipoDescuento(dto.getTipoDescuento().toUpperCase());
        cupon.setValor(dto.getValor());
        cupon.setMontoMinimo(dto.getMontoMinimo());
        cupon.setFechaInicio(dto.getFechaInicio());
        cupon.setFechaFin(dto.getFechaFin());
        cupon.setUsosMaximos(dto.getUsosMaximos());

        Cupon cuponActualizado = cuponRepository.save(cupon);

        log.info("Cupón actualizado correctamente con id {}", id);

        return cuponActualizado;
    }

    public Cupon cambiarEstado(Integer id, Boolean activo) {

        log.info("Cambiando estado del cupón {} a {}", id, activo);

        Cupon cupon = obtenerCupon(id);

        cupon.setActivo(activo);

        Cupon cuponActualizado = cuponRepository.save(cupon);

        log.info("Estado del cupón actualizado correctamente");

        return cuponActualizado;
    }

    public AplicarCuponResponseDTO validarCupon(AplicarCuponRequestDTO dto) {

        log.info("Validando cupón {}", dto.getCodigo());

        Cupon cupon = obtenerPorCodigo(dto.getCodigo());

        validarCuponDisponible(cupon, dto.getMontoPedido());

        BigDecimal descuento = calcularDescuento(cupon, dto.getMontoPedido());
        BigDecimal totalFinal = dto.getMontoPedido().subtract(descuento);

        return AplicarCuponResponseDTO.builder()
                .codigo(cupon.getCodigo())
                .valido(true)
                .descuento(descuento)
                .totalFinal(totalFinal)
                .mensaje("Cupón válido")
                .build();
    }

    public AplicarCuponResponseDTO aplicarCupon(AplicarCuponRequestDTO dto) {

        log.info("Aplicando cupón {}", dto.getCodigo());

        Cupon cupon = obtenerPorCodigo(dto.getCodigo());

        validarCuponDisponible(cupon, dto.getMontoPedido());

        BigDecimal descuento = calcularDescuento(cupon, dto.getMontoPedido());
        BigDecimal totalFinal = dto.getMontoPedido().subtract(descuento);

        cupon.setUsosActuales(cupon.getUsosActuales() + 1);

        cuponRepository.save(cupon);

        log.info("Cupón {} aplicado correctamente", cupon.getCodigo());

        return AplicarCuponResponseDTO.builder()
                .codigo(cupon.getCodigo())
                .valido(true)
                .descuento(descuento)
                .totalFinal(totalFinal)
                .mensaje("Cupón aplicado correctamente")
                .build();
    }

    public void eliminarCupon(Integer id) {

        log.info("Eliminando cupón con id {}", id);

        Cupon cupon = obtenerCupon(id);

        cuponRepository.delete(cupon);

        log.info("Cupón eliminado correctamente con id {}", id);
    }

    private void validarDatosCupon(CuponRequestDTO dto) {

        String tipo = dto.getTipoDescuento().toUpperCase();

        if (!tipo.equals("PORCENTAJE") && !tipo.equals("MONTO")) {
            throw new IllegalArgumentException("Tipo de descuento inválido. Use PORCENTAJE o MONTO");
        }

        if (tipo.equals("PORCENTAJE")
                && dto.getValor().compareTo(BigDecimal.valueOf(100)) > 0) {

            throw new IllegalArgumentException("El porcentaje no puede ser mayor a 100");
        }

        if (dto.getFechaFin().isBefore(dto.getFechaInicio())) {
            throw new IllegalArgumentException("La fecha de fin no puede ser anterior a la fecha de inicio");
        }
    }

    private void validarCuponDisponible(Cupon cupon, BigDecimal montoPedido) {

        LocalDate hoy = LocalDate.now();

        if (!Boolean.TRUE.equals(cupon.getActivo())) {
            throw new IllegalArgumentException("El cupón está inactivo");
        }

        if (hoy.isBefore(cupon.getFechaInicio())) {
            throw new IllegalArgumentException("El cupón aún no está disponible");
        }

        if (hoy.isAfter(cupon.getFechaFin())) {
            throw new IllegalArgumentException("El cupón está vencido");
        }

        if (cupon.getUsosActuales() >= cupon.getUsosMaximos()) {
            throw new IllegalArgumentException("El cupón ya alcanzó su límite de usos");
        }

        if (montoPedido.compareTo(cupon.getMontoMinimo()) < 0) {
            throw new IllegalArgumentException("El pedido no cumple con el monto mínimo del cupón");
        }
    }

    private BigDecimal calcularDescuento(Cupon cupon, BigDecimal montoPedido) {

        BigDecimal descuento;

        if (cupon.getTipoDescuento().equals("PORCENTAJE")) {

            descuento = montoPedido
                    .multiply(cupon.getValor())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        } else {

            descuento = cupon.getValor();
        }

        if (descuento.compareTo(montoPedido) > 0) {
            descuento = montoPedido;
        }

        return descuento;
    }

    private String normalizarCodigo(String codigo) {

        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("El código del cupón es obligatorio");
        }

        return codigo.trim().toUpperCase();
    }
}