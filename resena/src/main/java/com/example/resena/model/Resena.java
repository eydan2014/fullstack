package com.example.resena.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "resenas")
@Data
public class Resena {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_producto", nullable = false)
    private Long idProducto;

    @Column(nullable = false)
    private String usuario; 
    
    @Column(nullable = false)
    private Integer calificacion; 

    @Column(length = 500)
    private String comentario;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;
}