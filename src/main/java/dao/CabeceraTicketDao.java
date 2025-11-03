package dao;

import DataBase.DBConnection;

import javax.swing.plaf.nimbus.State;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import connection.ConnectionDB;
import model.CabeceraTicket;

public class CabeceraTicketDao {
    private Connection con;

    public CabeceraTicketDao(){
        con = ConnectionDB.getConnection();
    }

    public void create (CabeceraTicket cabeceraTicket) throws SQLException {
        String sql = "INSERT INTO cabecera_ticket ("
    }
}
