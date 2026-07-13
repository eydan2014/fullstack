CREATE TABLE IF NOT EXISTS pedidos (
    id INT PRIMARY KEY AUTO_INCREMENT,
    usuario_id INT NOT NULL,
    total DOUBLE NOT NULL,
    estado VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS detalle_pedido (
    id INT PRIMARY KEY AUTO_INCREMENT,
    pedido_id INT NOT NULL,
    producto_id INT NOT NULL,
    cantidad INT NOT NULL,
    precio DOUBLE NOT NULL,

    CONSTRAINT fk_detalle_pedido_pedido
    FOREIGN KEY (pedido_id)
    REFERENCES pedidos(id)
    ON DELETE CASCADE
);
insert into pedidos (usuario_id, total, estado) values (1, 100.0, 'pendiente');
insert into detalle_pedido (pedido_id, producto_id, cantidad, precio) values (1, 1, 2, 50.0);
