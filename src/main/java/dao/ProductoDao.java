package dao;

import connection.ConnectionDB;
import model.Producto;

import java.sql.*;

public class ProductoDao {

    private final Connection con;

    public ProductoDao() {
        this.con = ConnectionDB.getConnection();
    }

    // INSERTAR PRODUCTO
    public void create(Producto producto) throws SQLException {
        String sql = "INSERT INTO producto (" +
                "id_producto, descripcion, stock, precio, fecha_alta, activo" +
                ") VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, producto.getIdProducto());
            ps.setString(2, producto.getDescripcion());
            ps.setInt(3, producto.getStock());
            ps.setDouble(4, producto.getPrecio());
            ps.setTimestamp(5, Timestamp.valueOf(producto.getFechaAlta()));
            ps.setBoolean(6, producto.isActivo());

            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error al insertar producto: " + e.getMessage());
            throw e;
        }
    }
}