CREATE TABLE fidelidad (
    id INT AUTO_INCREMENT PRIMARY KEY,
    usuario VARCHAR(100) NOT NULL UNIQUE, -- El username que viene del JWT
    puntos_totales INT DEFAULT 0,
    ultima_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE historial_puntos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    usuario VARCHAR(100) NOT NULL,
    puntos_ganados INT NOT NULL,
    pago_id INT NOT NULL, -- ID del pago que generó estos puntos
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

insert into fidelidad (usuario, puntos_totales) values ('user1', 0);
insert into fidelidad (usuario, puntos_totales) values ('user2', 0);