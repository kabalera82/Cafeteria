package model;

import java.time.LocalDateTime;

public class Particular extends Cliente{

    // constructor por defecto
    public Particular(){
        super();
    }

    //constructor con todos los parametros
    public Particular(boolean activo, String direccion, String email, LocalDateTime fechaAlta,
                      String idCliente, String nombre, int numeroCliente, String primerApellido,
                      String segundoApellido, String telefono)
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
    }

    //toString
    @Override
    public String toString() {
        return super.toString() + " | Particular";
    }

}
