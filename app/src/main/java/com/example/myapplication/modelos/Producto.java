package com.example.myapplication.modelos;

public class Producto {
    private long id;
    private String nombre;
    private double precioCompra;
    private double precioVenta;
    private double stock;
    private String categoria;
    private String imagenUrl;
    private int activo = 1;
    private long fechaCreacion;
    private long ultimaModificacion;

    public Producto() {}

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public double getPrecioCompra() { return precioCompra; }
    public void setPrecioCompra(double precioCompra) { this.precioCompra = precioCompra; }
    public double getPrecioVenta() { return precioVenta; }
    public void setPrecioVenta(double precioVenta) { this.precioVenta = precioVenta; }
    public double getStock() { return stock; }
    public void setStock(double stock) { this.stock = stock; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public String getImagenUrl() { return imagenUrl; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }
    public int getActivo() { return activo; }
    public void setActivo(int activo) { this.activo = activo; }
    public long getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(long fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    public long getUltimaModificacion() { return ultimaModificacion; }
    public void setUltimaModificacion(long ultimaModificacion) { this.ultimaModificacion = ultimaModificacion; }
}
