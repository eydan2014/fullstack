package com.example.ms_pedidos.service;

import com.example.ms_pedidos.dto.PedidoRequestDTO;
import com.example.ms_pedidos.exception.ResourceNotFoundException;
import com.example.ms_pedidos.model.DetallePedido;
import com.example.ms_pedidos.model.Pedido;
import com.example.ms_pedidos.repository.DetallePedidoRepository;
import com.example.ms_pedidos.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.example.ms_pedidos.client.CuponClient;
import com.example.ms_pedidos.client.MenuClient;
import com.example.ms_pedidos.client.UserClient;
import com.example.ms_pedidos.dto.AplicarCuponResponseDTO;

import java.math.BigDecimal;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final DetallePedidoRepository detallePedidoRepository;
    private final CuponClient cuponClient;
    private final UserClient userClient;
    private final MenuClient menuClient;

    public List<Pedido> listarPedidos() {

        log.info("Listando pedidos");

        return pedidoRepository.findAll();
    }

    public Pedido obtenerPedido(Integer id) {

        log.info("Buscando pedido con id {}", id);

        return pedidoRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Pedido no encontrado"));
    }

    public Pedido crearPedido(PedidoRequestDTO dto) {

    log.info("Creando pedido para usuario {}", dto.getUsuarioId());

    // 🔗 INTER-SERVICIO: validamos que el usuario exista en 'user' antes de crear el pedido
    if (!userClient.existeUsuario(dto.getUsuarioId())) {
        log.error("Validación fallida: el usuario {} no existe.", dto.getUsuarioId());
        throw new ResourceNotFoundException("El usuario " + dto.getUsuarioId() + " no existe");
    }

    // 🔗 INTER-SERVICIO: validamos que cada producto del detalle exista en 'menu'
    for (PedidoRequestDTO.DetalleDTO detalleDTO : dto.getDetalles()) {
        if (!menuClient.existeProducto(detalleDTO.getProductoId())) {
            log.error("Validación fallida: el producto {} no existe.", detalleDTO.getProductoId());
            throw new ResourceNotFoundException("El producto " + detalleDTO.getProductoId() + " no existe en el catálogo");
        }
    }

    Pedido pedido = new Pedido();

    pedido.setUsuarioId(dto.getUsuarioId());
    pedido.setEstado("PENDIENTE");

    double total = 0;

    for (PedidoRequestDTO.DetalleDTO detalleDTO : dto.getDetalles()) {
        total += detalleDTO.getCantidad() * detalleDTO.getPrecio();
    }

    if (dto.getCodigoCupon() != null && !dto.getCodigoCupon().isBlank()) {

        log.info("Aplicando cupón {} al pedido", dto.getCodigoCupon());

        AplicarCuponResponseDTO cuponResponse = cuponClient.aplicarCupon(
                dto.getCodigoCupon(),
                BigDecimal.valueOf(total)
        );

        total = cuponResponse.getTotalFinal().doubleValue();

        log.info("Total después de aplicar cupón: {}", total);
    }

    pedido.setTotal(total);

    Pedido pedidoGuardado = pedidoRepository.save(pedido);

    for (PedidoRequestDTO.DetalleDTO detalleDTO : dto.getDetalles()) {

        DetallePedido detalle = new DetallePedido();

        detalle.setPedidoId(pedidoGuardado.getId());
        detalle.setProductoId(detalleDTO.getProductoId());
        detalle.setCantidad(detalleDTO.getCantidad());
        detalle.setPrecio(detalleDTO.getPrecio());

        detallePedidoRepository.save(detalle);
    }

    log.info("Pedido creado correctamente con id {}", pedidoGuardado.getId());

    return pedidoGuardado;
}

    public Pedido actualizarEstado(Integer id, String estado) {

        Pedido pedido = obtenerPedido(id);

        pedido.setEstado(estado);

        log.info("Actualizando estado del pedido {}", id);

        return pedidoRepository.save(pedido);
    }

    public void eliminarPedido(Integer id) {

        Pedido pedido = obtenerPedido(id);

        pedidoRepository.delete(pedido);

        log.info("Pedido eliminado con id {}", id);
    }
}