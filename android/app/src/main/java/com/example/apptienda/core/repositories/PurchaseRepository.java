package com.example.apptienda.core.repositories;

import com.example.apptienda.modelos.modeloCompra;

import java.util.List;

/**
 * Interfaz que abstrae operaciones relacionadas con compras (reabastecimientos).
 */
public interface PurchaseRepository {
    modeloCompra registrar(modeloCompra compra);
    List<modeloCompra> listar();
    boolean eliminar(long compraId);
}
