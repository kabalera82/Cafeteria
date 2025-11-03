CREATE DATABASE IF NOT EXISTS cafeteria;
USE cafeteria;

-- TABLA BASE DE CLIENTES
CREATE TABLE cliente (
    id_cliente CHAR(36) PRIMARY KEY,
    numero_cliente INT UNIQUE,
    nombre VARCHAR(100) NOT NULL,
    primer_apellido VARCHAR(100),
    segundo_apellido VARCHAR(100),
    direccion VARCHAR(150),
    telefono VARCHAR(15),
    email VARCHAR(100),
    fecha_alta DATETIME DEFAULT CURRENT_TIMESTAMP,
    activo BOOLEAN DEFAULT TRUE
);

-- TABLA EMPRESA (subclase)
CREATE TABLE empresa (
    id_cliente CHAR(36) PRIMARY KEY,
    descuento DECIMAL(5,2) NOT NULL,
    FOREIGN KEY (id_cliente) REFERENCES cliente(id_cliente)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

-- TABLA PARTICULAR (subclase)
CREATE TABLE particular (
    id_cliente CHAR(36) PRIMARY KEY,
    FOREIGN KEY (id_cliente) REFERENCES cliente(id_cliente)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

-- TABLA CAMAREROS
CREATE TABLE camarero (
    id_camarero INT AUTO_INCREMENT PRIMARY KEY,
    nif VARCHAR(20) NOT NULL UNIQUE,
    nombre VARCHAR(50) NOT NULL,
    fecha_incorporacion DATE NOT NULL,
    activo BOOLEAN NOT NULL
);

-- TABLA CABECERA DE TICKET
CREATE TABLE cabecera_ticket (
    num_ticket VARCHAR(50) PRIMARY KEY,
    fecha DATETIME NOT NULL,
    total DECIMAL(10,2) NOT NULL,
    id_cliente CHAR(36),
    id_camarero INT,
    FOREIGN KEY (id_cliente) REFERENCES cliente(id_cliente)
        ON UPDATE CASCADE
        ON DELETE SET NULL,
    FOREIGN KEY (id_camarero) REFERENCES camarero(id_camarero)
        ON UPDATE CASCADE
        ON DELETE SET NULL
);

-- TABLA LÍNEAS DE TICKET
CREATE TABLE linea_ticket (
    id_linea INT AUTO_INCREMENT PRIMARY KEY,
    num_ticket VARCHAR(50) NOT NULL,
    producto VARCHAR(100) NOT NULL,
    cantidad INT NOT NULL,
    precio_unitario DECIMAL(10,2) NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (num_ticket) REFERENCES cabecera_ticket(num_ticket)
        ON UPDATE CASCADE
        ON DELETE CASCADE
);

-- TABLA PRODUCTO
CREATE TABLE producto (
    id_producto CHAR(36) PRIMARY KEY,
    descripcion VARCHAR(150) NOT NULL,
    stock INT NOT NULL,
    precio DECIMAL(10,2) NOT NULL,
    fecha_alta DATETIME DEFAULT CURRENT_TIMESTAMP,
    activo BOOLEAN NOT NULL
);
