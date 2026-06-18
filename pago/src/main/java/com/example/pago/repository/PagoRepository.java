package com.example.pago.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.pago.model.pago;

@Repository
public interface PagoRepository extends JpaRepository<pago, Long> {
 List<pago> findByUsuarioId(String usuarioId);

    
}
