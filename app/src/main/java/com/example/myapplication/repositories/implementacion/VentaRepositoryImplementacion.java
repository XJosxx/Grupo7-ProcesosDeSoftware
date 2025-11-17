package com.example.myapplication.repositories.implementacion;

import com.example.myapplication.Configuracion.ConfiguracionBaseDatos;
import com.example.myapplication.entidades.Venta;
import com.example.myapplication.repositories.VentaRepository;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class VentaRepositoryImplementacion implements VentaRepository {

    @Override
    public boolean save(Venta venta) {

        String sql = "INSERT INTO venta (fecha, monto, descripcion, cliente_dni, vendedor_idvendedor) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = ConfiguracionBaseDatos.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setDate(1, new java.sql.Date(venta.getFecha().getTime()));
            stmt.setDouble(2, venta.getMonto());
            stmt.setString(3, venta.getDescripcion());
            stmt.setString(4, venta.getClienteDni());
            stmt.setInt(5, venta.getVendedorId());

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        venta.setId(rs.getInt(1));
                    }
                }
                return true;
            }
            return false;

        } catch (SQLException e) {
            throw new RuntimeException("Error de BD al guardar la venta.", e);
        }
    }

    @Override
    public boolean update(Venta venta) {
        String sql = "UPDATE venta SET fecha=?, monto=?, descripcion=?, cliente_dni=?, vendedor_idvendedor=? WHERE idventa=?";
        try (Connection conn = ConfiguracionBaseDatos.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, new java.sql.Date(venta.getFecha().getTime()));
            stmt.setDouble(2, venta.getMonto());
            stmt.setString(3, venta.getDescripcion());
            stmt.setString(4, venta.getClienteDni());
            stmt.setInt(5, venta.getVendedorId());
            stmt.setInt(6, venta.getId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Error de BD al actualizar la venta.", e);
        }
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM venta WHERE idventa=?";
        try (Connection conn = ConfiguracionBaseDatos.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Error de BD al eliminar la venta.", e);
        }
    }

    @Override
    public Venta findById(int id) {
        String sql = "SELECT * FROM venta WHERE idventa=?";
        try (Connection conn = ConfiguracionBaseDatos.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Venta v = new Venta();
                    v.setId(rs.getInt("idventa"));
                    v.setFecha(rs.getDate("fecha"));
                    v.setMonto(rs.getDouble("monto"));
                    v.setDescripcion(rs.getString("descripcion"));
                    v.setClienteDni(rs.getString("cliente_dni"));
                    v.setVendedorId(rs.getInt("vendedor_idvendedor"));
                    return v;
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error de BD al buscar venta por ID.", e);
        }
        return null;
    }

    @Override
    public List<Venta> findAll() {
        List<Venta> lista = new ArrayList<>();
        String sql = "SELECT * FROM venta";

        try (Connection conn = ConfiguracionBaseDatos.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Venta v = new Venta();
                v.setId(rs.getInt("idventa"));
                v.setFecha(rs.getDate("fecha"));
                v.setMonto(rs.getDouble("monto"));
                v.setDescripcion(rs.getString("descripcion"));
                v.setClienteDni(rs.getString("cliente_dni"));
                v.setVendedorId(rs.getInt("vendedor_idvendedor"));
                lista.add(v);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error de BD al obtener todas las ventas.", e);
        }
        return lista;
    }

    @Override
    public List<Venta> findByCliente(String dniCliente) {
        List<Venta> lista = new ArrayList<>();
        String sql = "SELECT * FROM venta WHERE cliente_dni=?";
        try (Connection conn = ConfiguracionBaseDatos.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, dniCliente);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Venta v = new Venta();
                    v.setId(rs.getInt("idventa"));
                    v.setFecha(rs.getDate("fecha"));
                    v.setMonto(rs.getDouble("monto"));
                    v.setDescripcion(rs.getString("descripcion"));
                    v.setClienteDni(rs.getString("cliente_dni"));
                    v.setVendedorId(rs.getInt("vendedor_idvendedor"));
                    lista.add(v);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error de BD al buscar ventas por DNI de cliente.", e);
        }
        return lista;
    }

    @Override
    public void registrarProductoNoEncontrado(Integer idProductoSolicitado, String nombreProductoSolicitado, Integer vendedorId) {
        String sql = "INSERT INTO log_producto_no_encontrado (id_producto_solicitado, nombre_producto_solicitado, fecha_solicitud, vendedor_id) VALUES (?, ?, NOW(), ?)";

        try (Connection conn = ConfiguracionBaseDatos.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Manejo de valores nulos para los IDs
            if (idProductoSolicitado != null) {
                stmt.setInt(1, idProductoSolicitado);
            } else {
                stmt.setNull(1, Types.INTEGER);
            }

            stmt.setString(2, nombreProductoSolicitado);

            if (vendedorId != null) {
                stmt.setInt(3, vendedorId);
            } else {
                stmt.setNull(3, Types.INTEGER);
            }

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error de BD al registrar producto no encontrado en log.", e);
        }
    }
}