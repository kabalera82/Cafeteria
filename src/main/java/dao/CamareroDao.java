package dao;

import model.Camarero;

import java.sql.*;
import connection.ConnectionDB;

public class CamareroDao {

    private Connection con;

    public CamareroDao() {
        con = ConnectionDB.getConnection();
    }

    public int create(Camarero camarero) throws SQLException {
        String sql = "INSERT INTO camarero (nif, nombre, fecha_incorporacion, activo) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, camarero.getNif());
            ps.setString(2, camarero.getNombre());
            ps.setDate(3, Date.valueOf(camarero.getFechaIncorporacion()));
            ps.setBoolean(4, camarero.isActivo());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    camarero.setIdCamarero(id);
                    System.out.println("✅ Camarero insertado con ID: " + id);
                    return id;
                }
            }
        }
        return -1;
    }
}
