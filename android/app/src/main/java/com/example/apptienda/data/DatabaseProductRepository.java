package com.example.apptienda.data;

import com.example.apptienda.core.repositories.ProductRepository;
import com.example.apptienda.modelos.Producto;

import java.util.List;

/**
 * Implementacion de ProductRepository que delega a DatabaseHelper (SQLite local).
 * Esta clase permite que la capa de negocio dependa de la interfaz y no de DatabaseHelper.
 */
public class DatabaseProductRepository implements ProductRepository {

    private final DatabaseHelper dbHelper;

    public DatabaseProductRepository(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    @Override
    public long insertarProducto(Producto producto) {
        return dbHelper.insertarProducto(producto);
    }

    @Override
    public Producto obtenerProducto(long id) {
        return dbHelper.obtenerProducto(id);
    }

    @Override
    public List<Producto> obtenerTodosLosProductos() {
        return dbHelper.obtenerTodosLosProductos();
    }

    @Override
    public List<Producto> obtenerProductosPorCategoria(String categoria) {
        return dbHelper.obtenerProductosPorCategoria(categoria);
    }

    @Override
    public boolean actualizarProducto(Producto producto) {
        return dbHelper.actualizarProducto(producto);
    }

    @Override
    public boolean eliminarProducto(long id) {
        return dbHelper.eliminarProducto(id);
    }
}
