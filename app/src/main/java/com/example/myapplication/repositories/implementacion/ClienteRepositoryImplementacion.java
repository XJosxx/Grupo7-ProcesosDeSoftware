package com.example.myapplication.repositories.implementacion;

import com.example.myapplication.Configuracion.ConfiguracionBaseDatos;
import com.example.myapplication.entidades.Cliente;
import com.example.myapplication.repositories.ClienteRepository;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteRepositoryImplementacion implements ClienteRepository {



    @Override
    public boolean save(Cliente cliente) {
        String sql = "INSERT INTO cliente (nombre, dni) VALUES (?, ?)";
        try (Connection conn = ConfiguracionBaseDatos.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cliente.getNombre());
            stmt.setString(2, cliente.getDni());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Error de BD al guardar el cliente.", e);
        }
    }

    @Override
    public boolean update(Cliente cliente) {
        String sql = "UPDATE cliente SET nombre=? WHERE dni=?";
        try (Connection conn = ConfiguracionBaseDatos.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cliente.getNombre());
            stmt.setString(2, cliente.getDni());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Error de BD al actualizar el cliente.", e);
        }
    }

    @Override
    public boolean delete(String dni) {
        String sql = "DELETE FROM cliente WHERE dni=?";
        try (Connection conn = ConfiguracionBaseDatos.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, dni);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Error de BD al eliminar el cliente.", e);
        }
    }

    @Override
    public Cliente findByDni(String dni) {
        String sql = "SELECT nombre, dni FROM cliente WHERE dni=?";
        try (Connection conn = ConfiguracionBaseDatos.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, dni);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Cliente cliente = new Cliente();
                    cliente.setNombre(rs.getString("nombre"));
                    cliente.setDni(rs.getString("dni"));
                    return cliente;
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error de BD al buscar cliente por DNI.", e);
        }
        return null;
    }

    @Override
    public List<Cliente> findAll() {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT nombre, dni FROM cliente";

        try (Connection conn = ConfiguracionBaseDatos.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) { // rs incluido aquí

            while (rs.next()) {
                Cliente cliente = new Cliente();
                cliente.setNombre(rs.getString("nombre"));
                cliente.setDni(rs.getString("dni"));
                lista.add(cliente);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error de BD al obtener todos los clientes.", e);
        }
        return lista;
    }

    @Override
    public List<Cliente> findByNombre(String nombre) {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT nombre, dni FROM cliente WHERE nombre LIKE ?";

        try (Connection conn = ConfiguracionBaseDatos.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + nombre + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Cliente cliente = new Cliente();
                    cliente.setNombre(rs.getString("nombre"));
                    cliente.setDni(rs.getString("dni"));
                    lista.add(cliente);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error de BD al buscar clientes por nombre.", e);
        }
        return lista;
    }

    @Override
    public boolean existePorDni(String dni) {
        String sql = "SELECT COUNT(dni) FROM cliente WHERE dni=?";

        try (Connection conn = ConfiguracionBaseDatos.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, dni);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error de BD al verificar existencia por DNI.", e);
        }
        return false;
    }
}
