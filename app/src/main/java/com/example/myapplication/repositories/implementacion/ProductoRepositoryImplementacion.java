package com.example.myapplication.repositories.implementacion;

import com.example.myapplication.Configuracion.ConfiguracionBaseDatos;
import com.example.myapplication.entidades.Producto;
import com.example.myapplication.repositories.ProductoRepository;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductoRepositoryImplementacion implements ProductoRepository {

    @Override
    public boolean save(Producto producto) {

        String sql = "INSERT INTO producto (nombre, fecha_ingreso, precio_compra, precio_venta, cantidad, categoria_nombre) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConfiguracionBaseDatos.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, producto.getNombre());
            // Conversión de java.util.Date a java.sql.Date
            stmt.setDate(2, new java.sql.Date(producto.getFechaIngreso().getTime()));
            stmt.setDouble(3, producto.getPrecioCompra());
            stmt.setDouble(4, producto.getPrecioVenta());
            stmt.setInt(5, producto.getCantidad());
            stmt.setString(6, producto.getCategoriaNombre());

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        producto.setId(rs.getInt(1));
                    }
                }
                return true;
            }
            return false;

        } catch (SQLException e) {
            throw new RuntimeException("Error de BD al guardar el producto.", e);
        }
    }

    @Override
    public boolean update(Producto producto) {

        String sql = "UPDATE producto SET nombre=?, fecha_ingreso=?, precio_compra=?, precio_venta=?, cantidad=?, categoria_nombre=? WHERE idproducto=?";
        try (Connection conn = ConfiguracionBaseDatos.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, producto.getNombre());
            stmt.setDate(2, new java.sql.Date(producto.getFechaIngreso().getTime()));
            stmt.setDouble(3, producto.getPrecioCompra());
            stmt.setDouble(4, producto.getPrecioVenta());
            stmt.setInt(5, producto.getCantidad());
            stmt.setString(6, producto.getCategoriaNombre());
            stmt.setInt(7, producto.getId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Error de BD al actualizar el producto.", e);
        }
    }

    @Override
    public boolean delete(int id) {

        String sql = "DELETE FROM producto WHERE idproducto=?";
        try (Connection conn = ConfiguracionBaseDatos.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Error de BD al eliminar el producto.", e);
        }
    }

    @Override
    public Producto findById(int id) {

        String sql = "SELECT * FROM producto WHERE idproducto=?";
        try (Connection conn = ConfiguracionBaseDatos.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearProducto(rs);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error de BD al buscar producto por ID.", e);
        }
        return null;
    }

    @Override
    public List<Producto> findAll() {

        List<Producto> lista = new ArrayList<>();
        String sql = "SELECT * FROM producto";

        try (Connection conn = ConfiguracionBaseDatos.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearProducto(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error de BD al obtener todos los productos.", e);
        }
        return lista;
    }

    @Override
    public List<Producto> findByCategoria(String categoriaNombre) {

        List<Producto> lista = new ArrayList<>();
        String sql = "SELECT * FROM producto WHERE categoria_nombre=?";
        try (Connection conn = ConfiguracionBaseDatos.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, categoriaNombre);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearProducto(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error de BD al buscar productos por categoría.", e);
        }
        return lista;
    }

    @Override
    public List<Producto> findByNombre(String nombre) {

        List<Producto> lista = new ArrayList<>();
        String sql = "SELECT * FROM producto WHERE nombre LIKE ?";
        try (Connection conn = ConfiguracionBaseDatos.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + nombre + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearProducto(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error de BD al buscar productos por nombre.", e);
        }
        return lista;
    }

    // Método auxiliar para evitar código duplicado
    private Producto mapearProducto(ResultSet rs) throws SQLException {
        Producto p = new Producto();
        p.setId(rs.getInt("idproducto"));
        p.setNombre(rs.getString("nombre"));
        p.setFechaIngreso(rs.getDate("fecha_ingreso"));
        p.setPrecioCompra(rs.getDouble("precio_compra"));
        p.setPrecioVenta(rs.getDouble("precio_venta"));
        p.setCantidad(rs.getInt("cantidad"));
        p.setCategoriaNombre(rs.getString("categoria_nombre"));
        return p;
    }

    @Override
    public boolean existeIdById(int id) {
        String sql = "SELECT COUNT(idproducto) AS cnt FROM producto WHERE idproducto = ?";
        try (Connection conn = ConfiguracionBaseDatos.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("cnt") > 0;
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error de BD al verificar existencia de producto por ID.", e);
        }
        return false;
    }
}