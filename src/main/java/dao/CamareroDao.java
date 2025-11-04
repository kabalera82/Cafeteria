package dao;

import model.CabeceraTicket;
import model.Camarero;

import java.sql.*;
import connection.ConnectionDB;

public class CamareroDao {

    private Connection con;

    public CamareroDao() {
        con = ConnectionDB.getConnection();
    }

    public void create(Camarero camarero) throws SQLException {
        String sql = "INSERT INTO camarero (nif, nombre, fecha_incorporacion, activo) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, camarero.getNif());
            ps.setString(2, camarero.getNombre());
            ps.setDate(3, Date.valueOf(camarero.getFechaIncorporacion()));
            ps.setBoolean(4, camarero.isActivo());

            ps.executeUpdate();

            // Recuperar el ID generado automáticamente
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int idGenerado = rs.getInt(1);
                    camarero.setIdCamarero(idGenerado);
                    System.out.println("✅ Camarero insertado con ID: " + idGenerado);
                }
            }
        } catch (SQLException e) {
            System.out.println("❌ Error al insertar al empleado: " + e.getMessage());
            throw e;
        }
    }

    public void update(Camarero camarero) throws SQLException {
        String sql = "UPDATE camarero" +
                "SET nif = ?," +
                "nombre = ?," +
                "fecha = ?," +
                "isActivo = ?" +
                "WHERE id_camarero = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, camarero.getNif());
            ps.setString(2, camarero.getNombre());
            ps.setDate(3, Date.valueOf(camarero.getFechaIncorporacion()));
            ps.setBoolean(4, camarero.isActivo());
            ps.setInt(5, camarero.getIdCamarero());

            ps.executeUpdate();

            System.out.println("✅ Camarero actualizado correctamente: ");
        } catch (SQLException e) {
            System.out.println("❌ Error al actualizar el camarero: " + e.getMessage());
            throw e;
        }
    }

     public void delete (Camarero camarero) throws SQLException {
         String sql = "DELETE FROM camarero WHERE id_camarero = ?";

         try (PreparedStatement ps = con.prepareStatement(sql)) {
             ps.setInt(1, camarero.getIdCamarero());
             ps.executeUpdate();
             System.out.println("✅ Camarero eliminad correctamente: " + camarero.getIdCamarero());
         } catch (SQLException e) {
             System.out.println("❌ Error al eliminar camarero: " + e.getMessage());
             throw e;
         }
     }


}
