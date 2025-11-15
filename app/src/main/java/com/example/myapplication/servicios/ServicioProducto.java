package com.example.myapplication.servicios;

import com.example.myapplication.entidades.Producto;
import java.util.List;

public interface ServicioProducto {


    boolean validarStock(int productoId, int cantidad);
    void actualizarInventario(Producto producto);
    List<Producto> obtenerTodos();
    Producto obtenerPorId(int id);
    List<Producto> buscarProductos(String criterio, String tipo);

    boolean agregarProducto(Producto producto);

    boolean eliminarProducto(int idProducto);
}