package com.cafeteria.model;

import jakarta.persistence.*;

@Entity
@Table(name = "linea_ticket")
public class LineaTicket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_linea")
    private int idLinea;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "num_ticket", nullable = false)
    private CabeceraTicket cabeceraTicket;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto producto;

    @Column(nullable = false)
    private int cantidad;

    @Column(name = "precio_unitario", nullable = false, precision = 10, scale = 2)
    private double precioUnitario;

    @Column(nullable = false, precision = 10, scale = 2)
    private double subtotal;

    public LineaTicket() {}

    public LineaTicket(Producto producto, int cantidad) {
        this.producto = producto;
        this.cantidad = cantidad;
        this.precioUnitario = producto.getPrecio();
        this.subtotal = precioUnitario * cantidad;
    }

    public int getIdLinea() { return idLinea; }
    public CabeceraTicket getCabeceraTicket() { return cabeceraTicket; }
    public void setCabeceraTicket(CabeceraTicket cabeceraTicket) { this.cabeceraTicket = cabeceraTicket; }
    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }
    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
        this.subtotal = this.precioUnitario * cantidad;
    }
    public double getPrecioUnitario() { return precioUnitario; }
    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }

    @Override
    public String toString() {
        return "Linea[id=" + idLinea + ", producto=" + (producto != null ? producto.getDescripcion() : "null")
                + ", cantidad=" + cantidad + ", subtotal=" + subtotal + "]";
    }
}
