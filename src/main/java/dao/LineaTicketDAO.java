package dao;

import connection.ConnectionDB;
import model.LineaTicket;
import java.sql.*;

public class LineaTicketDAO {

    private final Connection con;

    public LineaTicketDAO() {
        this.con = ConnectionDB.getConnection();
    }

    public void create(LineaTicket linea, String numTicket) throws SQLException {
        String sql = "INSERT INTO linea_ticket (num_ticket, producto, cantidad, precio_unitario, subtotal) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, numTicket);
            ps.setString(2, linea.getProducto().getDescripcion());
            ps.setInt(3, linea.getCantidad());
            ps.setDouble(4, linea.getProducto().getPrecio());
            ps.setDouble(5, linea.getSubtotal());
            ps.executeUpdate();
            System.out.println("✅ Línea de ticket insertada correctamente");
        } catch (SQLException e) {
            System.out.println("❌ Error al insertar línea de ticket: " + e.getMessage());
            throw e;
        }
    }

    public void update(LineaTicket linea, String numTicket) throws SQLException {
        String sql = "UPDATE linea_ticket " +
                "SET producto = ?, " +
                "cantidad = ?, " +
                "precio_unitario = ?, " +
                "subtotal = ? " +
                "WHERE num_ticket = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, linea.getProducto().getDescripcion());
            ps.setInt(2, linea.getCantidad());
            ps.setDouble(3, linea.getProducto().getPrecio());
            ps.setDouble(4, linea.getSubtotal());
            ps.setString(5, numTicket);
            ps.executeUpdate();
            System.out.println("✅ Línea de ticket actualizada correctamente");
        } catch (SQLException e) {
            System.out.println("❌ Error al actualizar línea de ticket: " + e.getMessage());
            throw e;
        }
    }

    public void delete(String numTicket) throws SQLException {
        String sql = "DELETE FROM linea_ticket WHERE num_ticket = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, numTicket);
            ps.executeUpdate();
            System.out.println("✅ Líneas de ticket eliminadas correctamente para ticket: " + numTicket);
        } catch (SQLException e) {
            System.out.println("❌ Error al eliminar líneas de ticket: " + e.getMessage());
            throw e;
        }
    }
}
