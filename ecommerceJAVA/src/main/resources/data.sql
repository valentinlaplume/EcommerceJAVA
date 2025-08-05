-- data.sql

-- NOTA: Si usas H2, las secuencias para los IDs se gestionan automáticamente.
--       En bases de datos como PostgreSQL, podrías necesitar reiniciar secuencias
--       después de un DELETE si no haces un DROP TABLE completo.
--       Para desarrollo, con ddl-auto=create-drop o create, las tablas se recrean cada vez.

-- ---------------------------------
-- 1. Inserción de Usuarios
-- Los IDs se autogenerarán (ej. 1, 2, 3...)
-- ---------------------------------
INSERT INTO usuarios (usuario, nombre, apellido, dni, mail, telefono, contrasena) VALUES
('valentin.laplume', 'Valentin', 'Laplume', '12345678', 'laplume.valentin@example.com', '1122334455', 'asd123'),
('maria.gomez', 'Maria', 'Gomez', '87654321', 'maria.gomez@example.com', '1198765432', 'asd123'),
('carlos.ruiz', 'Carlos', 'Ruiz', '98765432', 'carlos.ruiz@example.com', '1155667788', 'asd123');


-- ---------------------------------
-- 2. Inserción de Categorías
-- Los IDs se autogenerarán (ej. 1, 2, 3...)
-- ---------------------------------
INSERT INTO categorias (nombre) VALUES ('Tecnología');     -- ID = 1
INSERT INTO categorias (nombre) VALUES ('Hogar');          -- ID = 2
INSERT INTO categorias (nombre) VALUES ('Oficina');        -- ID = 3
INSERT INTO categorias (nombre) VALUES ('Accesorios');     -- ID = 4
INSERT INTO categorias (nombre) VALUES ('Alimentos');      -- ID = 5


-- ---------------------------------
-- 3. Inserción de Productos
-- Asegúrate que los id_categoria existan en la tabla 'categorias'
-- ---------------------------------
-- Productos de Tecnología (id_categoria = 1)
INSERT INTO productos (nombre, precio, stock, descripcion, id_categoria) VALUES ('Monitor', 1000.0, 10, 'Monitor de alta resolución para gaming.', 1);
INSERT INTO productos (nombre, precio, stock, descripcion, id_categoria) VALUES ('Micrófono', 2000.0, 10, 'Micrófono profesional para streaming.', 1);
INSERT INTO productos (nombre, precio, stock, descripcion, id_categoria) VALUES ('Teclado mecánico', 1500.0, 15, 'Teclado con switches mecánicos y retroiluminación RGB.', 1);
INSERT INTO productos (nombre, precio, stock, descripcion, id_categoria) VALUES ('Mouse gamer', 1200.0, 20, 'Mouse ergonómico con alta precisión y botones programables.', 1);
INSERT INTO productos (nombre, precio, stock, descripcion, id_categoria) VALUES ('Laptop', 15000.0, 5, 'Laptop potente para trabajo y entretenimiento.', 1);
INSERT INTO productos (nombre, precio, stock, descripcion, id_categoria) VALUES ('Smartphone', 12000.0, 8, 'Smartphone con cámara de alta resolución.', 1);
-- Productos de Oficina (id_categoria = 3)
INSERT INTO productos (nombre, precio, stock, descripcion, id_categoria) VALUES ('Impresora Multifunción', 4000.0, 6, 'Impresora, copiadora y escáner para oficina.', 3);
-- Productos de Hogar (id_categoria = 2)
INSERT INTO productos (nombre, precio, stock, descripcion, id_categoria) VALUES ('Cafetera Eléctrica', 1500.0, 10, 'Cafetera programable con filtro permanente.', 2);
-- Productos de Accesorios (id_categoria = 4)
INSERT INTO productos (nombre, precio, stock, descripcion, id_categoria) VALUES ('Mochila para Laptop', 1200.0, 20, 'Mochila resistente al agua con compartimento para laptop.', 4);
INSERT INTO productos (nombre, precio, stock, descripcion, id_categoria) VALUES ('Power Bank 10000mAh', 800.0, 30, 'Batería externa portátil para cargar dispositivos móviles.', 4);
-- Productos de Alimentos (id_categoria = 5)
INSERT INTO productos (nombre, precio, stock, descripcion, id_categoria) VALUES ('Café Molido Premium 250g', 350.0, 100, 'Café 100% arábica de tueste medio.', 5);


-- ---------------------------------
-- 4. Inserción de Pedidos_Estados
-- Los IDs se autogenerarán (ej. 1, 2, 3...)
-- ---------------------------------
INSERT INTO pedidos_estados (nombre) VALUES ('PENDIENTE');    -- ID = 1
INSERT INTO pedidos_estados (nombre) VALUES ('PROCESANDO');   -- ID = 2
INSERT INTO pedidos_estados (nombre) VALUES ('ENVIADO');      -- ID = 3
INSERT INTO pedidos_estados (nombre) VALUES ('ENTREGADO');    -- ID = 4
INSERT INTO pedidos_estados (nombre) VALUES ('CANCELADO');    -- ID = 5


-- ---------------------------------
-- 5. Inserción de Pedidos
-- Asegúrate que los id_usuario y estado_id existan en sus respectivas tablas.
-- El formato de fecha/hora para H2 es 'YYYY-MM-DD HH:MM:SS'
-- ---------------------------------
-- Pedido 1: Juan Perez (id_usuario = 1), Estado: CONFIRMADO (estado_id = 2)
INSERT INTO pedidos (id_usuario, fecha_creacion, estado_id) VALUES (1, '2025-07-20 10:00:00', 2);
-- Pedido 2: Maria Gomez (id_usuario = 2), Estado: PENDIENTE (estado_id = 1)
INSERT INTO pedidos (id_usuario, fecha_creacion, estado_id) VALUES (2, '2025-07-20 11:30:00', 1);
-- Pedido 3: Juan Perez (id_usuario = 1), Estado: ENVIADO (estado_id = 3)
INSERT INTO pedidos (id_usuario, fecha_creacion, estado_id) VALUES (1, '2025-07-19 15:45:00', 3);


-- ---------------------------------
-- 6. Inserción de Pedidos_Detalle
-- Asegúrate que los pedido_id y producto_id existan en sus respectivas tablas.
-- El precio_unitario debe ser el precio del producto en el momento de la compra.
-- ---------------------------------
-- Detalles para Pedido 1 (id = 1):
-- Producto: Monitor (id = 1), precio al momento de la compra: 1000.0
INSERT INTO pedidos_detalle (pedido_id, producto_id, cantidad, precio_unitario) VALUES (1, 1, 2, 1000.0);
-- Producto: Teclado mecánico (id = 3), precio al momento de la compra: 1500.0
INSERT INTO pedidos_detalle (pedido_id, producto_id, cantidad, precio_unitario) VALUES (1, 3, 1, 1500.0);
-- Producto: Power Bank 10000mAh (id = 10), precio al momento de la compra: 800.0
INSERT INTO pedidos_detalle (pedido_id, producto_id, cantidad, precio_unitario) VALUES (1, 10, 3, 800.0);

-- Detalles para Pedido 2 (id = 2):
-- Producto: Laptop (id = 5), precio al momento de la compra: 15000.0
INSERT INTO pedidos_detalle (pedido_id, producto_id, cantidad, precio_unitario) VALUES (2, 5, 1, 15000.0);
-- Producto: Mochila para Laptop (id = 9), precio al momento de la compra: 1200.0
INSERT INTO pedidos_detalle (pedido_id, producto_id, cantidad, precio_unitario) VALUES (2, 9, 1, 1200.0);

-- Detalles para Pedido 3 (id = 3):
-- Producto: Smartphone (id = 6), precio al momento de la compra: 12000.0
INSERT INTO pedidos_detalle (pedido_id, producto_id, cantidad, precio_unitario) VALUES (3, 6, 1, 12000.0);
-- Producto: Micrófono (id = 2), precio al momento de la compra: 2000.0
INSERT INTO pedidos_detalle (pedido_id, producto_id, cantidad, precio_unitario) VALUES (3, 2, 1, 2000.0);