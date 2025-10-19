-- Datos de ejemplo para poblar la base de datos SQLite de AppTienda

INSERT INTO productos (nombre, precio_compra, precio_venta, stock, categoria) VALUES
('Camiseta', 10.00, 20.00, 50, 'Ropa');
INSERT INTO productos (nombre, precio_compra, precio_venta, stock, categoria) VALUES
('Pantalon', 20.00, 40.00, 30, 'Ropa');
INSERT INTO productos (nombre, precio_compra, precio_venta, stock, categoria) VALUES
('Gorra', 5.00, 12.00, 100, 'Accesorios');

INSERT INTO usuarios (nombre, email, password_hash, rol) VALUES
('Administrador', 'admin@example.com', 'hash_de_ejemplo', 'admin');
INSERT INTO usuarios (nombre, email, password_hash, rol) VALUES
('Vendedor', 'vendedor@example.com', 'hash_de_ejemplo', 'user');
