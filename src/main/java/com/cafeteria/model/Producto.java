package com.cafeteria.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "producto")
public class Producto {

    @Id
    @Column(name = "id_producto", length = 36)
    private String idProducto;

    @Column(nullable = false, length = 150)
    private String descripcion;

    @Column(nullable = false)
    private int stock;

    @Column(nullable = false, precision = 10, scale = 2)
    private double precio;

    @Column(name = "fecha_alta")
    private LocalDateTime fechaAlta;

    @Column(nullable = false)
    private boolean activo;

    public Producto() {
        this.idProducto = UUID.randomUUID().toString();
        this.fechaAlta = LocalDateTime.now();
        this.activo = true;
    }

    public String getIdProducto() { return idProducto; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }
    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }
    public LocalDateTime getFechaAlta() { return fechaAlta; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    @Override
    public String toString() {
        return "Producto[id=" + idProducto + ", descripcion=" + descripcion + ", precio=" + precio + "]";
    }
}
