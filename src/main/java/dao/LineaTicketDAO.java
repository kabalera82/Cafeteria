package dao;

import connection.ConnectionDB;
import model.LineaTicket;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LineaTicketDAO {

    private final Connection con;

    public LineaTicketDAO () {
        this.con = ConnectionDB.getConnection();
    }

    public void create(LineaTicket linea, String numTicket) throws SQLException {
        String sql = "INSERT INTO linea_ticket (num_ticket, producto, cantidad, precio_unitario, subtotal) VALUES (?, ?, ?, ?, ?)";

        try(PreparedStatement ps = con.prepareStatement(sql)){
        ps.setString(1, numTicket);
        ps.setString(2, linea.getProducto().getDescripcion());
        ps.setInt(3, linea.getCantidad());
        ps.setDouble(4, linea.getProducto().getPrecio());
        ps.setDouble(5, linea.getSubtotal());
        ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Error al insertar LineaTicket: " + e.getMessage());
            throw e;
        }
    }


}