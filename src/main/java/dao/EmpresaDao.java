package dao;

import model.Empresa;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class EmpresaDao extends ClienteDao {

    public void create(Empresa empresa) throws SQLException {
        try {
            // 1️⃣ Inserta primero en la tabla cliente
            super.create(empresa);

            // 2️⃣ Inserta después en la tabla empresa
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
}
