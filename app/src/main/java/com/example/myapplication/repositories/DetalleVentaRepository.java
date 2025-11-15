package com.example.myapplication.repositories;

import com.example.myapplication.entidades.DetalleVenta;
import java.util.List;

/*
    Interfaz del repositorio para la entidad DetalleVenta
    define los métodos CRUD para interactuar con la base de datos.
*/
public interface DetalleVentaRepository {
    boolean save(DetalleVenta detalle);
    boolean update(DetalleVenta detalle);
    boolean delete(int id);
    DetalleVenta findById(int id);
    List<DetalleVenta> findByVenta(int ventaId);
}