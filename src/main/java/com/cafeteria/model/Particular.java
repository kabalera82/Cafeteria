package com.cafeteria.model;

import jakarta.persistence.*;

@Entity
@Table(name = "particular")
@PrimaryKeyJoinColumn(name = "id_cliente")
public class Particular extends Cliente {

    public Particular() { super(); }

    @Override
    public String toString() {
        return "Particular[id=" + getIdCliente() + ", nombre=" + getNombre() + "]";
    }
}
