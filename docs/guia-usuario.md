# Guia de Usuario — API Cafeteria

> **[APRENDE]** Una guia de usuario de una API REST es el primer documento que lee alguien que quiere integrar o consumir tu servicio. No es documentacion tecnica interna ni arquitectura: es la respuesta a la pregunta "¿Como lo uso yo, desde fuera?". Va dirigida a desarrolladores o estudiantes que conocen HTTP y JSON, pero NO conocen las decisiones de diseno de este proyecto. Su objetivo es que alguien pueda hacer su primera llamada exitosa en menos de diez minutos.

---

## 1. ¿Que hace esta aplicacion?

> **[APRENDE]** La seccion de descripcion funcional responde a "¿que problema resuelve?" antes de explicar nada tecnico. Aqui no hablas de clases, servicios ni base de datos. Hablas de lo que el usuario puede hacer con la aplicacion y del dominio de negocio que modela. Si alguien lee solo esta seccion, debe entender si esta API le sirve o no.

La API de Cafeteria gestiona las operaciones basicas de un negocio de hosteleria: el personal (camareros), el catalogo de productos, los clientes y los tickets de venta.

Con esta API puedes:

- Dar de alta, modificar y dar de baja **camareros**.
- Gestionar el **catalogo de productos** con precio y stock.
- Registrar **clientes**, distinguiendo entre particulares (personas fisicas) y empresas (con descuento aplicado).
- Crear **tickets de venta** y asociarles lineas de producto con su cantidad y precio unitario. El total del ticket se recalcula automaticamente al agregar lineas.

La aplicacion aplica **baja logica** en camareros, clientes y productos: nunca se eliminan fisicamente del sistema, simplemente se marcan como inactivos. Los endpoints de listado devuelven unicamente los registros activos.

---

## 2. Requisitos previos

> **[APRENDE]** La seccion de prerrequisitos evita que el usuario pierda tiempo intentando ejecutar algo para lo que no tiene el entorno preparado. Debe ser una lista concreta y sin ambiguedades: versiones exactas, herramientas obligatorias y opcionales, y nada mas. Un prerequisito que no se menciona es un bug en tu documentacion.

Para ejecutar y consumir esta API necesitas tener instalado:

| Herramienta | Version minima | Para que se usa |
|-------------|---------------|-----------------|
| **Java** | 21 | Ejecutar la aplicacion Spring Boot |
| **Maven** | 3.9 | Compilar y lanzar el proyecto |
| **Docker** y **Docker Compose** | cualquiera reciente | Levantar la base de datos |
| **curl** o **Postman** | cualquiera | Hacer peticiones HTTP a la API |

Verifica que los tienes disponibles antes de continuar:

```bash
java --version
mvn --version
docker --version
docker compose version
curl --version
```

---

## 3. Puesta en marcha

> **[APRENDE]** La seccion "getting started" (o puesta en marcha) contiene los pasos minimos y en orden para tener algo funcionando. No es un manual de instalacion exhaustivo: es la ruta mas corta desde cero hasta la primera respuesta exitosa. Cada paso debe ser un comando real y ejecutable. Al final de esta seccion el usuario ya sabe que la API esta viva.

### Paso 1: Levantar la base de datos

Desde la raiz del proyecto, arranca el contenedor con la base de datos:

```bash
docker compose up -d
```

### Paso 2: Arrancar la aplicacion

```bash
mvn spring-boot:run
```

Espera a ver en la consola algo similar a:

```
Started CafeteriaApplication in X.XXX seconds
```

### Paso 3: Verificar que esta operativa

```bash
curl http://localhost:8080/api/productos
```

Si la respuesta es `[]` (lista vacia) o una lista de productos en JSON, la API esta funcionando correctamente.

---

## 4. Referencia de la API

> **[APRENDE]** La seccion de referencia es el corazon de la documentacion de una API. Aqui listas TODAS las operaciones disponibles, sin omitir ninguna. Para cada endpoint documentas: el metodo HTTP y la URL, lo que hace, el cuerpo de la peticion si lo tiene, un ejemplo de respuesta exitosa y los codigos de error posibles. El objetivo es que un usuario pueda usar esta seccion como consulta rapida sin tener que leer el codigo fuente.

La URL base de todos los endpoints es:

```
http://localhost:8080
```

Todos los cuerpos de peticion y respuesta son en formato **JSON**. Las peticiones con body deben incluir la cabecera:

```
Content-Type: application/json
```

---

### 4.1 Camareros

Base: `/api/camareros`

Los camareros se identifican con un `id` numerico generado automaticamente por la base de datos. El campo `nif` es unico en el sistema. El campo `activo` no se envia en el alta; el sistema lo establece a `true` por defecto.

---

#### GET /api/camareros

Devuelve la lista de todos los camareros activos.

**Respuesta exitosa** `200 OK`:

```json
[
  {
    "idCamarero": 1,
    "nif": "12345678A",
    "nombre": "Lucia Fernandez",
    "fechaIncorporacion": "2023-09-01",
    "activo": true
  },
  {
    "idCamarero": 2,
    "nif": "87654321B",
    "nombre": "Marcos Ruiz",
    "fechaIncorporacion": "2024-01-15",
    "activo": true
  }
]
```

---

#### GET /api/camareros/{id}

Devuelve un camarero por su identificador numerico.

**Parametros de ruta**: `id` — entero, identificador del camarero.

**Respuesta exitosa** `200 OK`:

```json
{
  "idCamarero": 1,
  "nif": "12345678A",
  "nombre": "Lucia Fernandez",
  "fechaIncorporacion": "2023-09-01",
  "activo": true
}
```

**Errores posibles**:

| Codigo | Causa |
|--------|-------|
| `404 Not Found` | No existe un camarero con ese `id` |

---

#### POST /api/camareros

Crea un nuevo camarero.

**Body**:

```json
{
  "nif": "11223344C",
  "nombre": "Elena Torres",
  "fechaIncorporacion": "2026-03-26"
}
```

**Respuesta exitosa** `200 OK` (devuelve el camarero creado con su `id` asignado):

```json
{
  "idCamarero": 3,
  "nif": "11223344C",
  "nombre": "Elena Torres",
  "fechaIncorporacion": "2026-03-26",
  "activo": true
}
```

**Errores posibles**:

| Codigo | Causa |
|--------|-------|
| `400 Bad Request` | Campos obligatorios ausentes o `nif` duplicado |

---

#### PUT /api/camareros/{id}

Actualiza los datos de un camarero existente.

**Parametros de ruta**: `id` — identificador del camarero a modificar.

**Body** (misma estructura que el alta):

```json
{
  "nif": "11223344C",
  "nombre": "Elena Torres Moreno",
  "fechaIncorporacion": "2026-03-26"
}
```

**Respuesta exitosa** `200 OK` (devuelve el camarero actualizado).

**Errores posibles**:

| Codigo | Causa |
|--------|-------|
| `404 Not Found` | No existe un camarero con ese `id` |

---

#### DELETE /api/camareros/{id}

Da de baja logica a un camarero (lo marca como inactivo, no lo elimina).

**Parametros de ruta**: `id` — identificador del camarero.

**Respuesta exitosa** `204 No Content` (sin cuerpo).

**Errores posibles**:

| Codigo | Causa |
|--------|-------|
| `404 Not Found` | No existe un camarero con ese `id` |

---

### 4.2 Clientes

Base: `/api/clientes`

El sistema distingue dos tipos de cliente:

- **Particular**: persona fisica. No tiene campos adicionales mas alla de los comunes.
- **Empresa**: tiene un campo `descuento` (porcentaje, p. ej. `10.00` para un 10%).

El `idCliente` es un UUID generado automaticamente en el servidor. El `numeroCliente` es un entero correlativo unico.

---

#### GET /api/clientes

Devuelve todos los clientes activos (tanto particulares como empresas).

**Respuesta exitosa** `200 OK`:

```json
[
  {
    "idCliente": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
    "numeroCliente": 1,
    "nombre": "Construcciones",
    "primerApellido": null,
    "segundoApellido": null,
    "direccion": "Calle Mayor 10, Madrid",
    "telefono": "910000001",
    "email": "info@construcciones.es",
    "fechaAlta": "2026-01-10T09:00:00",
    "activo": true,
    "descuento": 15.0
  }
]
```

---

#### GET /api/clientes/{id}

Devuelve un cliente por su UUID.

**Parametros de ruta**: `id` — UUID del cliente.

**Respuesta exitosa** `200 OK` (el objeto del cliente).

**Errores posibles**:

| Codigo | Causa |
|--------|-------|
| `404 Not Found` | No existe un cliente con ese UUID |

---

#### GET /api/clientes/empresas

Devuelve unicamente los clientes de tipo empresa.

**Respuesta exitosa** `200 OK` (lista de empresas con su campo `descuento`).

---

#### GET /api/clientes/particulares

Devuelve unicamente los clientes de tipo particular.

**Respuesta exitosa** `200 OK` (lista de particulares).

---

#### POST /api/clientes/particular

Crea un nuevo cliente particular.

**Body**:

```json
{
  "nombre": "Carlos",
  "primerApellido": "Garcia",
  "segundoApellido": "Lopez",
  "direccion": "Avenida de la Paz 5, Valencia",
  "telefono": "600111222",
  "email": "carlos.garcia@email.com"
}
```

**Respuesta exitosa** `200 OK` (devuelve el particular creado con su UUID asignado).

---

#### POST /api/clientes/empresa

Crea un nuevo cliente empresa.

**Body**:

```json
{
  "nombre": "Transportes Ibericos SL",
  "direccion": "Poligono Industrial Norte, nave 7, Zaragoza",
  "telefono": "976000123",
  "email": "admin@transportesibericos.es",
  "descuento": 10.0
}
```

**Respuesta exitosa** `200 OK` (devuelve la empresa creada con su UUID asignado).

---

#### PUT /api/clientes/{id}

Actualiza los datos de un cliente existente (particular o empresa).

**Parametros de ruta**: `id` — UUID del cliente.

**Body**: mismo esquema que el tipo de cliente correspondiente.

**Respuesta exitosa** `200 OK` (devuelve el cliente actualizado).

**Errores posibles**:

| Codigo | Causa |
|--------|-------|
| `404 Not Found` | No existe un cliente con ese UUID |

---

#### DELETE /api/clientes/{id}

Da de baja logica a un cliente.

**Parametros de ruta**: `id` — UUID del cliente.

**Respuesta exitosa** `204 No Content`.

**Errores posibles**:

| Codigo | Causa |
|--------|-------|
| `404 Not Found` | No existe un cliente con ese UUID |

---

### 4.3 Productos

Base: `/api/productos`

El `idProducto` es un UUID generado automaticamente. El campo `activo` lo gestiona el sistema; no se envia en el alta.

---

#### GET /api/productos

Devuelve todos los productos activos.

**Respuesta exitosa** `200 OK`:

```json
[
  {
    "idProducto": "f7e8d9c0-b1a2-3456-cdef-789012345678",
    "descripcion": "Cafe con leche",
    "stock": 200,
    "precio": 1.80,
    "fechaAlta": "2026-01-01T08:00:00",
    "activo": true
  },
  {
    "idProducto": "a2b3c4d5-e6f7-8901-abcd-ef1234567890",
    "descripcion": "Tostada con tomate",
    "stock": 50,
    "precio": 2.50,
    "fechaAlta": "2026-01-01T08:00:00",
    "activo": true
  }
]
```

---

#### GET /api/productos/{id}

Devuelve un producto por su UUID.

**Parametros de ruta**: `id` — UUID del producto.

**Respuesta exitosa** `200 OK` (el objeto del producto).

**Errores posibles**:

| Codigo | Causa |
|--------|-------|
| `404 Not Found` | No existe un producto con ese UUID |

---

#### POST /api/productos

Crea un nuevo producto en el catalogo.

**Body**:

```json
{
  "descripcion": "Zumo de naranja natural",
  "stock": 30,
  "precio": 2.20
}
```

**Respuesta exitosa** `200 OK` (devuelve el producto creado con su UUID asignado).

---

#### PUT /api/productos/{id}

Actualiza los datos de un producto.

**Parametros de ruta**: `id` — UUID del producto.

**Body** (misma estructura que el alta):

```json
{
  "descripcion": "Zumo de naranja natural grande",
  "stock": 25,
  "precio": 2.80
}
```

**Respuesta exitosa** `200 OK` (devuelve el producto actualizado).

**Errores posibles**:

| Codigo | Causa |
|--------|-------|
| `404 Not Found` | No existe un producto con ese UUID |

---

#### DELETE /api/productos/{id}

Da de baja logica a un producto (deja de aparecer en el catalogo activo).

**Parametros de ruta**: `id` — UUID del producto.

**Respuesta exitosa** `204 No Content`.

**Errores posibles**:

| Codigo | Causa |
|--------|-------|
| `404 Not Found` | No existe un producto con ese UUID |

---

### 4.4 Tickets

Base: `/api/tickets`

Un ticket representa una venta. Tiene una **cabecera** (datos generales: numero, fecha, camarero, cliente, total) y **lineas** (cada producto consumido con su cantidad, precio unitario y subtotal). El total de la cabecera se recalcula automaticamente cada vez que se agrega una linea.

El `numTicket` es una cadena de texto que tu asignas al crear el ticket (por ejemplo: `"T-2026-001"`). La fecha se asigna automaticamente al momento de la creacion.

---

#### GET /api/tickets

Devuelve todos los tickets del sistema.

**Respuesta exitosa** `200 OK`:

```json
[
  {
    "numTicket": "T-2026-001",
    "fecha": "2026-03-26T10:30:00",
    "total": 5.50,
    "cliente": { "idCliente": "a1b2...", "nombre": "Carlos", ... },
    "camarero": { "idCamarero": 1, "nombre": "Lucia Fernandez", ... },
    "lineas": [
      {
        "idLinea": 1,
        "cantidad": 2,
        "precioUnitario": 1.80,
        "subtotal": 3.60
      },
      {
        "idLinea": 2,
        "cantidad": 1,
        "precioUnitario": 1.90,
        "subtotal": 1.90
      }
    ]
  }
]
```

---

#### GET /api/tickets/{numTicket}

Devuelve un ticket por su numero.

**Parametros de ruta**: `numTicket` — identificador textual del ticket.

**Respuesta exitosa** `200 OK` (el objeto del ticket con sus lineas).

**Errores posibles**:

| Codigo | Causa |
|--------|-------|
| `404 Not Found` | No existe un ticket con ese numero |

---

#### GET /api/tickets/cliente/{idCliente}

Devuelve todos los tickets asociados a un cliente.

**Parametros de ruta**: `idCliente` — UUID del cliente.

**Respuesta exitosa** `200 OK` (lista de tickets del cliente).

---

#### GET /api/tickets/camarero/{idCamarero}

Devuelve todos los tickets gestionados por un camarero.

**Parametros de ruta**: `idCamarero` — identificador numerico del camarero.

**Respuesta exitosa** `200 OK` (lista de tickets del camarero).

---

#### POST /api/tickets

Crea un nuevo ticket (cabecera). En este momento el ticket esta vacio, sin lineas, y el total es 0. Las lineas se agregan en un paso posterior.

**Body**:

```json
{
  "numTicket": "T-2026-001",
  "camarero": { "idCamarero": 1 },
  "cliente":  { "idCliente": "a1b2c3d4-e5f6-7890-abcd-ef1234567890" }
}
```

El campo `cliente` es opcional: un ticket puede pertenecer a un consumidor anonimo.

**Respuesta exitosa** `200 OK` (devuelve la cabecera del ticket creado):

```json
{
  "numTicket": "T-2026-001",
  "fecha": "2026-03-26T10:30:00",
  "total": 0.0,
  "lineas": []
}
```

---

#### POST /api/tickets/{numTicket}/lineas

Agrega una linea de producto a un ticket existente. El total de la cabecera se actualiza automaticamente.

**Parametros de ruta**: `numTicket` — identificador del ticket al que se agrega la linea.

**Body**:

```json
{
  "idProducto": "f7e8d9c0-b1a2-3456-cdef-789012345678",
  "cantidad": 2
}
```

**Respuesta exitosa** `200 OK` (devuelve el ticket completo con todas sus lineas y el total actualizado):

```json
{
  "numTicket": "T-2026-001",
  "fecha": "2026-03-26T10:30:00",
  "total": 3.60,
  "lineas": [
    {
      "idLinea": 1,
      "cantidad": 2,
      "precioUnitario": 1.80,
      "subtotal": 3.60
    }
  ]
}
```

**Errores posibles**:

| Codigo | Causa |
|--------|-------|
| `404 Not Found` | No existe el ticket o el producto indicado |

---

#### DELETE /api/tickets/{numTicket}

Elimina un ticket y todas sus lineas.

**Parametros de ruta**: `numTicket` — identificador del ticket.

**Respuesta exitosa** `204 No Content`.

---

## 5. Casos de uso completos

> **[APRENDE]** Los casos de uso (o "walkthroughs") son la seccion mas practica de la documentacion. Mientras la referencia describe cada operacion de forma aislada, aqui muestras flujos reales de principio a fin. El usuario ve como encajan las piezas: primero creo esto, luego uso su ID para crear aquello, y asi. Son especialmente utiles para detectar la dependencia entre recursos (no puedes crear un ticket sin tener antes un camarero).

### Flujo 1: Alta de camarero y productos, creacion de ticket con lineas

Este es el flujo basico de apertura: registrar un camarero, cargar los productos del dia y generar el primer ticket de la jornada.

#### Paso 1: Crear el camarero

```bash
curl -s -X POST http://localhost:8080/api/camareros \
  -H "Content-Type: application/json" \
  -d '{
    "nif": "11223344C",
    "nombre": "Elena Torres",
    "fechaIncorporacion": "2026-03-26"
  }'
```

Anota el `idCamarero` de la respuesta. Supongamos que es `1`.

#### Paso 2: Crear los productos

```bash
curl -s -X POST http://localhost:8080/api/productos \
  -H "Content-Type: application/json" \
  -d '{
    "descripcion": "Cafe con leche",
    "stock": 200,
    "precio": 1.80
  }'
```

Anota el `idProducto` del cafe. Supongamos que es `"cafe-uuid"`.

```bash
curl -s -X POST http://localhost:8080/api/productos \
  -H "Content-Type: application/json" \
  -d '{
    "descripcion": "Tostada con tomate",
    "stock": 50,
    "precio": 2.50
  }'
```

Anota el `idProducto` de la tostada. Supongamos que es `"tostada-uuid"`.

#### Paso 3: Crear el ticket (sin lineas aun)

```bash
curl -s -X POST http://localhost:8080/api/tickets \
  -H "Content-Type: application/json" \
  -d '{
    "numTicket": "T-2026-001",
    "camarero": { "idCamarero": 1 }
  }'
```

#### Paso 4: Agregar la primera linea (2 cafes)

```bash
curl -s -X POST http://localhost:8080/api/tickets/T-2026-001/lineas \
  -H "Content-Type: application/json" \
  -d '{
    "idProducto": "cafe-uuid",
    "cantidad": 2
  }'
```

Total acumulado: `3.60 €`

#### Paso 5: Agregar la segunda linea (1 tostada)

```bash
curl -s -X POST http://localhost:8080/api/tickets/T-2026-001/lineas \
  -H "Content-Type: application/json" \
  -d '{
    "idProducto": "tostada-uuid",
    "cantidad": 1
  }'
```

Total acumulado: `6.10 €`

#### Paso 6: Verificar el ticket completo

```bash
curl -s http://localhost:8080/api/tickets/T-2026-001
```

La respuesta mostrara la cabecera con `total: 6.10` y las dos lineas.

---

### Flujo 2: Alta de cliente empresa con descuento y ticket asociado

En este flujo registramos una empresa con descuento y le asociamos una consumicion. El descuento es un dato del cliente; la API no lo aplica automaticamente al total del ticket, es informacion disponible para el sistema externo que gestione la facturacion.

#### Paso 1: Crear la empresa

```bash
curl -s -X POST http://localhost:8080/api/clientes/empresa \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Construcciones Valera SL",
    "direccion": "Calle del Prado 22, Madrid",
    "telefono": "910234567",
    "email": "pedidos@valera.es",
    "descuento": 15.0
  }'
```

Anota el `idCliente` (UUID) de la respuesta. Supongamos que es `"empresa-uuid"`.

#### Paso 2: Crear un ticket vinculado a esa empresa y a un camarero

(Asumiendo que el camarero con `idCamarero: 1` ya existe del flujo anterior.)

```bash
curl -s -X POST http://localhost:8080/api/tickets \
  -H "Content-Type: application/json" \
  -d '{
    "numTicket": "T-2026-002",
    "camarero": { "idCamarero": 1 },
    "cliente":  { "idCliente": "empresa-uuid" }
  }'
```

#### Paso 3: Agregar lineas al ticket

```bash
curl -s -X POST http://localhost:8080/api/tickets/T-2026-002/lineas \
  -H "Content-Type: application/json" \
  -d '{
    "idProducto": "cafe-uuid",
    "cantidad": 5
  }'
```

#### Paso 4: Consultar todos los tickets de esta empresa

```bash
curl -s http://localhost:8080/api/tickets/cliente/empresa-uuid
```

---

## 6. Errores comunes y soluciones

> **[APRENDE]** La seccion de troubleshooting (o solucion de problemas) anticipa los errores mas frecuentes que comete un usuario nuevo. No documentes todos los errores posibles de HTTP: documenta los que de verdad vas a ver. Un buen troubleshooting reduce el tiempo de integracion a la mitad porque el usuario no tiene que adivinar por que algo falla.

| Error | Causa probable | Solucion |
|-------|---------------|----------|
| `Connection refused` al llamar a la API | La aplicacion no ha arrancado o el puerto no es el correcto | Comprueba que `mvn spring-boot:run` ha terminado de iniciar. Verifica que usas el puerto `8080` |
| `Connection refused` al arrancar la app | La base de datos no esta disponible | Ejecuta primero `docker compose up -d` y espera a que el contenedor este `healthy` |
| `404 Not Found` al buscar un recurso | El ID no existe o el recurso esta dado de baja | Verifica el ID con el endpoint de listado (`GET /api/...`). Recuerda que los recursos dados de baja no aparecen en el listado |
| `400 Bad Request` al crear un camarero | El campo `nif` ya existe en el sistema | Cada camarero debe tener un NIF unico. Consulta el listado antes de insertar |
| `400 Bad Request` al crear cualquier recurso | Falta un campo obligatorio en el body | Revisa la estructura del body en la seccion de referencia. Los campos `nombre`, `nif`, `fechaIncorporacion`, `precio` y `stock` son obligatorios en sus respectivos recursos |
| El total del ticket es `0.0` | Se consulta el ticket recien creado, antes de agregar lineas | El total solo se calcula al agregar lineas. Usa `POST /api/tickets/{numTicket}/lineas` primero |
| `404 Not Found` al agregar una linea a un ticket | El `idProducto` enviado no existe o esta dado de baja | Verifica el UUID del producto con `GET /api/productos` |
| El listado devuelve `[]` aunque hay datos | Los registros estan dados de baja logicamente | Los endpoints de listado solo devuelven activos. Un `DELETE` no borra el registro, lo desactiva |

---

## 7. Glosario

> **[APRENDE]** El glosario define los terminos del dominio que el usuario necesita entender para usar la API correctamente. No es un diccionario tecnico de HTTP: es el vocabulario especifico de este negocio. Un termino mal entendido provoca bugs de integracion imposibles de rastrear. Define siempre los conceptos que no son evidentes o que tienen un significado especifico en este sistema.

| Termino | Definicion |
|---------|-----------|
| **Camarero** | Empleado del establecimiento que atiende y registra las ventas. Es el responsable de cada ticket. Se identifica por un `id` numerico y un `nif` unico. |
| **Cliente** | Persona o entidad que consume en el establecimiento y puede estar asociada a tickets. Puede ser un **Particular** o una **Empresa**. El `idCliente` es un UUID generado automaticamente. |
| **Particular** | Tipo de cliente que representa a una persona fisica. No tiene campos adicionales mas alla de los datos de contacto. |
| **Empresa** | Tipo de cliente que representa a una persona juridica. Tiene un campo `descuento` (porcentaje) que puede aplicarse en la facturacion externa. |
| **Producto** | Articulo del catalogo que puede consumirse y registrarse en un ticket. Tiene `descripcion`, `precio` y `stock`. El `idProducto` es un UUID. |
| **Ticket** | Documento de venta que agrupa una o varias consumiciones (lineas). Se identifica por un `numTicket` textual asignado por el cliente de la API. |
| **Cabecera de ticket** | Parte del ticket que contiene los datos generales: numero, fecha, camarero responsable, cliente asociado y total acumulado. |
| **Linea de ticket** | Cada registro de consumo dentro de un ticket. Contiene el producto, la cantidad, el precio unitario en el momento de la venta y el subtotal. El precio unitario se congela en el momento del registro. |
| **Baja logica** | Mecanismo por el que un recurso se marca como `activo: false` en lugar de eliminarse fisicamente. El recurso sigue existiendo en la base de datos pero no aparece en los listados ni puede asociarse a nuevos tickets. |
| **UUID** | Identificador unico universal en formato `xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx`. Lo genera el servidor automaticamente al crear clientes y productos. No debes inventarlo: la respuesta del POST te lo devuelve. |
| **Stock** | Cantidad disponible de un producto. La API registra el valor pero no lo decrementa automaticamente al crear lineas de ticket; la gestion del stock es responsabilidad del sistema que consuma esta API. |
| **Total** | Suma de los subtotales de todas las lineas de un ticket. Se recalcula automaticamente en el servidor cada vez que se agrega una linea nueva. |
