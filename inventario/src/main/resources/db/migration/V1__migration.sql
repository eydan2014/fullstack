CREATE TABLE IF NOT EXISTS insumos (
    id_insumo BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL UNIQUE,
    stock INT NOT NULL
);
insert into insumos (nombre, stock) values ('Insumo 1', 100);
insert into insumos (nombre, stock) values ('Insumo 2', 200);