package com.example.myapplication.servicios;

import com.example.myapplication.entidades.Compra;
import com.example.myapplication.entidades.DetalleCompra;
import java.util.List;

public interface ServicioCompra {
    boolean registrarCompra(Compra compra, List<DetalleCompra> detalles);
    List<Compra> listarCompras();

    boolean actualizarStockPorCompra(List<DetalleCompra> detalles);

    boolean eliminarDetalleCompra(int detalleId);

    boolean editarDetalleCompra(int detalleId, int cantidad, double precio);

    DetalleCompra agregarProductoNuevoACompra(DetalleCompra detalle);
    boolean validarDatosProductoNuevo(String codigo, int cantidad, double precioCompra);
    DetalleCompra agregarProductoExistenteACompra(int productoId, int cantidad);

    boolean eliminarCompra(int compraId);
}