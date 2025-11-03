import connection.ConnectionDB;
import dao.*;
import model.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

public class Main {

    public static void main(String[] args) {

        try {
            // 1️⃣ Verificar conexión
            Connection con = ConnectionDB.getConnection();
            try (Statement st = con.createStatement()) {
                st.executeUpdate("DELETE FROM cabecera_ticket");
                st.executeUpdate("DELETE FROM linea_ticket");
                st.executeUpdate("DELETE FROM camarero");
                st.executeUpdate("DELETE FROM empresa");
                st.executeUpdate("DELETE FROM cliente");
                System.out.println("🧹 Tablas limpiadas antes de insertar nuevos datos.");
            }

            if (con != null) System.out.println("🎯 Conexión verificada y lista para usarse.");

            // 2️⃣ Crear camarero
            Camarero camarero = new Camarero(true, LocalDate.now(), "55555555Z", "Juan López");
            CamareroDao camareroDao = new CamareroDao();
            camareroDao.create(camarero);
            System.out.println("✅ Camarero insertado: " + camarero);

            // 3️⃣ Crear cliente tipo Empresa
            Empresa empresa = new Empresa();
            empresa.setNumeroCliente(2001);
            empresa.setNombre("Distribuciones Atlas");
            empresa.setPrimerApellido("S.L.");
            empresa.setDireccion("Avenida del Comercio, 45");
            empresa.setTelefono("912345678");
            empresa.setEmail("info@atlas.com");
            empresa.setDescuento(5.0);

            EmpresaDao empresaDao = new EmpresaDao();
            empresaDao.create(empresa);
            System.out.println("✅ Empresa insertada: " + empresa);

            // 4️⃣ Crear productos
            ProductoDao productoDao = new ProductoDao();

            Producto cafe = new Producto("Café Solo", 100, 1.20);
            Producto croissant = new Producto("Croissant", 50, 1.80);
            Producto zumo = new Producto("Zumo de Naranja", 40, 2.00);

            productoDao.create(cafe);
            productoDao.create(croissant);
            productoDao.create(zumo);
            System.out.println("✅ Productos insertados correctamente.");

            // 5️⃣ Crear cabecera del ticket
            CabeceraTicket ticket = new CabeceraTicket();
            ticket.setNumTicket("TCK-0002");
            ticket.setCliente(empresa);
            ticket.setCamarero(camarero);

            // 6️⃣ Crear líneas del ticket
            LineaTicket linea1 = new LineaTicket(1, cafe, 2);       // 2 cafés
            LineaTicket linea2 = new LineaTicket(2, croissant, 1);  // 1 croissant
            LineaTicket linea3 = new LineaTicket(3, zumo, 1);       // 1 zumo

            ticket.getLineas().add(linea1);
            ticket.getLineas().add(linea2);
            ticket.getLineas().add(linea3);

            // 7️⃣ Calcular total (sumando subtotales)
            double total = 0.0;
            for (LineaTicket linea : ticket.getLineas()) {
                total += linea.getSubtotal();
            }
            ticket.setTotal(total);

            // 8️⃣ Insertar cabecera
            CabeceraTicketDao cabeceraDao = new CabeceraTicketDao();
            cabeceraDao.create(ticket);
            System.out.println("✅ Ticket insertado: " + ticket);

            // 9️⃣ Insertar líneas asociadas
            LineaTicketDAO lineaDao = new LineaTicketDAO();
            for (LineaTicket linea : ticket.getLineas()) {
                lineaDao.create(linea, ticket.getNumTicket());
            }
            System.out.println("✅ Líneas del ticket insertadas correctamente.");

            // 10️⃣ Mostrar resumen
            System.out.println("\n🧾 RESUMEN TICKET:");
            System.out.println(ticket);
            for (LineaTicket l : ticket.getLineas()) {
                System.out.println("   → " + l);
            }
            System.out.printf("💰 TOTAL FINAL: %.2f €%n", total);

        } catch (SQLException e) {
            System.out.println("❌ Error SQL: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("⚠️ Error inesperado: " + e.getMessage());
        }
    }
}
