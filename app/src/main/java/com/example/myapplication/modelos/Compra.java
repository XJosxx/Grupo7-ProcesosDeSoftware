package com.example.myapplication.modelos;

public class Compra {
    private long id;
    private long productoId;
    private String proveedorRuc;
    private String proveedorNombre;
    private double precioUnitario;
    private double cantidad;
    private double total;
    private long fecha;

    public Compra() {}

    // Getters / setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public long getProductoId() { return productoId; }
    public void setProductoId(long productoId) { this.productoId = productoId; }
    public String getProveedorRuc() { return proveedorRuc; }
    public void setProveedorRuc(String proveedorRuc) { this.proveedorRuc = proveedorRuc; }
    public String getProveedorNombre() { return proveedorNombre; }
    public void setProveedorNombre(String proveedorNombre) { this.proveedorNombre = proveedorNombre; }
    public double getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(double precioUnitario) { this.precioUnitario = precioUnitario; }
    public double getCantidad() { return cantidad; }
    public void setCantidad(double cantidad) { this.cantidad = cantidad; }
    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
    public long getFecha() { return fecha; }
    public void setFecha(long fecha) { this.fecha = fecha; }
}
