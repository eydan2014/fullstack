package com.example.pago.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "pagos")
public class pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;    

    @Column(name = "usuario_id", nullable = false)
    private String usuarioId;
    @Column(name = "producto_id")
    private Long productoId;

    // 🔗 FK opcional hacia PEDIDOS (ms-pedidos). Nullable: el flujo histórico
    // de "pago directo" de un solo producto sigue funcionando sin pedidoId.
    @Column(name = "pedido_id")
    private Integer pedidoId;

    private Integer cantidad;

    @Column(name = "monto_total")
    private BigDecimal montoTotal;

    @Column(name = "metodo_pago")
    private String metodoPago;


    private String estado;

 @Column(name = "fecha_pago") 
    private LocalDateTime fechaPago;
    @PrePersist
    protected void onCreate(){
        this.fechaPago = LocalDateTime.now();
        this.estado = "Pendiente";
    }




}
