package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa la cabecera de un ticket de venta.
 * Contiene la información general del ticket (fecha, total, cliente, camarero)
 * y una lista de líneas asociadas.
 */
public class CabeceraTicket {

    private int numTicket;                  // identificador visible o incremental
    private LocalDateTime fecha;            // fecha y hora de emisión
    private double total;                   // total del ticket

    // Relaciones
    private Cliente cliente;                // cliente asociado
    private Camarero camarero;              // camarero que atendió
    private List<LineaTicket> lineas;       // lista de productos vendidos

    // 🔹 Constructor por defecto
    public CabeceraTicket() {
        this.fecha = LocalDateTime.now();
        this.lineas = new ArrayList<>();
    }

    // 🔹 Métodos de acceso
    public int getNumTicket() {
        return numTicket;
    }

    public void setNumTicket(int numTicket) {
        this.numTicket = numTicket;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Camarero getCamarero() {
        return camarero;
    }

    public void setCamarero(Camarero camarero) {
        this.camarero = camarero;
    }

    public List<LineaTicket> getLineas() {
        return lineas;
    }

    /*
    // Agregar una línea al ticket
    public void agregarLinea(LineaTicket linea) {
        lineas.add(linea);
        recalcularTotal();
    }
*/
    /*
    // Recalcular el total del ticket (suma de subtotales)
    public void recalcularTotal() {
        total = 0;
        for (LineaTicket l : lineas) {
            total += l.getSubtotal();
        }
    }
*/
    @Override
    public String toString() {
        return "Ticket{" +
                "numTicket=" + numTicket +
                ", fecha=" + fecha +
                ", total=" + total +
                ", cliente=" + (cliente != null ? cliente.getNombre() : "Sin cliente") +
                ", camarero=" + (camarero != null ? camarero.getNombre() : "Sin camarero") +
                ", lineas=" + lineas.size() +
                '}';
    }
}

