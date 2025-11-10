package com.example.apptienda.modelos;

/**
 * Modelo simple para una categoria de producto.
 */
public class Categoria {
    private int id;
    private String nombre;

    public Categoria() {}

    public Categoria(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }

    public void setId(int id) { this.id = id; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    @Override
    public String toString() {
        return "Categoria{" + "id=" + id + ", nombre='" + nombre + '\'' + '}';
    }
}
