package model;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Representa un producto disponible en la cafetería.
 */
public class Producto {

    // Identificador técnico único (UUID)
    private String idProducto;

    // Atributos del producto
    private String descripcion;    // nombre o descripción breve del producto
    private int stock;             // cantidad disponible
    private double precio;         // precio unitario

    // Datos de control
    private LocalDateTime fechaAlta;
    private boolean activo;

    // Constructor por defecto
    public Producto() {
        this.idProducto = UUID.randomUUID().toString();
        this.fechaAlta = LocalDateTime.now();
        this.activo = true;
    }

    // Constructor completo
    public Producto(String descripcion, int stock, double precio) {
        this();
        this.descripcion = descripcion;
        this.stock = stock;
        this.precio = precio;
    }

    // Getters y Setters
    public String getIdProducto() {
        return idProducto;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public LocalDateTime getFechaAlta() {
        return fechaAlta;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    @Override
    public String toString() {
        return "Producto{" +
                "idProducto='" + idProducto + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", stock=" + stock +
                ", precio=" + precio +
                ", activo=" + activo +
                '}';
    }
}

