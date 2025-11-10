-- Esquema básico adaptado para SQLite (AppTienda)
-- Crea tablas: productos, ventas, compras, usuarios

PRAGMA foreign_keys = ON;

CREATE TABLE IF NOT EXISTS productos (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  nombre TEXT NOT NULL,
  precio_compra REAL NOT NULL,
  precio_venta REAL,
  stock INTEGER DEFAULT 0,
  categoria TEXT,
  creado_en DATETIME DEFAULT (datetime('now'))
);

CREATE TABLE IF NOT EXISTS ventas (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  producto_id INTEGER NOT NULL,
  cantidad INTEGER NOT NULL,
  total REAL NOT NULL,
  fecha DATETIME DEFAULT (datetime('now')),
  FOREIGN KEY (producto_id) REFERENCES productos(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS compras (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  producto_id INTEGER NOT NULL,
  proveedor_ruc TEXT,
  precio_compra REAL NOT NULL,
  cantidad INTEGER NOT NULL,
  fecha DATETIME DEFAULT (datetime('now')),
  FOREIGN KEY (producto_id) REFERENCES productos(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS usuarios (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  nombre TEXT NOT NULL,
  email TEXT UNIQUE,
  password_hash TEXT NOT NULL,
  rol TEXT DEFAULT 'user',
  creado_en DATETIME DEFAULT (datetime('now'))
);

-- Índices recomendados
CREATE INDEX IF NOT EXISTS idx_productos_categoria ON productos(categoria);
CREATE INDEX IF NOT EXISTS idx_ventas_producto ON ventas(producto_id);
