package com.example.apptienda;

public class modeloCompra{
    private int idCompra;
    private Producto producto;
    private int RUC;
    private double precioCompra;
    private int cantidadComprada;
    private Date fechaCompra;

    public modeloCompra(int idCompra, Producto producto, int RUC, double precioCompra, int cantidadComprada, Date fechaCompra) {
        this.idCompra = idCompra;
        this.producto = producto;
        this.RUC = RUC;
        this.precioCompra = precioCompra;
        this.cantidadComprada = cantidadComprada;
        this.fechaCompra = fechaCompra;
    }

    // Getters
    public int getIdCompra() {
        return idCompra;
    }

    public Producto getProducto() {
        return producto;
    }

    public int getRUC() {
        return RUC;
    }

    public double getPrecioCompra() {
        return precioCompra;
    }

    public int getCantidadComprada() {
        return cantidadComprada;
    }

    public Date getFechaCompra() {
        return fechaCompra;
    }


    // Setters
    
    public void setIdCompra(int idCompra) {
        this.idCompra = idCompra;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public void setRUC(int RUC) {
        this.RUC = RUC;
    }

    public void setPrecioCompra(double precioCompra) {
        this.precioCompra = precioCompra;
    }

    public void setCantidadComprada(int cantidadComprada) {
        this.cantidadComprada = cantidadComprada;
    }

    public void setFechaCompra(Date fechaCompra) {
        this.fechaCompra = fechaCompra;
    }

}
