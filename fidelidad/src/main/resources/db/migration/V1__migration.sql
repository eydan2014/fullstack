-- 1. Crear la tabla principal alineada exactamente con el objeto Fidelidad.java
CREATE TABLE IF NOT EXISTS fidelidad (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario VARCHAR(255) NOT NULL,
    puntos_totales INT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. Inserciones de prueba corregidas con columnas existentes
INSERT INTO fidelidad (usuario, puntos_totales) VALUES ('user1', 150);
INSERT INTO fidelidad (usuario, puntos_totales) VALUES ('user2', 300);