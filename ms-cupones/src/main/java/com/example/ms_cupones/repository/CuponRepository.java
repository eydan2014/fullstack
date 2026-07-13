package com.example.ms_cupones.repository;

import com.example.ms_cupones.model.Cupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CuponRepository extends JpaRepository<Cupon, Integer> {

    Optional<Cupon> findByCodigo(String codigo);

    boolean existsByCodigo(String codigo);
}