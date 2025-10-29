package model;

/**
 * Representa una línea individual dentro de un ticket.
 * Cada línea corresponde a un producto y su cantidad vendida.
 */
public class LineaTicket {

    private int numLinea;         // número de línea dentro del ticket
    private Producto producto;    // producto vendido
    private int cantidad;         // cantidad vendida
    private double subtotal;      // precio total de esta línea

    // 🔹 Constructor por defecto
    public LineaTicket() {}

    // 🔹 Constructor con datos
    public LineaTicket(int numLinea, Producto producto, int cantidad) {
        this.numLinea = numLinea;
        this.producto = producto;
        this.cantidad = cantidad;
        this.subtotal = producto.getPrecio() * cantidad;
    }

    // 🔹 Getters y Setters
    public int getNumLinea() {
        return numLinea;
    }

    public void setNumLinea(int numLinea) {
        this.numLinea = numLinea;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
        this.subtotal = producto.getPrecio() * cantidad;
    }

    public double getSubtotal() {
        return subtotal;
    }

    @Override
    public String toString() {
        return "Linea{" +
                "numLinea=" + numLinea +
                ", producto=" + producto.getDescripcion() +
                ", cantidad=" + cantidad +
                ", subtotal=" + subtotal +
                '}';
    }
}
