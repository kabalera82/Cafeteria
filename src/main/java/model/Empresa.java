package model;

import java.time.LocalDateTime;

public class Empresa extends Cliente{

    private double descuento;
    public Empresa(){
        super();
    }

    public Empresa(boolean activo, String direccion, String email, LocalDateTime fechaAlta,
                   String idCliente, String nombre, int numeroCliente, String primerApellido,
                   String segundoApellido, String telefono, double descuento)
        {
            super(
                    activo,
                    direccion,
                    email,
                    fechaAlta,
                    idCliente,
                    nombre,
                    numeroCliente,
                    primerApellido,
                    segundoApellido,
                    telefono
            );
            this.descuento = descuento;
    }

    public Empresa(double descuento) {
        this.descuento = descuento;
    }

    @Override
    public String toString() {
        return super.toString() + " | Empresa (Descuento: " + descuento + "%)";
    }
}
