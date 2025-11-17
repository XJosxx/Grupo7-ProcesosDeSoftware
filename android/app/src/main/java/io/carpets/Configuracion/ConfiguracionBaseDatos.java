package io.carpets.Configuracion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConfiguracionBaseDatos {

    // Datos de conexión
    private static final String HOST = "gateway01.us-east-1.prod.aws.tidbcloud.com";
    private static final String PORT = "4000";
    private static final String DATABASE = "mydb";
    private static final String USER = "3tMoTzVSbiGDcvi.root";
    private static final String PASSWORD = "wlbblSelMCJHH813";

    private static Connection connection = null;

    // Obtener conexión
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                // Cargar driver de MySQL
                Class.forName("com.mysql.cj.jdbc.Driver");

                String url = "jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE
                        + "?useSSL=false&serverTimezone=UTC";

                connection = DriverManager.getConnection(url, USER, PASSWORD);
                System.out.println("Conexión a la base de datos establecida.");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                throw new SQLException("Driver JDBC no encontrado.");
            }
        }
        return connection;
    }

    // Cerrar conexión
    public static void cerrarConexion() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Conexión cerrada correctamente.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}