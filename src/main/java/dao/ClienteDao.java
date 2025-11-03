package dao;

import connection.ConnectionDB;
import model.Cliente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

public abstract class ClienteDao {

    protected Connection con;

    public ClienteDao() {
        this.con = ConnectionDB.getConnection();
    }

    public void create(Cliente cliente) throws SQLException {
        String sql = "INSERT INTO cliente (" +
                "id_cliente, numero_cliente, nombre, primer_apellido, segundo_apellido, " +
                "direccion, telefono, email, fecha_alta, activo" +
                ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, cliente.getIdCliente());
            ps.setInt(2, cliente.getNumeroCliente());
            ps.setString(3, cliente.getNombre());
            ps.setString(4, cliente.getPrimerApellido());
            ps.setString(5, cliente.getSegundoApellido());
            ps.setString(6, cliente.getDireccion());
            ps.setString(7, cliente.getTelefono());
            ps.setString(8, cliente.getEmail());
            ps.setTimestamp(9, Timestamp.valueOf(cliente.getFechaAlta()));
            ps.setBoolean(10, cliente.isActivo());
            ps.executeUpdate();
            System.out.println("✅ Cliente insertado correctamente: " + cliente.getNombre());
        } catch (SQLException e) {
            System.out.println("❌ Error al insertar cliente: " + e.getMessage());
            throw e;
        }
    }
}
