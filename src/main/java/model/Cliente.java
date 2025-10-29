package model;

import java.time.LocalDateTime;
import java.util.UUID;

public abstract class Cliente {

    //Identificador tecnico
    private String idCliente;

    // Atributos del cliente
    protected int numeroCliente;
    protected String nombre;
    protected String primerApellido;
    protected String segundoApellido;
    protected String direccion;
    protected String telefono;
    protected String email;

    // Datos de control
    private LocalDateTime fechaAlta;
    private boolean activo;

    /**
     * Constructor Base solo id fecha alta y si esta activo o no
     * Asigna el id la fecha y si esta activo o no para la herencia
     */
    protected Cliente() {
        this.idCliente = UUID.randomUUID().toString();
        this.fechaAlta = LocalDateTime.now();
        this.activo = true;
    }

    /**
     * Constructor completo.
     * Inicializa todos los campos, reutilizando el constructor base.
     */


    public Cliente(boolean activo, String direccion, String email, LocalDateTime fechaAlta, String idCliente, String nombre, int numeroCliente, String primerApellido, String segundoApellido, String telefono) {
        this(); // <-- Esto llama al constructor anterior
        this.activo = activo;
        this.direccion = direccion;
        this.email = email;
        this.fechaAlta = fechaAlta;
        this.idCliente = idCliente;
        this.nombre = nombre;
        this.numeroCliente = numeroCliente;
        this.primerApellido = primerApellido;
        this.segundoApellido = segundoApellido;
        this.telefono = telefono;
    }

    // Getters & Setters
    public boolean isActivo() {return activo;}
    public void setActivo(boolean activo) {this.activo = activo;}
    public String getDireccion() {return direccion;}
    public void setDireccion(String direccion) {this.direccion = direccion;}
    public String getEmail() {return email;}
    public void setEmail(String email) {this.email = email;}
    public LocalDateTime getFechaAlta() {return fechaAlta;}
    public void setFechaAlta(LocalDateTime fechaAlta) {this.fechaAlta = fechaAlta;}
    public String getIdCliente() {return idCliente;}
    public void setIdCliente(String idCliente) {this.idCliente = idCliente;}
    public String getNombre() {return nombre;}
    public void setNombre(String nombre) {this.nombre = nombre;}
    public int getNumeroCliente() {return numeroCliente;}
    public void setNumeroCliente(int numeroCliente) {this.numeroCliente = numeroCliente;}
    public String getPrimerApellido() {return primerApellido;}
    public void setPrimerApellido(String primerApellido) {this.primerApellido = primerApellido;}
    public String getSegundoApellido() {return segundoApellido;}
    public void setSegundoApellido(String segundoApellido) {this.segundoApellido = segundoApellido;}
    public String getTelefono() {return telefono;}
    public void setTelefono(String telefono) {this.telefono = telefono;}

    @Override
    public String toString() {
        return "Cliente[" +
                "activo=" + activo +
                ", idCliente='" + idCliente + '\'' +
                ", numeroCliente=" + numeroCliente +
                ", nombre='" + nombre + '\'' +
                ", primerApellido='" + primerApellido + '\'' +
                ", segundoApellido='" + segundoApellido + '\'' +
                ", direccion='" + direccion + '\'' +
                ", telefono='" + telefono + '\'' +
                ", email='" + email + '\'' +
                ", fechaAlta=" + fechaAlta +
                ']';
    }
}
