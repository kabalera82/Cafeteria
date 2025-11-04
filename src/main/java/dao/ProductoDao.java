package dao;

import connection.ConnectionDB;
import model.Producto;
import java.sql.*;

public class ProductoDao {

    private final Connection con;

    public ProductoDao() {
        this.con = ConnectionDB.getConnection();
    }

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
            System.out.println("✅ Producto insertado correctamente: " + producto.getDescripcion());
        } catch (SQLException e) {
            System.out.println("❌ Error al insertar producto: " + e.getMessage());
            throw e;
        }
    }

    public void update(Producto producto) throws SQLException {
        String sql = "UPDATE producto " +
                "SET descripcion = ?, " +
                "stock = ?, " +
                "precio = ?, " +
                "fecha_alta = ?, " +
                "activo = ? " +
                "WHERE id_producto = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, producto.getDescripcion());
            ps.setInt(2, producto.getStock());
            ps.setDouble(3, producto.getPrecio());
            ps.setTimestamp(4, Timestamp.valueOf(producto.getFechaAlta()));
            ps.setBoolean(5, producto.isActivo());
            ps.setString(6, producto.getIdProducto());
            ps.executeUpdate();
            System.out.println("✅ Producto actualizado correctamente: " + producto.getDescripcion());
        } catch (SQLException e) {
            System.out.println("❌ Error al actualizar producto: " + e.getMessage());
            throw e;
        }
    }

    public void delete (Producto producto) throws SQLException {
        String sql = "DELETE FROM producto WHERE id_producto = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, producto.getIdProducto());
            ps.executeUpdate();
            System.out.println("✅ Producto eliminado correctamente: " + producto.getDescripcion());
        } catch (SQLException e) {
            System.out.println("❌ Error al eliminar producto: " + e.getMessage());
            throw e;
        }
    }
}
