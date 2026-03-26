# Documentación Técnica — API Cafetería

> **[APRENDE]** La documentación técnica es un artefacto de ingeniería, no un lujo. Va dirigida a tres perfiles distintos con necesidades distintas: **desarrolladores** (que necesitan entender el código para extenderlo o mantenerlo), **operaciones/DevOps** (que necesitan desplegar y monitorizar el sistema) y **arquitectos** (que necesitan entender las decisiones de diseño para evolucionar el sistema sin romperlo). Una buena doc técnica responde a las preguntas que llegarán en producción a las 3 de la madrugada, no a las que ya conoce quien la escribió.

---

## 1. Visión general del sistema

> **[APRENDE]** El "system overview" es la primera sección que lee cualquiera. Debe responder en 30 segundos a la pregunta: ¿qué hace esto y con qué está construido? No es el momento de entrar en detalles de implementación. Piensa en ello como el plano de un edificio visto desde el cielo: se ven los bloques principales, no los tornillos. Si alguien tiene que leer más de un párrafo para entender el propósito del sistema, la sección está mal escrita.

### 1.1 Descripción

La API Cafetería es un sistema de gestión de punto de venta (TPV) para establecimientos de hostelería. Expone una API REST que permite gestionar el catálogo de productos, el personal (camareros), la cartera de clientes —tanto particulares como empresas con descuento— y el ciclo completo de un ticket: creación, adición de líneas y consulta histórica.

El sistema persiste todos los datos en MySQL y no implementa estado en memoria, por lo que es stateless y puede desplegarse detrás de un balanceador sin coordinación entre instancias.

### 1.2 Stack tecnológico

| Tecnología | Versión | Propósito |
|---|---|---|
| Java | 21 (LTS) | Lenguaje de la aplicación |
| Spring Boot | 3.2.5 | Framework de aplicación y autoconfiguración |
| Spring Data JPA | (incluido en Boot 3.2.5) | Capa de acceso a datos — ORM sobre Hibernate |
| Spring Web MVC | (incluido en Boot 3.2.5) | Exposición de endpoints REST |
| Spring Validation | (incluido en Boot 3.2.5) | Validación de beans con Bean Validation |
| Hibernate | (incluido en Boot 3.2.5) | Implementación JPA — mapeo objeto-relacional |
| MySQL | 8.0 | Base de datos relacional |
| mysql-connector-j | (gestionado por Boot) | Driver JDBC para MySQL |
| Maven | (wrapper) | Gestor de dependencias y build |
| Docker Compose | 3.9 | Orquestación de contenedores en desarrollo |
| Adminer | latest | Interfaz web para administrar la base de datos |

### 1.3 Diagrama de arquitectura

```
┌──────────────────────────────────────────────────────────────────┐
│                        Cliente HTTP                              │
│            (navegador, Postman, frontend, otra API)              │
└───────────────────────────┬──────────────────────────────────────┘
                            │  HTTP/JSON
                            ▼
┌──────────────────────────────────────────────────────────────────┐
│                    Spring Boot Application                       │
│  ┌─────────────────────────────────────────────────────────┐    │
│  │                    Controllers (@RestController)         │    │
│  │   ClienteController   CamareroController                │    │
│  │   ProductoController  TicketController                  │    │
│  └───────────────────────────┬─────────────────────────────┘    │
│                              │  llamada a método Java            │
│  ┌───────────────────────────▼─────────────────────────────┐    │
│  │                    Services (@Service)                   │    │
│  │   ClienteService   CamareroService                      │    │
│  │   ProductoService  TicketService                        │    │
│  └───────────────────────────┬─────────────────────────────┘    │
│                              │  llamada a método Java            │
│  ┌───────────────────────────▼─────────────────────────────┐    │
│  │              Repositories (Spring Data JPA)              │    │
│  │   ClienteRepository    EmpresaRepository                │    │
│  │   ParticularRepository CamareroRepository               │    │
│  │   ProductoRepository   CabeceraTicketRepository         │    │
│  │   LineaTicketRepository                                 │    │
│  └───────────────────────────┬─────────────────────────────┘    │
│                              │  SQL (JDBC / Hibernate)           │
└──────────────────────────────┼───────────────────────────────────┘
                               │
                               ▼
                  ┌────────────────────────┐
                  │     MySQL 8.0          │
                  │   base de datos:       │
                  │     cafeteria          │
                  └────────────────────────┘
```

---

## 2. Modelo de datos

> **[APRENDE]** El modelo de datos es el corazón de cualquier documentación técnica. Todo lo demás —las APIs, los servicios, los flujos— depende de él. Si el modelo de datos está mal documentado, el resto de la doc es casi inútil porque nadie sabrá qué representa realmente cada campo. Esta sección la consultan los desarrolladores cuando escriben queries, los DBA cuando optimizan índices, y los arquitectos cuando diseñan nuevas funcionalidades. Documenta los tipos reales de la base de datos, los constraints y el significado de negocio de cada campo: eso es lo que no se puede deducir solo leyendo el código.

### 2.1 Diagrama entidad-relación

```
┌──────────────────────┐        ┌──────────────────────┐
│       cliente        │        │       empresa        │
│──────────────────────│        │──────────────────────│
│ PK id_cliente CHAR36 │◄───────│ PK/FK id_cliente     │
│    numero_cliente INT│  1:1   │    descuento DEC(5,2)│
│    nombre VARCHAR100 │        └──────────────────────┘
│    primer_apellido   │
│    segundo_apellido  │        ┌──────────────────────┐
│    direccion         │        │      particular      │
│    telefono          │        │──────────────────────│
│    email             │◄───────│ PK/FK id_cliente     │
│    fecha_alta DATETIME│ 1:1   └──────────────────────┘
│    activo BOOLEAN    │
└──────────┬───────────┘
           │ 0..N (SET NULL al borrar cliente)
           │
           ▼
┌──────────────────────┐   N:1  ┌──────────────────────┐
│   cabecera_ticket    │───────►│      camarero        │
│──────────────────────│        │──────────────────────│
│ PK num_ticket VAR50  │        │ PK id_camarero INT AI │
│    fecha DATETIME    │        │    nif VARCHAR20 UNIQ │
│    total DEC(10,2)   │        │    nombre VARCHAR50   │
│ FK id_cliente CHAR36 │        │    fecha_incorporacion│
│ FK id_camarero INT   │        │    activo BOOLEAN     │
└──────────┬───────────┘        └──────────────────────┘
           │ 1:N (CASCADE DELETE)
           ▼
┌──────────────────────┐   N:1  ┌──────────────────────┐
│     linea_ticket     │───────►│       producto       │
│──────────────────────│        │──────────────────────│
│ PK id_linea INT AI   │        │ PK id_producto CHAR36 │
│ FK num_ticket VAR50  │        │    descripcion VAR150 │
│ FK id_producto CHAR36│        │    stock INT          │
│    cantidad INT      │        │    precio DEC(10,2)   │
│    precio_unitario   │        │    fecha_alta DATETIME│
│    subtotal DEC(10,2)│        │    activo BOOLEAN     │
└──────────────────────┘        └──────────────────────┘
```

**Cardinalidades:**
- `cliente` 1 ↔ 0..1 `empresa` (un cliente puede ser empresa o no serlo)
- `cliente` 1 ↔ 0..1 `particular` (un cliente puede ser particular o no serlo)
- `cliente` 1 ↔ 0..N `cabecera_ticket` (un cliente tiene cero o muchos tickets)
- `camarero` 1 ↔ 0..N `cabecera_ticket` (un camarero tiene cero o muchos tickets)
- `cabecera_ticket` 1 ↔ 1..N `linea_ticket` (un ticket tiene una o más líneas)
- `producto` 1 ↔ 0..N `linea_ticket` (un producto aparece en cero o muchas líneas)

### 2.2 Descripción de entidades

#### Tabla `cliente`

Entidad abstracta en Java. Nunca se instancia directamente: siempre es `Empresa` o `Particular`. La tabla contiene los campos comunes a ambos subtipos.

| Campo | Tipo SQL | Constraints | Descripción |
|---|---|---|---|
| `id_cliente` | `CHAR(36)` | PK | UUID generado en el constructor Java al crear el objeto |
| `numero_cliente` | `INT` | UNIQUE | Número de cliente legible por humanos (correlativo de negocio) |
| `nombre` | `VARCHAR(100)` | NOT NULL | Nombre del cliente o razón social de la empresa |
| `primer_apellido` | `VARCHAR(100)` | nullable | Primer apellido (solo relevante en particulares) |
| `segundo_apellido` | `VARCHAR(100)` | nullable | Segundo apellido (solo relevante en particulares) |
| `direccion` | `VARCHAR(150)` | nullable | Dirección postal |
| `telefono` | `VARCHAR(15)` | nullable | Teléfono de contacto |
| `email` | `VARCHAR(100)` | nullable | Correo electrónico de contacto |
| `fecha_alta` | `DATETIME` | DEFAULT NOW() | Fecha y hora de registro, asignada automáticamente en el constructor |
| `activo` | `BOOLEAN` | NOT NULL, DEFAULT TRUE | Marca de soft-delete: `false` significa dado de baja |

#### Tabla `empresa`

Subclase de `cliente`. Comparte PK con la tabla padre mediante FK (`id_cliente`).

| Campo | Tipo SQL | Constraints | Descripción |
|---|---|---|---|
| `id_cliente` | `CHAR(36)` | PK + FK → `cliente.id_cliente` CASCADE | Mismo identificador que en la tabla padre |
| `descuento` | `DECIMAL(5,2)` | NOT NULL | Porcentaje de descuento aplicable a esta empresa (ej. 10.50 = 10,50%) |

**Relaciones:** hereda todos los campos de `cliente`. FK con ON DELETE CASCADE: si se elimina el registro padre en `cliente`, se elimina también en `empresa`.

#### Tabla `particular`

Subclase de `cliente` sin campos adicionales. La tabla existe únicamente para materializar la herencia JPA JOINED y permitir distinguir el tipo de cliente en la base de datos.

| Campo | Tipo SQL | Constraints | Descripción |
|---|---|---|---|
| `id_cliente` | `CHAR(36)` | PK + FK → `cliente.id_cliente` CASCADE | Mismo identificador que en la tabla padre |

#### Tabla `camarero`

Entidad independiente. Representa al personal del establecimiento que atiende mesas.

| Campo | Tipo SQL | Constraints | Descripción |
|---|---|---|---|
| `id_camarero` | `INT` | PK, AUTO_INCREMENT | Clave subrogada numérica generada por la base de datos |
| `nif` | `VARCHAR(20)` | NOT NULL, UNIQUE | Número de identificación fiscal — identidad natural del camarero |
| `nombre` | `VARCHAR(50)` | NOT NULL | Nombre completo del camarero |
| `fecha_incorporacion` | `DATE` | NOT NULL | Fecha de inicio en el establecimiento |
| `activo` | `BOOLEAN` | NOT NULL | Soft-delete: `false` = baja laboral o cese |

**Igualdad (`equals`/`hashCode`):** implementada por `nif`, no por `id_camarero`. Dos objetos `Camarero` son iguales si tienen el mismo NIF.

#### Tabla `producto`

Catálogo de artículos disponibles para su venta.

| Campo | Tipo SQL | Constraints | Descripción |
|---|---|---|---|
| `id_producto` | `CHAR(36)` | PK | UUID generado automáticamente en el constructor Java |
| `descripcion` | `VARCHAR(150)` | NOT NULL | Nombre o descripción del artículo (ej. "Café con leche") |
| `stock` | `INT` | NOT NULL | Unidades disponibles en inventario |
| `precio` | `DECIMAL(10,2)` | NOT NULL | Precio de venta al público en el momento actual |
| `fecha_alta` | `DATETIME` | DEFAULT NOW() | Fecha y hora en que se dio de alta el producto |
| `activo` | `BOOLEAN` | NOT NULL, DEFAULT TRUE | Soft-delete: `false` = producto retirado del menú |

#### Tabla `cabecera_ticket`

Representa la cabecera de un ticket de compra. Contiene los metadatos del ticket y el total calculado. El total se recalcula automáticamente en Java cada vez que se agrega una línea.

| Campo | Tipo SQL | Constraints | Descripción |
|---|---|---|---|
| `num_ticket` | `VARCHAR(50)` | PK | Identificador del ticket (cadena libre, asignada por el cliente de la API) |
| `fecha` | `DATETIME` | NOT NULL | Fecha y hora de apertura del ticket, asignada en el constructor |
| `total` | `DECIMAL(10,2)` | NOT NULL | Suma de los subtotales de todas las líneas; se recalcula en `recalcularTotal()` |
| `id_cliente` | `CHAR(36)` | FK nullable → `cliente` ON DELETE SET NULL | Cliente asociado al ticket; puede ser nulo (ticket sin cliente identificado) |
| `id_camarero` | `INT` | FK nullable → `camarero` ON DELETE SET NULL | Camarero que atendió; puede ser nulo |

**Comportamiento relevante:** `agregarLinea(LineaTicket)` establece la relación bidireccional y llama a `recalcularTotal()`, manteniendo el campo `total` siempre consistente en memoria antes de persistir.

#### Tabla `linea_ticket`

Cada fila representa un artículo dentro de un ticket. El precio unitario se captura como snapshot en el momento de la venta.

| Campo | Tipo SQL | Constraints | Descripción |
|---|---|---|---|
| `id_linea` | `INT` | PK, AUTO_INCREMENT | Clave subrogada generada por la base de datos |
| `num_ticket` | `VARCHAR(50)` | FK NOT NULL → `cabecera_ticket` ON DELETE CASCADE | Ticket al que pertenece esta línea |
| `id_producto` | `CHAR(36)` | FK NOT NULL → `producto` ON DELETE RESTRICT | Producto vendido; RESTRICT impide borrar un producto con líneas |
| `cantidad` | `INT` | NOT NULL | Número de unidades del producto en esta línea |
| `precio_unitario` | `DECIMAL(10,2)` | NOT NULL | Precio del producto en el momento de la venta (snapshot) |
| `subtotal` | `DECIMAL(10,2)` | NOT NULL | `cantidad × precio_unitario`, calculado en el constructor Java |

### 2.3 Estrategia de herencia JPA

> **[APRENDE]** JPA ofrece tres estrategias para mapear una jerarquía de clases Java a tablas relacionales. Esta es una de las decisiones de diseño más importantes en una aplicación con ORM, porque afecta directamente al esquema de la base de datos, al rendimiento de las consultas y a la capacidad de imponer constraints a nivel de base de datos. Elegir mal aquí tiene consecuencias que se pagan durante años. Hay que conocer las tres opciones antes de decidir.

#### Las tres estrategias de herencia en JPA

**Opción 1: SINGLE_TABLE** — Una sola tabla para toda la jerarquía

```
┌────────────────────────────────────────────────────────────────┐
│                           cliente                              │
│  id_cliente | nombre | ... | descuento | DTYPE                 │
│─────────────────────────────────────────────────────────────── │
│  uuid-1     | Acme   | ... | 15.00     | Empresa               │
│  uuid-2     | Juan   | ... | NULL      | Particular            │
└────────────────────────────────────────────────────────────────┘
```

- Pros: consultas simples, sin JOINs, máximo rendimiento de lectura
- Contras: columnas nullable obligatoriamente, imposible añadir constraints NOT NULL a campos de subclases, la tabla crece con columnas nulas para registros que no las usan

**Opción 2: TABLE_PER_CLASS** — Una tabla completa por cada clase concreta

```
┌─────────────────────────────────┐  ┌─────────────────────────────────┐
│            empresa              │  │           particular             │
│  id_cliente | nombre | descuento│  │  id_cliente | nombre | ...       │
└─────────────────────────────────┘  └─────────────────────────────────┘
```

- Pros: cada tabla es autónoma, sin NULLs
- Contras: los campos comunes se duplican en cada tabla, consultas polimórficas requieren UNION ALL (muy costoso), no hay una tabla `cliente` que referenciar con FK desde `cabecera_ticket`

**Opción 3: JOINED** ← elegida en este proyecto

```
┌───────────────────────────────────────┐
│               cliente                 │
│  id_cliente | nombre | email | activo │
└─────────────┬─────────────────────────┘
              │ PK compartida (no FK "normal")
    ┌─────────▼──────┐    ┌──────────────┐
    │    empresa     │    │  particular  │
    │  id_cliente    │    │  id_cliente  │
    │  descuento     │    │  (sin extras)│
    └────────────────┘    └──────────────┘
```

- Pros: modelo normalizado, sin redundancia, permite FK desde otras tablas apuntando a `cliente` independientemente del subtipo, constraints NOT NULL en campos de subclases, semánticamente correcto
- Contras: las consultas polimórficas requieren un JOIN entre la tabla padre y la subclase correspondiente; para este dominio el coste es asumible

#### Por qué se eligió JOINED en este proyecto

`cabecera_ticket` tiene una FK que apunta a `cliente` sin importar si es empresa o particular. Con JOINED, esa FK referencia siempre la misma tabla `cliente`, lo que es limpio y correcto. Con TABLE_PER_CLASS no existiría esa tabla `cliente` y la FK sería imposible de implementar de forma íntegra. Con SINGLE_TABLE, el campo `descuento` de `Empresa` no podría tener NOT NULL porque los registros de `Particular` lo tendrían a NULL, perdiendo garantía de integridad.

---

## 3. Arquitectura de la aplicación

> **[APRENDE]** La sección de arquitectura documenta la estructura lógica del sistema: cómo están organizadas las capas, qué responsabilidad tiene cada una y cómo se comunican entre sí. Es distinta del diagrama de despliegue (que muestra dónde corre el código) y del modelo de datos (que muestra cómo se almacena). Esta sección responde a la pregunta: "si tengo que añadir una nueva funcionalidad, ¿dónde la pongo y por qué?" Una arquitectura bien documentada hace que cualquier desarrollador nuevo pueda integrarse en días, no semanas.

### 3.1 Estructura de paquetes

```
com.cafeteria/
│
├── CafeteriaApplication.java        ← Punto de entrada, @SpringBootApplication
│
├── model/                           ← Entidades JPA (@Entity). Representan el dominio.
│   │                                   Son el único lugar donde vive el estado persistente.
│   ├── Cliente.java                 ← Clase abstracta: base de la jerarquía de clientes
│   ├── Empresa.java                 ← Subtipo de Cliente con descuento
│   ├── Particular.java              ← Subtipo de Cliente sin campos adicionales
│   ├── Camarero.java                ← Personal del establecimiento
│   ├── Producto.java                ← Artículos del catálogo
│   ├── CabeceraTicket.java          ← Cabecera del ticket de compra
│   └── LineaTicket.java             ← Línea individual dentro de un ticket
│
├── repository/                      ← Interfaces Spring Data JPA (@Repository implícito).
│   │                                   Acceso a datos sin SQL manual. Nunca contienen lógica de negocio.
│   ├── ClienteRepository.java
│   ├── EmpresaRepository.java
│   ├── ParticularRepository.java
│   ├── CamareroRepository.java
│   ├── ProductoRepository.java
│   ├── CabeceraTicketRepository.java
│   └── LineaTicketRepository.java
│
├── service/                         ← Lógica de negocio (@Service, @Transactional).
│   │                                   Orquestan llamadas a repositorios y aplican reglas.
│   ├── ClienteService.java
│   ├── CamareroService.java
│   ├── ProductoService.java
│   └── TicketService.java
│
└── controller/                      ← Capa HTTP (@RestController).
    │                                   Reciben peticiones, delegan en services, devuelven respuestas.
    │                                   NO contienen lógica de negocio.
    ├── ClienteController.java
    ├── CamareroController.java
    ├── ProductoController.java
    └── TicketController.java
```

**Regla de dependencias (siempre hacia abajo, nunca hacia arriba):**

```
Controller → Service → Repository → Model
```

Ninguna capa inferior conoce la existencia de la capa superior. Un `Repository` nunca llama a un `Service`. Un `Service` nunca llama a un `Controller`.

### 3.2 Flujo de una petición HTTP

El siguiente diagrama muestra el flujo completo de `POST /api/tickets/{numTicket}/lineas` — la operación más representativa del sistema porque involucra todas las capas y dos repositorios distintos.

```
Cliente HTTP
    │
    │  POST /api/tickets/TICKET-001/lineas
    │  Body: { "idProducto": "uuid-xyz", "cantidad": 2 }
    │
    ▼
TicketController.agregarLinea(@PathVariable numTicket, @RequestBody body)
    │
    │  Extrae idProducto y cantidad del Map<String,Object>
    │  Llama a: service.agregarLinea("TICKET-001", "uuid-xyz", 2)
    │
    ▼
TicketService.agregarLinea(numTicket, idProducto, cantidad)    [@Transactional]
    │
    ├──► ticketRepo.findById("TICKET-001")
    │        │
    │        ▼
    │    CabeceraTicketRepository → SELECT * FROM cabecera_ticket WHERE num_ticket='TICKET-001'
    │        │
    │        ◄── CabeceraTicket (o RuntimeException si no existe)
    │
    ├──► productoRepo.findById("uuid-xyz")
    │        │
    │        ▼
    │    ProductoRepository → SELECT * FROM producto WHERE id_producto='uuid-xyz'
    │        │
    │        ◄── Producto (o RuntimeException si no existe)
    │
    │  new LineaTicket(producto, 2)
    │      → precioUnitario = producto.getPrecio()   [snapshot del precio actual]
    │      → subtotal = precioUnitario * 2
    │
    │  ticket.agregarLinea(linea)
    │      → linea.setCabeceraTicket(ticket)         [relación bidireccional]
    │      → ticket.lineas.add(linea)
    │      → ticket.recalcularTotal()                [suma todos los subtotales]
    │
    ├──► ticketRepo.save(ticket)
    │        │
    │        ▼
    │    Hibernate → UPDATE cabecera_ticket SET total=... WHERE num_ticket='TICKET-001'
    │              → INSERT INTO linea_ticket (num_ticket, id_producto, cantidad, ...) VALUES (...)
    │        │
    │        ◄── CabeceraTicket actualizado con la nueva línea
    │
    ◄── CabeceraTicket
    │
TicketController
    │
    │  ResponseEntity.ok(ticket)  →  HTTP 200 + JSON body
    │
    ▼
Cliente HTTP recibe la respuesta
```

### 3.3 Patrón Repository y derived queries

> **[APRENDE]** Spring Data JPA es una de las abstracciones más poderosas del ecosistema Spring. El patrón Repository elimina el código boilerplate de acceso a datos: no hay `EntityManager` explícito, no hay `TypedQuery`, no hay gestión de conexiones. Simplemente defines una interfaz, la extiendes de `JpaRepository<Entidad, TipoPK>`, y Spring genera la implementación en tiempo de ejecución. Lo más importante que hay que entender aquí son las **derived queries**: métodos cuyo nombre DESCRIBE la query que se va a ejecutar. Spring las parsea y genera el SQL automáticamente. Son poderosas pero tienen límites — para queries complejas se usa `@Query`.

Spring Data JPA analiza el nombre del método y genera el SQL correspondiente siguiendo convenciones de nomenclatura:

| Método en el repository | SQL generado por Hibernate |
|---|---|
| `findByActivoTrue()` | `SELECT * FROM cliente WHERE activo = 1` |
| `findByNombreContainingIgnoreCase(String nombre)` | `SELECT * FROM cliente WHERE LOWER(nombre) LIKE LOWER('%?%')` |
| `findByClienteIdCliente(String id)` | `SELECT * FROM cabecera_ticket WHERE id_cliente = ?` |
| `findByCamareroIdCamarero(int id)` | `SELECT * FROM cabecera_ticket WHERE id_camarero = ?` |
| `findByFechaBetween(LocalDateTime a, LocalDateTime b)` | `SELECT * FROM cabecera_ticket WHERE fecha BETWEEN ? AND ?` |
| `findByCabeceraTicketNumTicket(String num)` | `SELECT * FROM linea_ticket WHERE num_ticket = ?` |
| `findByNif(String nif)` | `SELECT * FROM camarero WHERE nif = ?` |
| `findByDescripcionContainingIgnoreCase(String desc)` | `SELECT * FROM producto WHERE LOWER(descripcion) LIKE LOWER('%?%')` |

La convención es: `findBy` + `NombreCampo` + `Condición`. Para navegar relaciones se usa `_` o PascalCase: `findByClienteIdCliente` significa "busca por el campo `idCliente` de la relación `cliente`".

---

## 4. Configuración

> **[APRENDE]** La sección de configuración es crítica para operaciones. Un desarrollador puede entender el código leyéndolo, pero un equipo de DevOps necesita saber exactamente qué propiedades existen, qué hace cada una y cuáles son peligrosas en producción. Un `show-sql=true` en producción puede exponer información sensible en logs. Un `ddl-auto=create-drop` en producción destruye la base de datos al arrancar. Documenta TODAS las propiedades, incluso las que parecen obvias.

### 4.1 `application.properties`

| Propiedad | Valor actual | Descripción | Notas para producción |
|---|---|---|---|
| `spring.datasource.url` | `jdbc:mysql://localhost:3306/cafeteria?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC` | URL de conexión JDBC a MySQL | Cambiar `localhost` por el host real; activar `useSSL=true` |
| `spring.datasource.username` | `user` | Usuario de la base de datos | Usar variable de entorno; nunca hardcodear |
| `spring.datasource.password` | `0000` | Contraseña del usuario de BD | Usar variable de entorno; nunca hardcodear |
| `spring.datasource.driver-class-name` | `com.mysql.cj.jdbc.Driver` | Driver JDBC de MySQL 8 | No cambiar salvo migración de driver |
| `spring.jpa.hibernate.ddl-auto` | `update` | Hibernate actualiza el esquema al arrancar si detecta cambios | En producción: `validate` o `none`; nunca `create-drop` |
| `spring.jpa.show-sql` | `true` | Imprime cada sentencia SQL en los logs | Poner `false` en producción — degrada rendimiento y llena logs |
| `spring.jpa.properties.hibernate.format_sql` | `true` | Formatea el SQL impreso para legibilidad | Solo útil en desarrollo; poner `false` en producción |
| `spring.jpa.properties.hibernate.dialect` | `org.hibernate.dialect.MySQL8Dialect` | Dialecto SQL específico de MySQL 8 | Correcto para MySQL 8.0 |
| `server.port` | `8080` | Puerto en el que escucha el servidor embebido Tomcat | Configurable por entorno |

### 4.2 Docker Compose

El fichero `docker-compose.yml` define dos servicios para el entorno de desarrollo local:

**Servicio `db`**

| Parámetro | Valor | Descripción |
|---|---|---|
| Imagen | `mysql:8.0` | MySQL versión 8 |
| Container name | `db` | Nombre fijo del contenedor |
| `restart` | `always` | Se reinicia automáticamente si falla |
| Puerto | `3306:3306` | Mapea el puerto MySQL al host local |
| `MYSQL_ROOT_PASSWORD` | `root` | Password del usuario root — solo desarrollo |
| `MYSQL_DATABASE` | `cafeteria` | Crea la base de datos automáticamente |
| `MYSQL_USER` | `user` | Usuario de la aplicación |
| `MYSQL_PASSWORD` | `0000` | Password del usuario de la aplicación |
| Volumen bind mount | `./scripts/init.sql → /docker-entrypoint-initdb.d/` | Ejecuta el script SQL al crear el contenedor por primera vez |
| Volumen named | `mysql_data` | Persiste los datos entre reinicios del contenedor |

**Servicio `adminer`**

| Parámetro | Valor | Descripción |
|---|---|---|
| Imagen | `adminer` | Interfaz web para administrar la base de datos |
| Container name | `cafeteria_adminer` | Nombre fijo del contenedor |
| Puerto | `8080:8080` | Accesible en `http://localhost:8080` |
| `depends_on` | `db` | Espera a que el servicio `db` esté levantado antes de arrancar |

> **ATENCION:** En el `docker-compose.yml` actual, Adminer ocupa el puerto 8080, que es el mismo que usa la aplicación Spring Boot. Al levantar el compose, la aplicación Spring Boot debe correr fuera de Docker o en un puerto distinto (ej. 8081 via `server.port=8081` en desarrollo).

**Volúmenes declarados:**

| Nombre | Tipo | Propósito |
|---|---|---|
| `mysql_data` | Named volume | Persiste los datos de MySQL entre reinicios |

### 4.3 Variables de entorno necesarias para producción

En producción las credenciales y la configuración sensible nunca deben estar hardcodeadas en `application.properties`. Spring Boot soporta sobreescritura mediante variables de entorno con la convención `SPRING_DATASOURCE_URL` (puntos y guiones se convierten a guiones bajos y mayúsculas):

| Variable de entorno | Propiedad que sobreescribe | Ejemplo de valor |
|---|---|---|
| `SPRING_DATASOURCE_URL` | `spring.datasource.url` | `jdbc:mysql://db-prod:3306/cafeteria?useSSL=true&serverTimezone=UTC` |
| `SPRING_DATASOURCE_USERNAME` | `spring.datasource.username` | `cafeteria_app` |
| `SPRING_DATASOURCE_PASSWORD` | `spring.datasource.password` | _(secret manager)_ |
| `SPRING_JPA_HIBERNATE_DDL_AUTO` | `spring.jpa.hibernate.ddl-auto` | `validate` |
| `SPRING_JPA_SHOW_SQL` | `spring.jpa.show-sql` | `false` |
| `SERVER_PORT` | `server.port` | `8080` |

---

## 5. API REST — Especificación completa

> **[APRENDE]** La especificación de la API es la sección más consultada de toda la documentación técnica. Es el contrato entre el backend y cualquier consumidor: frontend, otra API, scripts de integración, QA. Debe estar al nivel de detalle que permita a alguien consumir la API SIN leer el código fuente. Documenta todos los endpoints, los esquemas de request y response (con tipos de datos concretos), y todos los posibles códigos HTTP de respuesta — incluyendo los de error. Una API sin documentar es una API que nadie puede usar correctamente.

Prefijo base de todos los endpoints: `http://localhost:8080`

Los cuerpos de request y response son JSON. El servidor no implementa autenticación, por lo que todos los endpoints son públicos.

---

### 5.1 Clientes — `/api/clientes`

| Método | Path | Descripción |
|---|---|---|
| `GET` | `/api/clientes` | Lista todos los clientes activos (`activo = true`) |
| `GET` | `/api/clientes/{id}` | Obtiene un cliente por su UUID |
| `GET` | `/api/clientes/empresas` | Lista todas las empresas activas |
| `GET` | `/api/clientes/particulares` | Lista todos los particulares activos |
| `POST` | `/api/clientes/empresa` | Crea una nueva empresa |
| `POST` | `/api/clientes/particular` | Crea un nuevo particular |
| `PUT` | `/api/clientes/{id}` | Actualiza los datos de un cliente existente |
| `DELETE` | `/api/clientes/{id}` | Marca el cliente como inactivo (soft-delete) |

**GET /api/clientes** — Response 200:
```json
[
  {
    "idCliente": "550e8400-e29b-41d4-a716-446655440000",
    "numeroCliente": 1,
    "nombre": "Acme S.L.",
    "primerApellido": null,
    "segundoApellido": null,
    "direccion": "Calle Mayor 1, Madrid",
    "telefono": "912345678",
    "email": "info@acme.es",
    "fechaAlta": "2024-01-15T10:30:00",
    "activo": true,
    "descuento": 15.00
  }
]
```

**POST /api/clientes/empresa** — Request Body:
```json
{
  "numeroCliente": 1,
  "nombre": "Acme S.L.",
  "direccion": "Calle Mayor 1, Madrid",
  "telefono": "912345678",
  "email": "info@acme.es",
  "descuento": 15.00
}
```

**POST /api/clientes/particular** — Request Body:
```json
{
  "numeroCliente": 2,
  "nombre": "Juan",
  "primerApellido": "García",
  "segundoApellido": "López",
  "telefono": "612345678",
  "email": "juan@email.es"
}
```

| Endpoint | Códigos HTTP posibles |
|---|---|
| `GET /api/clientes` | 200 OK |
| `GET /api/clientes/{id}` | 200 OK, 404 Not Found |
| `GET /api/clientes/empresas` | 200 OK |
| `GET /api/clientes/particulares` | 200 OK |
| `POST /api/clientes/empresa` | 200 OK |
| `POST /api/clientes/particular` | 200 OK |
| `PUT /api/clientes/{id}` | 200 OK, 404 Not Found |
| `DELETE /api/clientes/{id}` | 204 No Content |

---

### 5.2 Camareros — `/api/camareros`

| Método | Path | Descripción |
|---|---|---|
| `GET` | `/api/camareros` | Lista todos los camareros activos (`activo = true`) |
| `GET` | `/api/camareros/{id}` | Obtiene un camarero por su id numérico |
| `POST` | `/api/camareros` | Crea un nuevo camarero |
| `PUT` | `/api/camareros/{id}` | Actualiza los datos de un camarero existente |
| `DELETE` | `/api/camareros/{id}` | Marca el camarero como inactivo (soft-delete) |

**POST /api/camareros** — Request Body:
```json
{
  "nif": "12345678A",
  "nombre": "María Pérez",
  "fechaIncorporacion": "2023-06-01"
}
```

**Response 200** (ejemplo):
```json
{
  "idCamarero": 1,
  "nif": "12345678A",
  "nombre": "María Pérez",
  "fechaIncorporacion": "2023-06-01",
  "activo": true
}
```

| Endpoint | Códigos HTTP posibles |
|---|---|
| `GET /api/camareros` | 200 OK |
| `GET /api/camareros/{id}` | 200 OK, 404 Not Found |
| `POST /api/camareros` | 200 OK |
| `PUT /api/camareros/{id}` | 200 OK, 404 Not Found |
| `DELETE /api/camareros/{id}` | 204 No Content |

---

### 5.3 Productos — `/api/productos`

| Método | Path | Descripción |
|---|---|---|
| `GET` | `/api/productos` | Lista todos los productos activos |
| `GET` | `/api/productos/{id}` | Obtiene un producto por su UUID |
| `POST` | `/api/productos` | Crea un nuevo producto |
| `PUT` | `/api/productos/{id}` | Actualiza los datos de un producto existente |
| `DELETE` | `/api/productos/{id}` | Marca el producto como inactivo (soft-delete) |

**POST /api/productos** — Request Body:
```json
{
  "descripcion": "Café con leche",
  "stock": 100,
  "precio": 1.80
}
```

**Response 200** (ejemplo):
```json
{
  "idProducto": "a3bb189e-8bf9-3888-9912-ace4e6543002",
  "descripcion": "Café con leche",
  "stock": 100,
  "precio": 1.80,
  "fechaAlta": "2024-03-15T09:00:00",
  "activo": true
}
```

| Endpoint | Códigos HTTP posibles |
|---|---|
| `GET /api/productos` | 200 OK |
| `GET /api/productos/{id}` | 200 OK, 404 Not Found |
| `POST /api/productos` | 200 OK |
| `PUT /api/productos/{id}` | 200 OK, 404 Not Found |
| `DELETE /api/productos/{id}` | 204 No Content |

---

### 5.4 Tickets — `/api/tickets`

| Método | Path | Descripción |
|---|---|---|
| `GET` | `/api/tickets` | Lista todos los tickets |
| `GET` | `/api/tickets/{numTicket}` | Obtiene un ticket por su número |
| `GET` | `/api/tickets/cliente/{idCliente}` | Lista los tickets de un cliente concreto |
| `GET` | `/api/tickets/camarero/{idCamarero}` | Lista los tickets de un camarero concreto |
| `POST` | `/api/tickets` | Crea un nuevo ticket (cabecera vacía) |
| `POST` | `/api/tickets/{numTicket}/lineas` | Agrega una línea de producto al ticket |
| `DELETE` | `/api/tickets/{numTicket}` | Elimina un ticket y todas sus líneas (DELETE físico con CASCADE) |

**POST /api/tickets** — Request Body:
```json
{
  "numTicket": "TICKET-2024-001",
  "cliente": { "idCliente": "550e8400-e29b-41d4-a716-446655440000" },
  "camarero": { "idCamarero": 1 }
}
```

**POST /api/tickets/{numTicket}/lineas** — Request Body:
```json
{
  "idProducto": "a3bb189e-8bf9-3888-9912-ace4e6543002",
  "cantidad": 2
}
```

**Response 200** de agregar línea (ticket completo con líneas):
```json
{
  "numTicket": "TICKET-2024-001",
  "fecha": "2024-03-26T12:30:00",
  "total": 3.60,
  "cliente": { "idCliente": "...", "nombre": "Juan García" },
  "camarero": { "idCamarero": 1, "nombre": "María Pérez" },
  "lineas": [
    {
      "idLinea": 1,
      "producto": { "idProducto": "...", "descripcion": "Café con leche" },
      "cantidad": 2,
      "precioUnitario": 1.80,
      "subtotal": 3.60
    }
  ]
}
```

| Endpoint | Códigos HTTP posibles |
|---|---|
| `GET /api/tickets` | 200 OK |
| `GET /api/tickets/{numTicket}` | 200 OK, 404 Not Found |
| `GET /api/tickets/cliente/{idCliente}` | 200 OK |
| `GET /api/tickets/camarero/{idCamarero}` | 200 OK |
| `POST /api/tickets` | 200 OK |
| `POST /api/tickets/{numTicket}/lineas` | 200 OK, 500 si ticket o producto no existen |
| `DELETE /api/tickets/{numTicket}` | 204 No Content |

---

## 6. Decisiones de diseño

> **[APRENDE]** Esta es la sección más infrautilizada en documentación técnica y paradójicamente la más valiosa a largo plazo. Documenta el POR QUÉ, no el qué. El código ya dice qué hace el sistema. Lo que el código nunca dice es por qué se tomó esa decisión, qué alternativas se descartaron y cuáles son las consecuencias aceptadas. Sin esto, los desarrolladores futuros "arreglan" cosas que funcionan perfectamente, sin entender que la solución aparentemente subóptima existe por una razón. El formato ADR (Architecture Decision Record) es el estándar de la industria para esto.

---

### ADR-001 — Spring Boot vs Java plano con JDBC

**Contexto:** el proyecto necesita exponer una API REST con persistencia en MySQL. Se puede hacer con Java puro usando `HttpServer` + JDBC directo, sin frameworks.

**Decisión:** usar Spring Boot 3.2.5 con Spring Data JPA.

**Alternativas consideradas:**

| Opción | Pros | Contras |
|---|---|---|
| Java puro + JDBC | Control total, sin magia, cero dependencias | Boilerplate masivo: gestión de conexiones, mapeo manual ResultSet→objeto, parsing HTTP manual, serialización JSON manual |
| Spring Boot + JPA | Autoconfiguración, ORM, repositorios sin implementación, servidor embebido | Curva de aprendizaje, "magia" que oculta lo que ocurre, JVM pesada |
| Quarkus / Micronaut | Arranque más rápido, menor huella de memoria | Ecosistema menor, menos documentación, más fricción para aprendizaje |

**Consecuencias aceptadas:** el proyecto tiene dependencia del ecosistema Spring. El JAR resultante es más grande. A cambio, el código de acceso a datos es mínimo y el tiempo de desarrollo es significativamente menor.

---

### ADR-002 — InheritanceType.JOINED para la jerarquía Cliente

**Contexto:** `Cliente` tiene dos subtipos: `Empresa` (con descuento) y `Particular` (sin campos extra). JPA ofrece tres estrategias para mapear esto a tablas relacionales.

**Decisión:** `@Inheritance(strategy = InheritanceType.JOINED)`.

**Alternativas consideradas:**

| Estrategia | Descartada porque |
|---|---|
| `SINGLE_TABLE` | El campo `descuento` de `Empresa` no puede tener NOT NULL ya que los particulares lo tendrían a NULL. Se pierde integridad referencial a nivel de base de datos. |
| `TABLE_PER_CLASS` | No existe tabla `cliente` → `cabecera_ticket` no puede tener una FK que apunte a "cualquier tipo de cliente". Las queries polimórficas requieren UNION ALL, que escala mal. |

**Consecuencias aceptadas:** cada consulta polimórfica sobre `Cliente` genera un JOIN entre `cliente` y la tabla de subtipo correspondiente. Para el volumen de datos esperado en una cafetería, este coste es completamente asumible.

---

### ADR-003 — `precio_unitario` como snapshot en `linea_ticket`

**Contexto:** `linea_ticket` tiene un campo `precio_unitario` que copia el precio del producto en el momento de la venta, en lugar de referenciar directamente `producto.precio`.

**Decisión:** copiar el precio en el constructor de `LineaTicket`:
```java
this.precioUnitario = producto.getPrecio();  // snapshot en el momento de la venta
```

**Alternativas consideradas:**

| Opción | Problema |
|---|---|
| Guardar solo la FK a producto y leer el precio desde allí | Si el precio del producto cambia después de la venta, el histórico de tickets quedaría corrupto — mostraría importes distintos a los cobrados realmente |
| Sistema de versiones de precios (tabla `precio_historico`) | Sobreingeniería para el alcance actual; complejidad innecesaria |

**Consecuencias aceptadas:** el precio en `linea_ticket` es inmutable una vez guardado. Cambiar el precio de un producto no afecta a los tickets históricos. El campo `subtotal` también se calcula y persiste para evitar recalcularlo en cada lectura.

---

### ADR-004 — Soft-delete (`activo = false`) en lugar de DELETE físico

**Contexto:** los endpoints `DELETE` de clientes, camareros y productos no eliminan el registro de la base de datos. Ponen `activo = false`.

**Decisión:** implementar soft-delete en `Cliente`, `Camarero` y `Producto`. Los repositorios de lectura usan `findByActivoTrue()` para filtrar los inactivos.

**Alternativas consideradas:**

| Opción | Problema |
|---|---|
| DELETE físico | Si un cliente o camarero tiene tickets históricos, borrarlos físicamente viola integridad referencial (o requiere CASCADE que borra tickets, perdiendo historial) |
| DELETE físico solo si no tienen relaciones | Requiere lógica adicional de comprobación antes de cada borrado; errores en producción difíciles de depurar |

**Consecuencias aceptadas:** los registros "borrados" siguen ocupando espacio en la base de datos. Las consultas de listado deben siempre filtrar por `activo = true` (los repositorios ya lo hacen). Es necesario implementar un proceso de limpieza periódica si el volumen crece. En contrapartida, el historial de tickets siempre permanece coherente y auditable.

> **Nota:** `TicketService.eliminarTicket()` sí hace un DELETE físico, porque las `linea_ticket` se borran en cascada (FK con ON DELETE CASCADE en el DDL) y el ticket en sí no suele tener dependencias externas que preservar.

---

## 7. Limitaciones conocidas y mejoras futuras

> **[APRENDE]** Toda documentación técnica honesta reconoce sus propias limitaciones. Esta sección es señal de madurez del equipo, no de debilidad. Sirve para tres cosas: (1) gestionar expectativas de quien usa el sistema, (2) guiar el trabajo futuro, y (3) evitar que alguien intente usar el sistema para algo para lo que no está preparado. Un sistema sin esta sección transmite una falsa sensación de completitud que lleva a problemas en producción.

### Limitaciones actuales

| Area | Limitación | Impacto |
|---|---|---|
| **Seguridad** | No hay autenticación ni autorización. Cualquier persona con acceso a la red puede leer y modificar todos los datos. | Crítico — no apto para entorno con datos reales sin añadir Spring Security |
| **Validación** | El proyecto incluye `spring-boot-starter-validation` como dependencia, pero las entidades no tienen anotaciones `@NotNull`, `@Size`, etc. Un POST con campos nulos puede generar errores 500 en lugar de 400. | Alto — los errores de validación no son informativos para el cliente HTTP |
| **Gestión de errores** | No existe `@ControllerAdvice` global. Las excepciones lanzadas en el service (ej. `RuntimeException("Ticket no encontrado")`) devuelven HTTP 500, no 404. | Alto — el cliente HTTP recibe respuestas de error no estructuradas |
| **Stock** | Agregar una línea de ticket no descuenta stock. `ProductoService.actualizarStock()` existe pero no se llama desde `TicketService.agregarLinea()`. | Medio — el stock en base de datos no refleja las ventas reales |
| **Paginación** | Los endpoints de listado devuelven todos los registros sin paginación. Con un volumen alto de datos, estos endpoints pueden causar timeouts o consumo excesivo de memoria. | Medio — requiere `Pageable` en los repositories |
| **Tests** | No existe ningún test (unitario, de integración ni e2e). | Alto — cualquier cambio puede romper funcionalidad sin que haya detección automática |
| **Documentación de API** | No hay Swagger/OpenAPI. La especificación de la API está solo en este documento. | Medio — no hay contrato ejecutable ni interfaz de prueba interactiva |
| **Logs estructurados** | No hay configuración de logging estructurado (Logback/JSON). | Bajo — dificulta la monitorización en producción |
| **Transaccionalidad en ClienteService** | Los métodos de `ClienteService` no tienen `@Transactional`. En caso de fallo parcial en una operación que involucre múltiples saves, el estado podría quedar inconsistente. | Bajo-Medio |
| **Conflicto de puertos** | En `docker-compose.yml`, Adminer ocupa el puerto 8080, igual que la aplicación Spring Boot. | Bajo — solo afecta en desarrollo local |

### Mejoras futuras recomendadas

1. **Autenticación JWT con Spring Security** — proteger todos los endpoints con roles (ADMIN, CAMARERO)
2. **`@ControllerAdvice` global** — centralizar el manejo de excepciones y devolver respuestas de error en formato estándar (RFC 7807 Problem Details)
3. **Bean Validation** — anotar las entidades con `@NotBlank`, `@Positive`, `@Email`, etc. y activar `@Valid` en los controllers
4. **Descuento de stock en `agregarLinea`** — llamar a `actualizarStock()` dentro de la transacción de `TicketService.agregarLinea()`
5. **Paginación** — añadir `Pageable` a los endpoints de listado y devolver `Page<T>`
6. **Tests** — tests unitarios del service con Mockito, tests de integración con `@SpringBootTest` y base de datos H2 en memoria o Testcontainers con MySQL real
7. **OpenAPI/Swagger** — añadir `springdoc-openapi-starter-webmvc-ui` para generar documentación interactiva automáticamente
8. **DTOs** — separar el modelo de dominio (entidades JPA) de los objetos de transferencia de datos para evitar exponer la estructura interna en la API
9. **Migración de esquema con Flyway o Liquibase** — reemplazar `ddl-auto=update` por migraciones versionadas y reproducibles
