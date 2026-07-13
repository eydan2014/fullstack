
ALTER TABLE pagos
    ADD COLUMN pedido_id BIGINT NULL AFTER producto_id;

insert into pagos (usuario_id, producto_id, monto, estado, pedido_id) values (1, 1, 100.0, 'pendiente', 1);