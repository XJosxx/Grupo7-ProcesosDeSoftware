package com.example.myapplication.servicios;

import com.example.myapplication.entidades.Venta;
import com.example.myapplication.entidades.DetalleVenta;
import com.example.myapplication.entidades.Producto;
import com.example.myapplication.DTOs.MontosCalculados;
import com.example.myapplication.DTOs.BoletaVentaDTO;
import java.util.List;

public interface ServicioVenta {
    int registrarVenta(Venta venta, List<DetalleVenta> detalles);
    List<Venta> obtenerVentasPorDia(String fecha);
    List<Venta> obtenerVentasPorRango(String fechaInicio, String fechaFin);
    List<Producto> buscarProductoEnVentaPorIdONombre(String criterio);
    boolean validarProductoExiste(int productoId);

    // Métodos para cálculos
    MontosCalculados calcularMontos(double precioUnitario, int cantidad);
    MontosCalculados calcularMontosVentaCompleta(List<DetalleVenta> detalles);
    double calcularTotalVenta(List<DetalleVenta> detalles);

    BoletaVentaDTO generarBoleta(int ventaId, List<DetalleVenta> detalles);

    List<Venta> listarVentas();

    boolean eliminarVenta(int ventaId);
}