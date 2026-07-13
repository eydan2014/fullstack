-- Crear la tabla principal alineada con tu modelo pago.java
CREATE TABLE IF NOT EXISTS pagos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id VARCHAR(100) NOT NULL,
    producto_id BIGINT NOT NULL,
    cantidad INT NOT NULL,
    monto_total DECIMAL(10, 2) NOT NULL,
    metodo_pago VARCHAR(50),
    estado VARCHAR(20) DEFAULT 'PENDIENTE', 
    fecha_pago TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


INSERT INTO pagos (usuario_id, producto_id, cantidad, monto_total, metodo_pago, estado) VALUES
('user123', 1, 2, 19.98, 'TARJETA', 'COMPLETADO'),
('user456', 3, 1, 12.99, 'TRANSFERENCIA', 'COMPLETADO'),
('user789', 2, 3, 23.97, 'TARJETA', 'COMPLETADO'),
('user123', 5, 5, 14.95, 'TRANSFERENCIA', 'COMPLETADO'),
('user456', 4, 1, 4.99, 'TARJETA', 'COMPLETADO');