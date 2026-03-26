package com.cafeteria.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cabecera_ticket")
public class CabeceraTicket {

    @Id
    @Column(name = "num_ticket", length = 50)
    private String numTicket;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @Column(nullable = false, precision = 10, scale = 2)
    private double total;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente")
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_camarero")
    private Camarero camarero;

    @OneToMany(mappedBy = "cabeceraTicket", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LineaTicket> lineas = new ArrayList<>();

    public CabeceraTicket() {
        this.fecha = LocalDateTime.now();
    }

    public void agregarLinea(LineaTicket linea) {
        linea.setCabeceraTicket(this);
        lineas.add(linea);
        recalcularTotal();
    }

    public void recalcularTotal() {
        this.total = lineas.stream().mapToDouble(LineaTicket::getSubtotal).sum();
    }

    public String getNumTicket() { return numTicket; }
    public void setNumTicket(String numTicket) { this.numTicket = numTicket; }
    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }
    public Camarero getCamarero() { return camarero; }
    public void setCamarero(Camarero camarero) { this.camarero = camarero; }
    public List<LineaTicket> getLineas() { return lineas; }

    @Override
    public String toString() {
        return "Ticket[num=" + numTicket + ", fecha=" + fecha + ", total=" + total + ", lineas=" + lineas.size() + "]";
    }
}
