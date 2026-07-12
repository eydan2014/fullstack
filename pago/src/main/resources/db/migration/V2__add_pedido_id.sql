-- Agrega la relación PAGOS.id_orden -> PEDIDOS del modelo ER (ms-pedidos).
-- Es nullable porque el flujo histórico de "pago directo" (sin pasar por
-- ms-pedidos) sigue siendo válido; cuando viene desde un pedido, se completa.
ALTER TABLE pagos
    ADD COLUMN pedido_id BIGINT NULL AFTER producto_id;
