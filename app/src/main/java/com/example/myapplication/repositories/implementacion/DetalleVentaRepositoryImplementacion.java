package com.example.myapplication.repositories.implementacion;

import com.example.myapplication.entidades.DetalleVenta;
import com.example.myapplication.repositories.DetalleVentaRepository;
import com.example.myapplication.Configuracion.ConfiguracionBaseDatos;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DetalleVentaRepositoryImplementacion implements DetalleVentaRepository {

    @Override
    public boolean save(DetalleVenta detalle) {
        String sql = "INSERT INTO detalle_venta (cantidad, precio_unitario, subtotal, venta_idventa, idproducto) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = ConfiguracionBaseDatos.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, detalle.getCantidad());
            stmt.setDouble(2, detalle.getPrecioUnitario());
            stmt.setDouble(3, detalle.getSubtotal());
            stmt.setInt(4, detalle.getVentaId());
            stmt.setInt(5, detalle.getProductoId());

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        detalle.setId(rs.getInt(1));
                    }
                }
                return true;
            }
            return false;

        } catch (SQLException e) {
            throw new RuntimeException("Error de BD al guardar el detalle de venta.", e);
        }
    }

    @Override
    public boolean update(DetalleVenta detalle) {
        String sql = "UPDATE detalle_venta SET cantidad=?, precio_unitario=?, subtotal=?, venta_idventa=?, idproducto=? WHERE iddetalle_venta=?";
        try (Connection conn = ConfiguracionBaseDatos.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, detalle.getCantidad());
            stmt.setDouble(2, detalle.getPrecioUnitario());
            stmt.setDouble(3, detalle.getSubtotal());
            stmt.setInt(4, detalle.getVentaId());
            stmt.setInt(5, detalle.getProductoId());
            stmt.setInt(6, detalle.getId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Error de BD al actualizar el detalle de venta.", e);
        }
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM detalle_venta WHERE iddetalle_venta=?";
        try (Connection conn = ConfiguracionBaseDatos.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Error de BD al eliminar el detalle de venta.", e);
        }
    }

    @Override
    public DetalleVenta findById(int id) {
        String sql = "SELECT iddetalle_venta, cantidad, precio_unitario, subtotal, venta_idventa, idproducto FROM detalle_venta WHERE iddetalle_venta=?";
        try (Connection conn = ConfiguracionBaseDatos.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    DetalleVenta d = new DetalleVenta();
                    d.setId(rs.getInt("iddetalle_venta"));
                    d.setCantidad(rs.getInt("cantidad"));
                    d.setPrecioUnitario(rs.getDouble("precio_unitario"));
                    d.setSubtotal(rs.getDouble("subtotal"));
                    d.setVentaId(rs.getInt("venta_idventa"));
                    d.setProductoId(rs.getInt("idproducto"));
                    return d;
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error de BD al buscar detalle de venta por ID.", e);
        }
        return null;
    }

    @Override
    public List<DetalleVenta> findByVenta(int ventaId) {
        List<DetalleVenta> lista = new ArrayList<>();
        String sql = "SELECT iddetalle_venta, cantidad, precio_unitario, subtotal, venta_idventa, idproducto FROM detalle_venta WHERE venta_idventa=?";
        try (Connection conn = ConfiguracionBaseDatos.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, ventaId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    DetalleVenta d = new DetalleVenta();
                    d.setId(rs.getInt("iddetalle_venta"));
                    d.setCantidad(rs.getInt("cantidad"));
                    d.setPrecioUnitario(rs.getDouble("precio_unitario"));
                    d.setSubtotal(rs.getDouble("subtotal"));
                    d.setVentaId(rs.getInt("venta_idventa"));
                    d.setProductoId(rs.getInt("idproducto"));
                    lista.add(d);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error de BD al buscar detalles por ID de venta.", e);
        }
        return lista;
    }

}