package src.main.java;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {

        String url = "jdbc:mysql://localhost:3306/cafeteria";  // conexión a tu contenedor Docker
        String user = "user";  // el que definiste en docker-compose.yml
        String password = "0000";  // la contraseña del compose

        System.out.println("☕ Intentando conectar con la base de datos...");

        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            System.out.println("✅ Conexión exitosa a la base de datos 'cafeteria'!");
        } catch (SQLException e) {
            System.out.println("❌ Error al conectar con la base de datos:");
            e.printStackTrace();
        }
    }
}