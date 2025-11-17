package com.example.myapplication.repositories.implementacion;

import com.example.myapplication.Configuracion.ConfiguracionBaseDatos;
import com.example.myapplication.entidades.DetalleCompra;
import com.example.myapplication.repositories.DetalleCompraRepository;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DetalleCompraRepositoryImplementacion implements DetalleCompraRepository {


    @Override
    public boolean save(DetalleCompra detalle) {

        String sql = "INSERT INTO `detalle-compra` (unidades, producto_idproducto, compra_idcompra, precio_unitario) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConfiguracionBaseDatos.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, detalle.getUnidades());
            stmt.setInt(2, detalle.getProductoId());
            stmt.setInt(3, detalle.getCompraId());
            stmt.setDouble(4, detalle.getPrecioUnitario());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Error de BD al guardar el detalle de la compra.", e);
        }
    }

    @Override
    public boolean update(DetalleCompra detalle) {

        String sql = "UPDATE `detalle-compra` SET unidades=?, producto_idproducto=?, compra_idcompra=?, precio_unitario=? WHERE iddetalle_compra=?";
        try (Connection conn = ConfiguracionBaseDatos.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, detalle.getUnidades());
            stmt.setInt(2, detalle.getProductoId());
            stmt.setInt(3, detalle.getCompraId());
            stmt.setDouble(4, detalle.getPrecioUnitario());
            stmt.setInt(5, detalle.getId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Error de BD al actualizar el detalle de la compra.", e);
        }
    }

    @Override
    public boolean delete(int id) {

        String sql = "DELETE FROM `detalle-compra` WHERE iddetalle_compra=?";
        try (Connection conn = ConfiguracionBaseDatos.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Error de BD al eliminar el detalle de la compra.", e);
        }
    }

    @Override
    public DetalleCompra findById(int id) {

        String sql = "SELECT * FROM `detalle-compra` WHERE iddetalle_compra=?";
        try (Connection conn = ConfiguracionBaseDatos.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    DetalleCompra d = new DetalleCompra();
                    d.setId(rs.getInt("iddetalle_compra"));
                    d.setUnidades(rs.getInt("unidades"));
                    d.setProductoId(rs.getInt("producto_idproducto"));
                    d.setCompraId(rs.getInt("compra_idcompra"));
                    d.setPrecioUnitario(rs.getDouble("precio_unitario"));
                    return d;
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error de BD al buscar detalle por ID.", e);
        }
        return null;
    }

    @Override
    public List<DetalleCompra> findByCompraId(int compraId) {
        List<DetalleCompra> lista = new ArrayList<>();
        String sql = "SELECT * FROM `detalle-compra` WHERE compra_idcompra=?";
        try (Connection conn = ConfiguracionBaseDatos.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, compraId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    DetalleCompra d = new DetalleCompra();
                    d.setId(rs.getInt("iddetalle_compra"));
                    d.setUnidades(rs.getInt("unidades"));
                    d.setProductoId(rs.getInt("producto_idproducto"));
                    d.setCompraId(rs.getInt("compra_idcompra"));
                    d.setPrecioUnitario(rs.getDouble("precio_unitario"));
                    lista.add(d);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error de BD al buscar detalles por ID de compra.", e);
        }
        return lista;
    }

    @Override
    public List<DetalleCompra> findAll() {

        List<DetalleCompra> lista = new ArrayList<>();
        String sql = "SELECT * FROM `detalle-compra`";

        try (Connection conn = ConfiguracionBaseDatos.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                DetalleCompra d = new DetalleCompra();
                d.setId(rs.getInt("iddetalle_compra"));
                d.setUnidades(rs.getInt("unidades"));
                d.setProductoId(rs.getInt("producto_idproducto"));
                d.setCompraId(rs.getInt("compra_idcompra"));
                d.setPrecioUnitario(rs.getDouble("precio_unitario"));
                lista.add(d);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error de BD al obtener todos los detalles de compra.", e);
        }
        return lista;
    }
}