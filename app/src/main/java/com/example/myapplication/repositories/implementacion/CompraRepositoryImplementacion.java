package com.example.myapplication.repositories.implementacion;

import com.example.myapplication.Configuracion.ConfiguracionBaseDatos;
import com.example.myapplication.entidades.Compra;
import com.example.myapplication.repositories.CompraRepository;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación del repositorio para la entidad Compra.
 * Se encarga de las operaciones CRUD en la base de datos.
 * Utiliza un pool de conexiones y propaga errores de BD como RuntimeException.
 */
public class CompraRepositoryImplementacion implements CompraRepository {

    @Override
    public boolean save(Compra compra) {
        String sql = "INSERT INTO compra (descripcion, monto) VALUES (?, ?)";
        try (Connection conn = ConfiguracionBaseDatos.getConnection();
             // Solicitamos que se devuelvan las claves generadas
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, compra.getDescripcion());
            stmt.setDouble(2, compra.getMonto());

            int rows = stmt.executeUpdate();

            if (rows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        // Asignamos el ID generado al objeto Compra
                        compra.setId(rs.getInt(1));
                    }
                }
                return true;
            }
            return false;

        } catch (SQLException e) {
            throw new RuntimeException("Error de base de datos al guardar la compra.", e);
        }
    }

    @Override
    public boolean update(Compra compra) {
        String sql = "UPDATE compra SET descripcion=?, monto=? WHERE idcompra=?";
        try (Connection conn = ConfiguracionBaseDatos.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, compra.getDescripcion());
            stmt.setDouble(2, compra.getMonto());
            stmt.setInt(3, compra.getId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Error de base de datos al actualizar la compra.", e);
        }
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM compra WHERE idcompra=?";
        try (Connection conn = ConfiguracionBaseDatos.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Error de base de datos al eliminar la compra.", e);
        }
    }

    @Override
    public Compra findById(int id) {
        String sql = "SELECT idcompra, descripcion, monto FROM compra WHERE idcompra=?";
        try (Connection conn = ConfiguracionBaseDatos.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Compra c = new Compra();
                    c.setId(rs.getInt("idcompra"));
                    c.setDescripcion(rs.getString("descripcion"));
                    c.setMonto(rs.getDouble("monto"));
                    return c;
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error de base de datos al buscar la compra por ID.", e);
        }
        return null;
    }

    @Override
    public List<Compra> findAll() {
        List<Compra> lista = new ArrayList<>();
        String sql = "SELECT idcompra, descripcion, monto FROM compra";

        try (Connection conn = ConfiguracionBaseDatos.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Compra c = new Compra();
                c.setId(rs.getInt("idcompra"));
                c.setDescripcion(rs.getString("descripcion"));
                c.setMonto(rs.getDouble("monto"));
                lista.add(c);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error de base de datos al obtener todas las compras.", e);
        }
        return lista;
    }
}