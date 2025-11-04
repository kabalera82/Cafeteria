package app;

import dao.ProductoDao;
import model.Producto;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        ProductoDao productoDao = new ProductoDao();

        while (true) {
            System.out.println("\n====== MENÚ PRINCIPAL ======");
            System.out.println("1. Insertar producto");
            System.out.println("2. Actualizar producto");
            System.out.println("3. Eliminar producto");
            System.out.println("4. Salir");
            System.out.print("Selecciona una opción: ");
            int opcion = sc.nextInt();
            sc.nextLine(); // limpiar salto de línea

            try {
                switch (opcion) {
                    case 1 -> {
                        Producto nuevo = new Producto();
                        System.out.print("Descripción: ");
                        nuevo.setDescripcion(sc.nextLine());
                        System.out.print("Stock: ");
                        nuevo.setStock(sc.nextInt());
                        System.out.print("Precio: ");
                        nuevo.setPrecio(sc.nextDouble());
                        nuevo.setFechaAlta(LocalDateTime.now());
                        nuevo.setActivo(true);
                        productoDao.create(nuevo);
                    }
                    case 2 -> {
                        Producto actualizar = new Producto();
                        System.out.print("ID del producto a actualizar: ");
                        actualizar.setIdProducto(sc.nextLine());
                        System.out.print("Nueva descripción: ");
                        actualizar.setDescripcion(sc.nextLine());
                        System.out.print("Nuevo stock: ");
                        actualizar.setStock(sc.nextInt());
                        System.out.print("Nuevo precio: ");
                        actualizar.setPrecio(sc.nextDouble());
                        actualizar.setFechaAlta(LocalDateTime.now());
                        actualizar.setActivo(true);
                        productoDao.update(actualizar);
                    }
                    case 3 -> {
                        Producto eliminar = new Producto();
                        System.out.print("ID del producto a eliminar: ");
                        eliminar.setIdProducto(sc.nextLine());
                        productoDao.delete(eliminar);
                    }
                    case 4 -> {
                        System.out.println("Saliendo del programa...");
                        sc.close();
                        return;
                    }
                    default -> System.out.println("Opción no válida.");
                }
            } catch (SQLException e) {
                System.out.println("❌ Error en la operación: " + e.getMessage());
            }
        }
    }
}
