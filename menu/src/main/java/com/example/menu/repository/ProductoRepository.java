package com.example.menu.repository;



import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.menu.model.Productos;

@Repository
public interface ProductoRepository extends JpaRepository<Productos, Long> {
   
}


