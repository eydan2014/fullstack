package com.example.ms_cocina.Repository;

import com.example.ms_cocina.Model.TicketCocina;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TicketCocinaRepository extends JpaRepository<TicketCocina, Integer> {

    Optional<TicketCocina> findByPedidoId(Integer pedidoId);

    List<TicketCocina> findByEstado(String estado);
}
