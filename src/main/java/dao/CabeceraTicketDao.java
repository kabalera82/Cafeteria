package dao;

import java.sql.*;


import connection.ConnectionDB;
import model.CabeceraTicket;


public class CabeceraTicketDao {
    private Connection con;

    public CabeceraTicketDao() {
        this.con = ConnectionDB.getConnection();
    }

    public void create(CabeceraTicket cabeceraTicket) throws SQLException {

        String sql = "INSERT INTO cabecera_ticket (num_ticket, fecha, total, id_cliente, id_camarero) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, cabeceraTicket.getNumTicket());
            ps.setTimestamp(2, Timestamp.valueOf(cabeceraTicket.getFecha())); // ✅ convierte LocalDateTime
            ps.setDouble(3, cabeceraTicket.getTotal());
            ps.setString(4, cabeceraTicket.getCliente().getIdCliente());
            ps.setInt(5, cabeceraTicket.getCamarero().getIdCamarero());
            ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Error al insertar Cabecera del ticket: " + e.getMessage());
            throw e;
        }
    }
}