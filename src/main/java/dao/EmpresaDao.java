package dao;

import model.Empresa;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class EmpresaDao extends ClienteDao {

    public void create(Empresa empresa) throws SQLException {
        try {
            super.create(empresa);
            String sql = "INSERT INTO empresa (id_cliente, descuento) VALUES (?, ?)";
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, empresa.getIdCliente());
                ps.setDouble(2, empresa.getDescuento());
                ps.executeUpdate();
            }
            System.out.println("✅ Empresa insertada correctamente: " + empresa.getNombre());
        } catch (SQLException e) {
            System.out.println("❌ Error al insertar Empresa: " + e.getMessage());
            throw e;
        }
    }

    public void update(Empresa empresa) throws SQLException {
        String sql = "UPDATE empresa SET descuento = ? WHERE id_cliente = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDouble(1, empresa.getDescuento());
            ps.setString(2, empresa.getIdCliente());
            ps.executeUpdate();
            System.out.println("✅ Empresa actualizada correctamente: " + empresa.getNombre());
        } catch (SQLException e) {
            System.out.println("❌ Error al actualizar Empresa: " + e.getMessage());
            throw e;
        }
    }

    public void delete(Empresa empresa) throws SQLException {
        String sql = "DELETE FROM empresa WHERE id_cliente = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, empresa.getIdCliente());
            ps.executeUpdate();
            System.out.println("✅ Empresa eliminada correctamente: " + empresa.getNombre());
        } catch (SQLException e) {
            System.out.println("❌ Error al eliminar Empresa: " + e.getMessage());
            throw e;
        }
    }
}
