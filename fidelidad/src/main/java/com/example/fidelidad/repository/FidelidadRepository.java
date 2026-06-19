package com.example.fidelidad.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.fidelidad.model.Fidelidad;

@Repository
public interface FidelidadRepository extends JpaRepository<Fidelidad, Long> {
    Optional<Fidelidad> findByUsuario(String usuario);

}



