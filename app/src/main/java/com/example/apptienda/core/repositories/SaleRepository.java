package com.example.apptienda.core.repositories;

import com.example.apptienda.modelos.modeloVenta;

import java.util.List;

/**
 * Interfaz que abstrae operaciones relacionadas con ventas.
 */
public interface SaleRepository {
    modeloVenta registrar(modeloVenta venta);
    List<modeloVenta> listar();
    modeloVenta obtenerPorCodigo(String codigo);
    modeloVenta obtenerPorId(long id);
    boolean actualizar(modeloVenta venta);
    boolean eliminar(long id);
    String generarCodigo();
}
