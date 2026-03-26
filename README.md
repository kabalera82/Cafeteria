# Cafetería API

![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.5-brightgreen)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue)
![Docker](https://img.shields.io/badge/Docker-Compose-2496ED)
![License](https://img.shields.io/badge/License-MIT-yellow)

API REST para la gestión de un punto de venta (TPV) de hostelería. Cubre el ciclo completo: catálogo de productos, personal (camareros), cartera de clientes y emisión de tickets con recálculo automático de totales. Diseñada como proyecto de aprendizaje de Spring Boot 3 con JPA y MySQL.

## Características

- CRUD completo de **camareros** con baja lógica (soft-delete)
- CRUD completo de **productos** con precio y baja lógica
- Gestión de **clientes** con herencia JPA: `Particular` (persona física) y `Empresa` (con descuento aplicable)
- Creación de **tickets de venta** asociados a un camarero y un cliente
- Adición de **líneas de ticket** con precio unitario capturado en el momento de la venta
- **Recálculo automático del total** del ticket al agregar cada línea
- Los listados devuelven únicamente registros activos en todos los recursos
- Persistencia stateless en MySQL — desplegable detrás de un balanceador sin coordinación entre instancias

## Stack tecnológico

| Tecnología       | Versión                   | Uso                                      |
|------------------|---------------------------|------------------------------------------|
| Java             | 21 (LTS)                  | Lenguaje de la aplicación                |
| Spring Boot      | 3.2.5                     | Framework principal y autoconfiguración  |
| Spring Data JPA  | incluido en Boot 3.2.5    | Capa de acceso a datos (ORM / Hibernate) |
| Spring Web MVC   | incluido en Boot 3.2.5    | Exposición de endpoints REST             |
| Spring Validation| incluido en Boot 3.2.5    | Validación de beans con Bean Validation  |
| MySQL            | 8.0                       | Base de datos relacional                 |
| Maven            | 3.9+                      | Gestor de dependencias y build           |
| Docker Compose   | 3.9                       | Orquestación de contenedores en local    |
| Adminer          | latest                    | Interfaz web para administrar la BD      |

## Arquitectura

### Capas de la aplicación

```
Cliente HTTP (curl / Postman / frontend)
        │  HTTP / JSON
        ▼
  Controllers (@RestController)
  ClienteController · CamareroController
  ProductoController · TicketController
        │  llamada a método Java
        ▼
  Services (@Service)
  ClienteService · CamareroService
  ProductoService · TicketService
        │  Spring Data JPA
        ▼
  Repositories (JpaRepository)
        │  Hibernate / JDBC
        ▼
  MySQL 8.0
```

### Herencia JPA de Cliente (JOINED)

```
         Cliente  (tabla: cliente)
        /         \
  Particular    Empresa
(tabla:          (tabla:
 particular)      empresa)
```

`InheritanceType.JOINED`: cada subtipo tiene su propia tabla con la clave foránea al padre. Los endpoints `/api/clientes/particulares` y `/api/clientes/empresas` devuelven el tipo concreto.

## Inicio rápido

### Prerrequisitos

| Herramienta       | Versión mínima |
|-------------------|----------------|
| Java              | 21             |
| Maven             | 3.9            |
| Docker            | cualquiera reciente |
| Docker Compose    | v2 (plugin)    |

### Instalación y arranque

1. Clonar el repositorio:

```bash
git clone https://github.com/tu-usuario/cafeteria.git
cd cafeteria
```

2. Levantar la base de datos con Docker:

```bash
docker compose up -d
```

Esto arranca MySQL 8.0 en el puerto `3306` y Adminer en el puerto `8080`. El schema inicial se carga desde `scripts/init.sql`.

3. Arrancar la aplicación Spring Boot:

```bash
mvn spring-boot:run
```

La API queda disponible en `http://localhost:8080`. Adminer estará en `http://localhost:8080` solo si no arrancas la app (comparten puerto — arranca primero la app).

> Nota: con la app corriendo, Adminer no es accesible porque ambos usan el puerto 8080. Para usar Adminer, detén la app primero o cambia `server.port` en `application.properties`.

4. Verificar que todo funciona:

```bash
curl http://localhost:8080/api/productos
```

Debe responder `[]` (lista vacía) con HTTP 200.

### Primer uso — ejemplo en 3 pasos

**Paso 1 — Crear un producto:**

```bash
curl -s -X POST http://localhost:8080/api/productos \
  -H "Content-Type: application/json" \
  -d '{"descripcion": "Café solo", "precio": 1.20, "stock": 100}'
```

Guarda el `idProducto` de la respuesta.

**Paso 2 — Crear un camarero:**

```bash
curl -s -X POST http://localhost:8080/api/camareros \
  -H "Content-Type: application/json" \
  -d '{"nombre": "Ana", "apellidos": "García López", "salario": 1800.00}'
```

Guarda el `idCamarero` de la respuesta.

**Paso 3 — Crear un ticket y agregarle una línea:**

```bash
# Crear el ticket (sustituye los IDs por los de tu respuesta)
curl -s -X POST http://localhost:8080/api/tickets \
  -H "Content-Type: application/json" \
  -d '{"numTicket": "T-001", "camarero": {"idCamarero": 1}}'

# Agregar una línea al ticket
curl -s -X POST http://localhost:8080/api/tickets/T-001/lineas \
  -H "Content-Type: application/json" \
  -d '{"idProducto": "<id-del-producto>", "cantidad": 2}'
```

La respuesta incluye el ticket con `total` recalculado automáticamente.

## API — resumen de endpoints

### Camareros

| Método   | Endpoint                  | Descripción                        |
|----------|---------------------------|------------------------------------|
| GET      | `/api/camareros`          | Listar camareros activos           |
| GET      | `/api/camareros/{id}`     | Obtener camarero por ID            |
| POST     | `/api/camareros`          | Crear camarero                     |
| PUT      | `/api/camareros/{id}`     | Actualizar camarero                |
| DELETE   | `/api/camareros/{id}`     | Dar de baja (soft-delete)          |

### Productos

| Método   | Endpoint                  | Descripción                        |
|----------|---------------------------|------------------------------------|
| GET      | `/api/productos`          | Listar productos activos           |
| GET      | `/api/productos/{id}`     | Obtener producto por ID            |
| POST     | `/api/productos`          | Crear producto                     |
| PUT      | `/api/productos/{id}`     | Actualizar producto                |
| DELETE   | `/api/productos/{id}`     | Dar de baja (soft-delete)          |

### Clientes

| Método   | Endpoint                        | Descripción                        |
|----------|---------------------------------|------------------------------------|
| GET      | `/api/clientes`                 | Listar clientes activos            |
| GET      | `/api/clientes/{id}`            | Obtener cliente por ID             |
| GET      | `/api/clientes/empresas`        | Listar solo empresas               |
| GET      | `/api/clientes/particulares`    | Listar solo particulares           |
| POST     | `/api/clientes/empresa`         | Crear cliente empresa              |
| POST     | `/api/clientes/particular`      | Crear cliente particular           |
| PUT      | `/api/clientes/{id}`            | Actualizar cliente                 |
| DELETE   | `/api/clientes/{id}`            | Dar de baja (soft-delete)          |

### Tickets

| Método   | Endpoint                            | Descripción                              |
|----------|-------------------------------------|------------------------------------------|
| GET      | `/api/tickets`                      | Listar todos los tickets                 |
| GET      | `/api/tickets/{numTicket}`          | Obtener ticket por número                |
| GET      | `/api/tickets/cliente/{idCliente}`  | Listar tickets de un cliente             |
| GET      | `/api/tickets/camarero/{idCamarero}`| Listar tickets de un camarero            |
| POST     | `/api/tickets`                      | Crear ticket                             |
| POST     | `/api/tickets/{numTicket}/lineas`   | Agregar línea a un ticket                |
| DELETE   | `/api/tickets/{numTicket}`          | Eliminar ticket                          |

## Documentación

| Documento | Descripción |
|-----------|-------------|
| [Guía de Usuario](docs/guia-usuario.md) | Referencia completa de la API con ejemplos curl para cada endpoint |
| [Documentación Técnica](docs/documentacion-tecnica.md) | Arquitectura, modelo de datos, diagrama ER y decisiones de diseño |
| [Guía de Desarrollador](docs/guia-desarrollador.md) | Cómo construir la aplicación desde cero, paso a paso |

## Estructura del proyecto

```
cafeteria/
├── src/
│   └── main/
│       ├── java/com/cafeteria/
│       │   ├── CafeteriaApplication.java   # Punto de entrada Spring Boot
│       │   ├── controller/                 # Capa HTTP — @RestController
│       │   │   ├── CamareroController.java
│       │   │   ├── ClienteController.java
│       │   │   ├── ProductoController.java
│       │   │   └── TicketController.java
│       │   ├── service/                    # Lógica de negocio — @Service
│       │   │   ├── CamareroService.java
│       │   │   ├── ClienteService.java
│       │   │   ├── ProductoService.java
│       │   │   └── TicketService.java
│       │   ├── repository/                 # Acceso a datos — JpaRepository
│       │   │   ├── CamareroRepository.java
│       │   │   ├── ClienteRepository.java
│       │   │   ├── EmpresaRepository.java
│       │   │   ├── ParticularRepository.java
│       │   │   ├── ProductoRepository.java
│       │   │   ├── CabeceraTicketRepository.java
│       │   │   └── LineaTicketRepository.java
│       │   └── model/                      # Entidades JPA
│       │       ├── Cliente.java            # Clase base abstracta (JOINED)
│       │       ├── Particular.java         # Subtipo de Cliente
│       │       ├── Empresa.java            # Subtipo de Cliente
│       │       ├── Camarero.java
│       │       ├── Producto.java
│       │       ├── CabeceraTicket.java     # Cabecera del ticket con total recalculable
│       │       └── LineaTicket.java        # Línea individual con subtotal automático
│       └── resources/
│           └── application.properties      # Configuración de datasource y JPA
├── scripts/
│   └── init.sql                            # Script de inicialización de la BD
├── docs/
│   ├── guia-usuario.md
│   ├── documentacion-tecnica.md
│   └── guia-desarrollador.md
├── docker-compose.yml                      # MySQL 8.0 + Adminer
└── pom.xml                                 # Dependencias Maven
```

## Configuración

Todas las propiedades se configuran en `src/main/resources/application.properties`:

| Propiedad | Valor por defecto | Descripción |
|-----------|-------------------|-------------|
| `spring.datasource.url` | `jdbc:mysql://localhost:3306/cafeteria` | URL de conexión a MySQL |
| `spring.datasource.username` | `user` | Usuario de la base de datos |
| `spring.datasource.password` | `0000` | Contraseña de la base de datos |
| `server.port` | `8080` | Puerto en que escucha la API |
| `spring.jpa.hibernate.ddl-auto` | `update` | Gestión del schema (update / validate / create) |
| `spring.jpa.show-sql` | `true` | Mostrar SQL generado en consola |

Las credenciales del contenedor Docker están definidas en `docker-compose.yml` y deben coincidir con las de `application.properties`.

## Estado del proyecto

### Implementado

- CRUD completo de camareros, productos y clientes
- Herencia JPA (JOINED) para clientes empresa y particular
- Creación de tickets y adición de líneas con recálculo automático del total
- Filtrado de tickets por cliente y por camarero
- Soft-delete en todos los recursos (camareros, productos, clientes)
- Inicialización del schema via `scripts/init.sql`

### Pendiente

- Autenticación y autorización (Spring Security / JWT)
- Tests unitarios e integración (JUnit 5 / Testcontainers)
- Paginación en los endpoints de listado
- Validación de stock disponible al crear líneas de ticket
- Manejo global de errores con `@ControllerAdvice` y respuestas de error estructuradas
- Documentación OpenAPI / Swagger UI
- Perfiles de configuración (dev / prod)

## Contribuir

1. Haz un fork del repositorio
2. Crea una rama descriptiva: `git checkout -b feat/nombre-de-la-funcionalidad`
3. Realiza tus cambios y escribe commits siguiendo [Conventional Commits](https://www.conventionalcommits.org/):
   ```
   feat: añadir paginación en listado de productos
   fix: corregir recálculo de total al eliminar línea
   docs: actualizar guía de usuario con ejemplos de empresa
   ```
4. Abre un Pull Request describiendo qué cambia y por qué

## Licencia

MIT
