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

