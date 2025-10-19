package com.example.apptienda;

public class Producto {
    private int productoID;
    private String nombre;
    private int PreDeCompraStock;
    private String categoria;
    private int Stock;

    public Producto(int productoID, String nombre, int PreDeCompraStock, String categoria ) {
        this.productoID = productoID;
        this.nombre = nombre;
        this.PreDeCompraStock = PreDeCompraStock;
        this.categoria = categoria;
    }

    //geters

    public int getID() {
        return productoID;
    }

    public String getNombre() {
        return nombre;
    }

    public int getPrecioCompra() {
        return PreDeCompraStock;
    }

    public int getStock() {
        return Stock;
    }
    public String getCategoria () {
        return categoria;
    }       

    //setters

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setPrecioCompra(int PreDeCompraStock) {
        this.PreDeCompraStock = PreDeCompraStock;
    }

    public void setStock(int Stock) {
        this.Stock = Stock;
    }
    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    @Override
    public String toString() {
        return "Producto{" +
                "ProductoID =" + productoID +
                ", Nombre ='" + nombre + '\'' +
                ", Precio de compra =" + PreDeCompraStock +
                ", Stock =" + Stock +
                ", Categoria =" + categoria +
                '}';
    }

}