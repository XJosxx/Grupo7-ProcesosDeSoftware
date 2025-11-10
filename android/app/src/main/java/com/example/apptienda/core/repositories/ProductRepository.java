package com.example.apptienda.core.repositories;

import com.example.apptienda.modelos.Producto;

import java.util.List;

/**
 * Interfaz que abstrae las operaciones de persistencia para productos.
 * Implementaciones concretas pueden usar SQLite (DatabaseHelper), Room, o servicios remotos.
 */
public interface ProductRepository {
    long insertarProducto(Producto producto);
    Producto obtenerProducto(long id);
    List<Producto> obtenerTodosLosProductos();
    List<Producto> obtenerProductosPorCategoria(String categoria);
    boolean actualizarProducto(Producto producto);
    boolean eliminarProducto(long id);
}
