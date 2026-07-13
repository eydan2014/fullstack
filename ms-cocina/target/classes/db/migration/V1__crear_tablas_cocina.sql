CREATE TABLE IF NOT EXISTS tickets_cocina (
    id INT PRIMARY KEY AUTO_INCREMENT,
    pedido_id INT NOT NULL,
    estado VARCHAR(50) NOT NULL,
    observacion VARCHAR(255),
    fecha_creacion DATETIME NOT NULL,
    fecha_actualizacion DATETIME
);
insert into tickets_cocina (pedido_id, estado, observacion, fecha_creacion, fecha_actualizacion) 
values (1, 'pendiente', 'Ticket de cocina para el pedido 1', NOW(), NULL);