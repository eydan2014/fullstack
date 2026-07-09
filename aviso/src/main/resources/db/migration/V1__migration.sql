-- 1. Crear la tabla principal alineada con el objeto AvisoModel.java
CREATE TABLE IF NOT EXISTS avisos (
    id BIGINT NOT NULL AUTO_INCREMENT,
    usuario VARCHAR(255) NULL,
    mensaje VARCHAR(255) NULL,
    tipo VARCHAR(255) NULL,
    fecha_creacion DATETIME(6) NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
