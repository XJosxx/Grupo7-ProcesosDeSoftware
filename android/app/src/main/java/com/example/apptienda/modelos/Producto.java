package com.example.apptienda.modelos;

import java.util.Date;

public class Producto {
    /**
     * Producto
     * Modelo que representa un producto en inventario.
     * Contiene atributos (precio, stock, categoria) y métodos de negocio simples
     * como `actualizarStock` y `tieneStockSuficiente`.
     * Conexiones: usado por DatabaseHelper, ServicioProducto y vistas (ProductActivity, adapters).
     */
    private int id;
    private String nombre;
    private double precioCompra;
    private double precioVenta;
    private double stock;
    private String categoria;
    private String imagenUrl;
    private boolean activo;
    private Date fechaCreacion;
    private Date ultimaModificacion;

    public Producto() {
        this.fechaCreacion = new Date();
        this.ultimaModificacion = new Date();
        this.activo = true;
    }

    public Producto(int id, String nombre, double precioCompra, double precioVenta, double stock, String categoria) {
        this.id = id;
        this.nombre = nombre;
        this.precioCompra = precioCompra;
        this.precioVenta = precioVenta;
        this.stock = stock;
        this.categoria = categoria;
        this.fechaCreacion = new Date();
        this.ultimaModificacion = new Date();
        this.activo = true;
    }

    // Getters
    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public double getPrecioCompra() { return precioCompra; }
    public double getPrecioVenta() { return precioVenta; }
    public double getStock() { return stock; }
    public String getCategoria() { return categoria; }
    public String getImagenUrl() { return imagenUrl; }
    public boolean isActivo() { return activo; }
    public Date getFechaCreacion() { return fechaCreacion; }
    public Date getUltimaModificacion() { return ultimaModificacion; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setPrecioCompra(double precioCompra) { this.precioCompra = precioCompra; }
    public void setPrecioVenta(double precioVenta) { this.precioVenta = precioVenta; }
    public void setStock(double stock) { this.stock = stock; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }
    public void setActivo(boolean activo) { this.activo = activo; }
    public void setFechaCreacion(Date fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    public void setUltimaModificacion(Date ultimaModificacion) { this.ultimaModificacion = ultimaModificacion; }

    // Metodos de negocio
    public boolean actualizarStock(double cantidad) {
        if(cantidad < 0 && this.stock + cantidad < 0) {
            return false; // No se puede reducir el stock por debajo de cero
        }
        this.stock += cantidad;
        this.ultimaModificacion = new Date();
        return true;
    }

    public boolean tieneStockSuficiente(double cantidad) {
        return this.stock >= cantidad;
    }

    public double calcularGanancia() {
        return precioVenta - precioCompra;
    }

    @Override
    public String toString() {
        return "Producto{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", precioCompra=" + precioCompra +
                ", precioVenta=" + precioVenta +
                ", stock=" + stock +
                ", categoria='" + categoria + '\'' +
                ", activo=" + activo +
                '}';
    }
}
