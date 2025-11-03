import connection.ConnectionDB;


import java.sql.DriverManager;
import java.sql.SQLException;

import static connection.ConnectionDB.getConnection;

public class Main {
    public static void main(String[] args) throws SQLException  {

        getConnection();
    }
}