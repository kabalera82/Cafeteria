CREATE DATABASE IF NOT EXISTS cafeteria;
USE cafeteria;

-- Tabla base de clientes (abstracta)
CREATE TABLE Cliente (
    idCliente CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    numeroCliente INT AUTO_INCREMENT,
    nombre VARCHAR(100),
    primerApellido VARCHAR(100),
    segundoApellido VARCHAR(100),
    direccion VARCHAR(150),
    telefono VARCHAR(15),
    email VARCHAR(100),
    fechaAlta DATETIME DEFAULT CURRENT_TIMESTAMP,
    activo BOOLEAN DEFAULT TRUE
);
