package com.cafeteria.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "cliente")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Cliente {

    @Id
    @Column(name = "id_cliente", length = 36)
    private String idCliente;

    @Column(name = "numero_cliente", unique = true)
    private int numeroCliente;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(name = "primer_apellido", length = 100)
    private String primerApellido;

    @Column(name = "segundo_apellido", length = 100)
    private String segundoApellido;

    @Column(length = 150)
    private String direccion;

    @Column(length = 15)
    private String telefono;

    @Column(length = 100)
    private String email;

    @Column(name = "fecha_alta")
    private LocalDateTime fechaAlta;

    @Column(nullable = false)
    private boolean activo;

    protected Cliente() {
        this.idCliente = UUID.randomUUID().toString();
        this.fechaAlta = LocalDateTime.now();
        this.activo = true;
    }

    public String getIdCliente() { return idCliente; }
    public void setIdCliente(String idCliente) { this.idCliente = idCliente; }
    public int getNumeroCliente() { return numeroCliente; }
    public void setNumeroCliente(int numeroCliente) { this.numeroCliente = numeroCliente; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getPrimerApellido() { return primerApellido; }
    public void setPrimerApellido(String primerApellido) { this.primerApellido = primerApellido; }
    public String getSegundoApellido() { return segundoApellido; }
    public void setSegundoApellido(String segundoApellido) { this.segundoApellido = segundoApellido; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public LocalDateTime getFechaAlta() { return fechaAlta; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}
