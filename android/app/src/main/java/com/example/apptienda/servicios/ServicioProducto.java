package com.example.apptienda.servicios;

import com.example.apptienda.modelos.Producto;
import com.example.apptienda.data.DatabaseHelper;
import com.example.apptienda.core.repositories.ProductRepository;
import com.example.apptienda.data.DatabaseProductRepository;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;

/**
 * Servicio para manejar la logica de negocio de productos.
 */
public class ServicioProducto {
    /**
     * ServicioProducto
     * - Capa de negocio para operaciones relacionadas con productos: crear, listar, actualizar stock, etc.
     * - Delegará llamadas a DatabaseHelper y encapsula reglas simples (ej. calcular precio de venta por defecto).
     */
    // Ahora ServicioProducto depende de la interfaz ProductRepository para desacoplar la persistencia
    private ProductRepository productRepository;

    /**
     * Constructor principal que recibe una implementación de ProductRepository.
     * Esto permite sustituir la persistencia (DB local, Room, API remota) sin cambiar la lógica.
     */
    public ServicioProducto(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Compatibilidad: constructor antiguo que acepta DatabaseHelper y crea un adaptador DatabaseProductRepository.
     */
    public ServicioProducto(DatabaseHelper dbHelper) {
        this(new DatabaseProductRepository(dbHelper));
    }

    public Producto crearProducto(String nombre, double precioCompra, double precioVenta, 
                                double stock, String categoria, String imagenUrl) {
        Producto producto = new Producto();
        producto.setNombre(nombre);
        producto.setPrecioCompra(precioCompra);
        producto.setPrecioVenta(precioVenta);
        producto.setStock(stock);
        producto.setCategoria(categoria);
        producto.setImagenUrl(imagenUrl);
        producto.setActivo(true);
        producto.setFechaCreacion(new Date());
        producto.setUltimaModificacion(new Date());
        
        long id = productRepository.insertarProducto(producto);
        producto.setId((int)id);
        return producto;
    }

    public Producto obtenerProductoPorId(long id) {
        return productRepository.obtenerProducto(id);
    }

    public List<Producto> listarProductos() {
        return productRepository.obtenerTodosLosProductos();
    }

    public List<Producto> listarProductosPorCategoria(String categoria) {
        return productRepository.obtenerProductosPorCategoria(categoria);
    }

    public boolean actualizarProducto(Producto producto) {
        producto.setUltimaModificacion(new Date());
        return productRepository.actualizarProducto(producto);
    }

    public boolean actualizarStock(long productoId, double cantidad) {
        Producto producto = productRepository.obtenerProducto(productoId);
        if (producto != null) {
            producto.actualizarStock(cantidad);
            return productRepository.actualizarProducto(producto);
        }
        return false;
    }

    public boolean desactivarProducto(int productoId) {
        Producto producto = productRepository.obtenerProducto(productoId);
        if (producto != null) {
            producto.setActivo(false);
            producto.setUltimaModificacion(new Date());
            return productRepository.actualizarProducto(producto);
        }
        return false;
    }

    public double calcularValorInventario() {
        List<Producto> productos = productRepository.obtenerTodosLosProductos();
        double total = 0;
        for (Producto p : productos) {
            total += p.getPrecioCompra() * p.getStock();
        }
        return total;
    }

    public List<Producto> buscarProductosBajoStock(int limiteMinimo) {
        List<Producto> todos = productRepository.obtenerTodosLosProductos();
        List<Producto> bajoStock = new ArrayList<>();
        for (Producto p : todos) {
            if (p.getStock() <= limiteMinimo) {
                bajoStock.add(p);
            }
        }
        return bajoStock;
    }
}
