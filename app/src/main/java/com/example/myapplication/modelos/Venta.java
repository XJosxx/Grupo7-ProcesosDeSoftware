package com.example.myapplication.modelos;

public class Venta {
    private long id;
    private String codigo;
    private long productoId;
    private String productoNombre;
    private String dniCliente;
    private double precioUnitario;
    private double costoUnitario;
    private double cantidad;
    private double igvPorcentaje;
    private double igvMonto;
    private double totalSinIgv;
    private double totalConIgv;
    private long fecha;

    public Venta() {}

    public void recalcularTotales() {
        this.totalSinIgv = precioUnitario * cantidad;
        this.igvMonto = totalSinIgv * (igvPorcentaje / 100.0);
        this.totalConIgv = totalSinIgv + igvMonto;
    }

    // Getters / setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public long getProductoId() { return productoId; }
    public void setProductoId(long productoId) { this.productoId = productoId; }
    public String getProductoNombre() { return productoNombre; }
    public void setProductoNombre(String productoNombre) { this.productoNombre = productoNombre; }
    public String getDniCliente() { return dniCliente; }
    public void setDniCliente(String dniCliente) { this.dniCliente = dniCliente; }
    public double getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(double precioUnitario) { this.precioUnitario = precioUnitario; }
    public double getCostoUnitario() { return costoUnitario; }
    public void setCostoUnitario(double costoUnitario) { this.costoUnitario = costoUnitario; }
    public double getCantidad() { return cantidad; }
    public void setCantidad(double cantidad) { this.cantidad = cantidad; }
    public double getIgvPorcentaje() { return igvPorcentaje; }
    public void setIgvPorcentaje(double igvPorcentaje) { this.igvPorcentaje = igvPorcentaje; }
    public double getIgvMonto() { return igvMonto; }
    public void setIgvMonto(double igvMonto) { this.igvMonto = igvMonto; }
    public double getTotalSinIgv() { return totalSinIgv; }
    public void setTotalSinIgv(double totalSinIgv) { this.totalSinIgv = totalSinIgv; }
    public double getTotalConIgv() { return totalConIgv; }
    public void setTotalConIgv(double totalConIgv) { this.totalConIgv = totalConIgv; }
    public long getFecha() { return fecha; }
    public void setFecha(long fecha) { this.fecha = fecha; }
}
