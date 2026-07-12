package com.example.ms_pedidos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.ms_pedidos.model.Pedido;

public interface PedidoRepository extends JpaRepository<Pedido, Integer>{

}
