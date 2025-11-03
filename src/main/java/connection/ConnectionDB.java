package connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionDB {

    private static final String URL ="jdbc:mysql://localhost:3306/Cafeteria";
    private static final String USER = "root";
    private static final String PASSWORD = "221182";

    //La llamada se hace a la clase Connection especifica del paquete Mysql
    public static Connection getConnection() {
        // Clase especifica para conectarse a mysql
        Connection con = null;
        try {
            con = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Conectado correctamente");
        } catch (SQLException e) {
            System.out.println("Error de Conexion: " + e.getMessage());
        }
        return con;
    }
}
