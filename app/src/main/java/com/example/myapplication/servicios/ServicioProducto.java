package com.example.myapplication.servicios;

import android.content.Context;

import com.example.myapplication.data.DatabaseHelper;
import com.example.myapplication.modelos.Producto;

import java.util.List;

public class ServicioProducto {
    private final DatabaseHelper db;

    public ServicioProducto(Context ctx) {
        this.db = DatabaseHelper.getInstance(ctx);
    }

    public long crearProducto(Producto p) {
        p.setFechaCreacion(System.currentTimeMillis());
        p.setUltimaModificacion(System.currentTimeMillis());
        return db.insertarProducto(p);
    }

    public Producto obtenerProducto(long id) {
        return db.obtenerProducto(id);
    }

    public List<Producto> listarProductos() {
        return db.obtenerTodosProductos();
    }

    public boolean actualizarStock(long productoId, double delta) {
        return db.aplicarAjusteStock(db.getWritableDatabase(), productoId, delta);
    }

    public double calcularValorInventario() {
        double total = 0.0;
        for (Producto p : listarProductos()) {
            total += p.getPrecioCompra() * p.getStock();
        }
        return total;
    }
}
