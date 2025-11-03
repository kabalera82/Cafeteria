package dao;

import model.Particular;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ParticularDao extends ClienteDao {

    public void create(Particular particular) throws SQLException {
        try {
            // 1️⃣ Inserta primero en la tabla cliente
            super.create(particular);

            // 2️⃣ Inserta después en la tabla particular
            String sql = "INSERT INTO particular (id_cliente) VALUES (?)";

            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, particular.getIdCliente());
                ps.executeUpdate();
            }

            System.out.println("✅ Particular insertado correctamente: " + particular.getNombre());
        } catch (SQLException e) {
            System.out.println("❌ Error al insertar Particular: " + e.getMessage());
            throw e;
        }
    }
}
