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
insert into cupones (codigo, descripcion, tipo_descuento, valor, monto_minimo, fecha_inicio, fecha_fin, activo, usos_maximos, usos_actuales)
 values ('DESCUENTO10', 'Descuento del 10% en tu compra', 'porcentaje', 10.00, 50.00, '2024-01-01', '2024-12-31', 1, 100, 0);