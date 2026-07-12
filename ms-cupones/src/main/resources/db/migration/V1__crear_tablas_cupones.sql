CREATE TABLE IF NOT EXISTS cupones (
    id INT PRIMARY KEY AUTO_INCREMENT,
    codigo VARCHAR(50) NOT NULL UNIQUE,
    descripcion VARCHAR(255),
    tipo_descuento VARCHAR(30) NOT NULL,
    valor DECIMAL(10,2) NOT NULL,
    monto_minimo DECIMAL(10,2) NOT NULL,
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE NOT NULL,
    activo BIT NOT NULL,
    usos_maximos INT NOT NULL,
    usos_actuales INT NOT NULL
);