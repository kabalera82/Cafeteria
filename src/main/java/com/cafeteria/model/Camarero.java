package com.cafeteria.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "camarero")
public class Camarero {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_camarero")
    private int idCamarero;

    @Column(nullable = false, unique = true, length = 20)
    private String nif;

    @Column(nullable = false, length = 50)
    private String nombre;

    @Column(name = "fecha_incorporacion", nullable = false)
    private LocalDate fechaIncorporacion;

    @Column(nullable = false)
    private boolean activo;

    public Camarero() { this.activo = true; }

    public int getIdCamarero() { return idCamarero; }
    public void setIdCamarero(int idCamarero) { this.idCamarero = idCamarero; }
    public String getNif() { return nif; }
    public void setNif(String nif) { this.nif = nif; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public LocalDate getFechaIncorporacion() { return fechaIncorporacion; }
    public void setFechaIncorporacion(LocalDate fechaIncorporacion) { this.fechaIncorporacion = fechaIncorporacion; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Camarero c)) return false;
        return Objects.equals(nif, c.nif);
    }

    @Override
    public int hashCode() { return Objects.hash(nif); }

    @Override
    public String toString() {
        return "Camarero[id=" + idCamarero + ", nif=" + nif + ", nombre=" + nombre + "]";
    }
}
