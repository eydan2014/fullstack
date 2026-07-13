CREATE TABLE IF NOT EXISTS resenas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_producto BIGINT NOT NULL,
    usuario VARCHAR(255) NOT NULL,
    calificacion INT NOT NULL,
    comentario VARCHAR(500),
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
insert into resenas (id_producto, usuario, calificacion, comentario) values (1, 'usuario1', 5, 'Excelente producto');