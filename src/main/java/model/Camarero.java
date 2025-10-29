package model;

import java.time.LocalDate;

public class Camarero {


    // Identificador técnico (autoincremental en MySQL)
    private int idCamarero;

    // Atributos del camarero
    private String nif;
    private String nombre;
    private LocalDate fechaIncorporacion;

    // Datos de control del empleado
    private boolean activo;

    public Camarero (){
        this.activo = true;
    }

    public Camarero(boolean activo, LocalDate fechaIncorporacion, int idCamarero, String nif, String nombre) {
        this.activo = activo;
        this.fechaIncorporacion = fechaIncorporacion;
        this.idCamarero = idCamarero;
        this.nif = nif;
        this.nombre = nombre;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public LocalDate getFechaIncorporacion() {
        return fechaIncorporacion;
    }

    public void setFechaIncorporacion(LocalDate fechaIncorporacion) {
        this.fechaIncorporacion = fechaIncorporacion;
    }

    public int getIdCamarero() {
        return idCamarero;
    }

    public void setIdCamarero(int idCamarero) {
        this.idCamarero = idCamarero;
    }

    public String getNif() {
        return nif;
    }

    public void setNif(String nif) {
        this.nif = nif;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public String toString() {
        return "Camarero{" +
                "activo=" + activo +
                ", idCamarero=" + idCamarero +
                ", nif='" + nif + '\'' +
                ", nombre='" + nombre + '\'' +
                ", fechaIncorporacion=" + fechaIncorporacion +
                '}';
    }
}
