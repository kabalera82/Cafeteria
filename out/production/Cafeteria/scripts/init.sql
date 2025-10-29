CREATE DATABASE IF NOT EXISTS cafeteria;
USE CAFETERIA;

CREATE TABLE Cliente(
                idCliente CHAR(36) PRIMARY KEY DEFAULT (UUID()),
                nombre VARCHAR(100),
                direccion VARCHAR(100),
                telefono VARCHAR(15)
                    );