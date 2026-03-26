package com.cafeteria.model;

import jakarta.persistence.*;

@Entity
@Table(name = "empresa")
@PrimaryKeyJoinColumn(name = "id_cliente")
public class Empresa extends Cliente {

    @Column(nullable = false, precision = 5, scale = 2)
    private double descuento;

    public Empresa() { super(); }

    public double getDescuento() { return descuento; }
    public void setDescuento(double descuento) { this.descuento = descuento; }

    @Override
    public String toString() {
        return "Empresa[id=" + getIdCliente() + ", nombre=" + getNombre() + ", descuento=" + descuento + "]";
    }
}
