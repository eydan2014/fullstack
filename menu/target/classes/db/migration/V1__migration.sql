
CREATE TABLE IF NOT EXISTS productos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    descripcion VARCHAR(255),
    precio DECIMAL(38,2) NOT NULL,
    stock INT NOT NULL,
    is_hot BOOLEAN NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. Inserciones de datos de prueba válidos
INSERT INTO productos (nombre, descripcion, precio, stock, is_hot) VALUES
('Café Espresso', 'Café molido para espresso', 9.99, 100, true),
('Café Americano', 'Café molido para filtro', 7.99, 150, false),
('Café Latte', 'Café molido para latte', 11.99, 80, true),
('Café Cappuccino', 'Café molido para cappuccino', 10.99, 120, true),
('Café Mocha', 'Café molido para mocha', 12.99, 60, true);