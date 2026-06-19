package com.example.menu.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

@Data
@Entity
@Table(name = "productos")
public class Productos {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String nombre;
    
    private String descripcion;
    
    
    @Column(nullable = false)
    private BigDecimal precio;
    
    
    @Column(nullable = false)
    private int stock;
    
    @Column(name = "is_hot", nullable = false)
    private Boolean isHot;
    

}