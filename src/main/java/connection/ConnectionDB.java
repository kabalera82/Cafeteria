package connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionDB {

    private static final String URL = "jdbc:mysql://localhost:3306/cafeteria?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USER = "user";
    private static final String PASSWORD = "0000";


    public static Connection getConnection() {
        Connection con = null;
        try {
            con = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Conectado correctamente");
        } catch (SQLException e) {
            System.out.println("Error de Conexión: " + e.getMessage());
        }
        return con;
    }
}
