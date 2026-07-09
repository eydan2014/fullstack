package com.example.inventario.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "insumos")
@Data
public class Insumo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_insumo")
    private Long idInsumo;

    @Column(nullable = false, unique = true)
    private String nombre; // Ej: "Vasos", "Leche", "Grano de Cafe"

    @Column(nullable = false)
    private Integer stock; // Cantidad disponible
}