# Guía de Desarrollador — API Cafetería

Esta guía está pensada para alguien que quiere construir esta aplicación desde cero. No es un tutorial genérico de Spring Boot: usa el código real del proyecto como referencia en cada paso. Si ya tienes la aplicación corriendo y quieres entender por qué está construida como está, este es tu documento.

Para saber qué hace cada endpoint, consulta la **Guía de API**. Para levantar el entorno en dos minutos, consulta el **README**.

---

## 0. Antes de empezar — conceptos que necesitas tener claros

> **[APRENDE]** No saltes a la sección 1 si no tienes claros estos conceptos. Construir sin entender los fundamentos es lo que produce código que nadie puede mantener.

### Qué es Spring Boot y por qué simplifica el desarrollo

Spring Boot es un framework que elimina la configuración boilerplate de Spring tradicional. En lugar de escribir cientos de líneas de XML o configuración Java, Spring Boot aplica convenciones inteligentes: si tienes MySQL en el classpath y configuras el datasource, automáticamente configura el pool de conexiones, el EntityManager de JPA y todo lo demás.

La clave del modelo mental: Spring Boot es un contenedor de objetos (el contexto de aplicación) que sabe cómo ensamblar y conectar tus clases entre sí. Tú declaras qué quieres, él decide cómo construirlo.

Referencias conceptuales: [Spring Boot Reference](https://docs.spring.io/spring-boot/docs/current/reference/html/) | [Inversion of Control](https://martinfowler.com/articles/injection.html)

### Qué es JPA / Hibernate y su relación con JDBC

JDBC es la API de bajo nivel de Java para hablar con bases de datos: escribes SQL a mano, gestionas conexiones, mapeas ResultSet a objetos. Funciona, pero es verboso y propenso a errores.

JPA (Jakarta Persistence API) es una ESPECIFICACIÓN que define cómo mapear objetos Java a tablas relacionales. Hibernate es la IMPLEMENTACIÓN más popular de esa especificación. Spring Data JPA pone una capa más encima que elimina incluso las implementaciones de repositorios estándar.

La cadena es: `tu código → JPA (especificación) → Hibernate (implementación) → JDBC → MySQL`.

Referencias: [JPA Guide](https://www.baeldung.com/the-persistence-layer-with-spring-and-jpa) | [Hibernate Docs](https://hibernate.org/orm/documentation/)

### Qué es el patrón MVC en el contexto de una REST API

MVC (Model-View-Controller) en una aplicación web tradicional tiene una "Vista" que renderiza HTML. En una REST API no hay vistas: el JSON que devuelves ES la representación. El patrón se adapta así:

- **Model**: las entidades JPA — representan los datos y las reglas de negocio del dominio
- **Service**: la lógica de negocio — orquesta operaciones, aplica reglas, gestiona transacciones
- **Controller**: el punto de entrada HTTP — recibe requests, delega al servicio, devuelve responses

La dependencia siempre va en una sola dirección: `Controller → Service → Repository → Model`. Nunca al revés.

### Qué es Docker y por qué lo usamos para la base de datos

Docker permite ejecutar aplicaciones en contenedores aislados que incluyen todo lo que necesitan para funcionar. Para desarrollo local, significa que no necesitas instalar MySQL en tu máquina: levantas un contenedor con `docker compose up` y tienes una instancia limpia, reproducible y descartable.

La ventaja clave: todos los desarrolladores del equipo trabajan contra exactamente la misma versión de MySQL, con el mismo schema inicial, sin interferir entre ellos.

---

## 1. Configuración del entorno de desarrollo

> **[APRENDE]** Documentar el entorno no es burocracia. Es lo que evita perder medio día por una versión de JDK incorrecta o un Maven que no encuentra el wrapper.

### JDK 21

Este proyecto usa Java 21 (LTS). Verifica tu versión:

```bash
java -version
# Debe mostrar: openjdk version "21.x.x"
```

Si no lo tienes, instálalo desde [Adoptium](https://adoptium.net/) (distribución Eclipse Temurin, la más fiable para entornos de producción).

### Maven 3.9+

```bash
mvn -version
# Debe mostrar: Apache Maven 3.9.x
```

Descarga desde [maven.apache.org](https://maven.apache.org/download.cgi). Asegúrate de que `JAVA_HOME` apunta a tu JDK 21.

### Docker Desktop

Necesitas Docker corriendo para levantar MySQL. Descárgalo desde [docker.com](https://www.docker.com/products/docker-desktop). Verifica:

```bash
docker --version
docker compose version
```

### IDE recomendado: IntelliJ IDEA

IntelliJ IDEA Community Edition es suficiente para este proyecto. Ultimate añade soporte nativo para Spring, pero no es imprescindible.

**Plugins útiles:**
- **Spring Boot Assistant** — autocompletado en `application.properties`
- **Database Tools** (incluido en Ultimate) — explorador de base de datos integrado
- **Lombok** — si en el futuro se añade Lombok al proyecto
- **SonarLint** — análisis de calidad de código en tiempo real

---

## 2. Creación del proyecto desde cero

### 2.1 Generar el esqueleto con Spring Initializr

> **[APRENDE]** Spring Initializr no es magia: genera un `pom.xml` con las dependencias correctas y la estructura de directorios estándar de Maven. Entender qué genera te permite modificarlo cuando algo no encaja.

Ve a [start.spring.io](https://start.spring.io) y configura:

- **Project**: Maven
- **Language**: Java
- **Spring Boot**: 3.2.5
- **Group**: `com.cafeteria`
- **Artifact**: `cafeteria`
- **Java**: 21

Dependencias a seleccionar y POR QUÉ cada una:

| Dependencia | Por qué la necesitas |
|-------------|---------------------|
| **Spring Web** | Añade el servidor embebido Tomcat y el soporte para controllers REST (`@RestController`, `@GetMapping`...) |
| **Spring Data JPA** | Integra Hibernate con Spring, autoconfigura el EntityManager y habilita los repositorios mágicos |
| **MySQL Driver** | El driver JDBC que Hibernate necesita para hablar con MySQL. Scope `runtime` porque solo se necesita en ejecución, no en compilación |
| **Validation** | Activa Bean Validation (JSR-380) para usar `@NotNull`, `@Size`, `@Email` en tus entidades y DTOs |

El `pom.xml` resultante en este proyecto refleja exactamente estas decisiones:

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.2.5</version>  <!-- el parent gestiona versiones de todas las dependencias -->
</parent>

<properties>
    <java.version>21</java.version>
</properties>

<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
        <!-- incluye Hibernate, Spring Data JPA, JDBC -->
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
        <!-- incluye Tomcat embebido, Jackson para JSON, Spring MVC -->
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
        <!-- Bean Validation con Hibernate Validator -->
    </dependency>
    <dependency>
        <groupId>com.mysql</groupId>
        <artifactId>mysql-connector-j</artifactId>
        <scope>runtime</scope>  <!-- solo en ejecución, no en compilación -->
    </dependency>
</dependencies>
```

### 2.2 Estructura de paquetes — la decisión más importante antes de escribir código

> **[APRENDE]** La estructura de paquetes define tu arquitectura mental. Si empiezas sin pensar en esto, acabas con código imposible de testear y de mantener. DECIDE ANTES DE CODIFICAR.

La estructura elegida en este proyecto es la clásica por capa técnica:

```
com.cafeteria/
├── CafeteriaApplication.java     ← punto de entrada
├── model/                        ← entidades JPA (el dominio)
│   ├── Cliente.java
│   ├── Empresa.java
│   ├── Particular.java
│   ├── Camarero.java
│   ├── Producto.java
│   ├── CabeceraTicket.java
│   └── LineaTicket.java
├── repository/                   ← acceso a datos (interfaces)
│   ├── CabeceraTicketRepository.java
│   ├── CamareroRepository.java
│   └── ...
├── service/                      ← lógica de negocio
│   ├── TicketService.java
│   ├── CamareroService.java
│   └── ...
└── controller/                   ← API REST (punto de entrada HTTP)
    ├── TicketController.java
    ├── CamareroController.java
    └── ...
```

La regla de dependencia es UNIDIRECCIONAL:

```
Controller  →  Service  →  Repository  →  Model
```

Un Controller NUNCA llama a un Repository directamente. Un Service NUNCA llama a otro Controller. Violar esto es el primer paso hacia el código espagueti.

### 2.3 Configurar application.properties

> **[APRENDE]** Externalizar la configuración significa que el comportamiento de la aplicación cambia según el entorno SIN tocar el código. Nunca hardcodees credenciales en el código fuente.

El `application.properties` real del proyecto, con cada línea explicada:

```properties
# URL de conexión a la base de datos
# useSSL=false → sin SSL en desarrollo (en producción debes activarlo)
# allowPublicKeyRetrieval=true → necesario para versiones recientes de MySQL con ciertas auth policies
# serverTimezone=UTC → evita problemas de zonas horarias en fechas
spring.datasource.url=jdbc:mysql://localhost:3306/cafeteria?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC

# Credenciales del usuario de base de datos (las del docker-compose.yml)
spring.datasource.username=user
spring.datasource.password=0000

# Driver JDBC explícito (Spring Boot lo detecta automáticamente, pero es buena práctica declararlo)
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# ddl-auto: qué hace Hibernate con el schema al arrancar
# 'update' → ajusta el schema existente sin borrar datos. NUNCA uses 'create' en producción.
# 'validate' → solo valida que el schema coincide con las entidades (recomendado en producción)
# 'none' → no toca el schema (cuando lo gestionas con Flyway o Liquibase)
spring.jpa.hibernate.ddl-auto=update

# Muestra el SQL generado por Hibernate en los logs
spring.jpa.show-sql=true

# Formatea el SQL en múltiples líneas para que sea legible
spring.jpa.properties.hibernate.format_sql=true

# Dialecto MySQL 8 — le dice a Hibernate qué funciones y sintaxis SQL puede usar
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect

# Puerto del servidor (8080 es el puerto de Adminer también — cambia uno de los dos)
server.port=8080
```

**Ojo importante**: en este proyecto tanto la app como Adminer usan el puerto 8080. O cambias `server.port` de la app (por ejemplo a 9090) o cambias el puerto de Adminer en el `docker-compose.yml`. Decide esto antes de arrancar.

---

## 3. La base de datos — Docker + MySQL

### 3.1 Levantar MySQL con Docker Compose

> **[APRENDE]** Docker Compose no es solo "levantar contenedores". Es declarar de forma versionable y reproducible qué infraestructura necesita tu aplicación para funcionar. El `docker-compose.yml` es parte del código del proyecto.

El `docker-compose.yml` real del proyecto, línea a línea:

```yaml
version: "3.9"

services:
  db:
    image: mysql:8.0          # versión concreta — nunca uses 'latest' en proyectos serios
    container_name: db        # nombre fijo para poder referenciarlo desde otros servicios/herramientas
    restart: always           # si el contenedor muere, Docker lo levanta automáticamente
    environment:
      MYSQL_ROOT_PASSWORD: root   # password del usuario root (solo para admin local)
      MYSQL_DATABASE: cafeteria   # crea esta base de datos al arrancar
      MYSQL_USER: user            # usuario de aplicación (el que usa Spring Boot)
      MYSQL_PASSWORD: 0000        # su contraseña — debe coincidir con application.properties
    ports:
      - "3306:3306"           # expone el puerto MySQL al host local
    volumes:
      - ./scripts/init.sql:/docker-entrypoint-initdb.d/init.sql  # ejecuta este SQL al crear la BD
      - mysql_data:/var/lib/mysql   # persiste los datos entre reinicios del contenedor

  adminer:
    image: adminer            # interfaz web para explorar la base de datos
    container_name: cafeteria_adminer
    restart: always
    ports:
      - "8080:8080"           # accesible en http://localhost:8080
    depends_on:
      - db                    # espera a que el servicio db esté corriendo

volumes:
  mysql_data:                 # volumen nombrado — Docker gestiona su ubicación
```

Comandos esenciales:

```bash
# Levantar todo en segundo plano
docker compose up -d

# Ver logs del contenedor de base de datos
docker compose logs -f db

# Parar los contenedores (sin borrar datos)
docker compose stop

# Parar Y borrar contenedores, redes y volúmenes (reset completo)
docker compose down -v
```

### 3.2 El esquema SQL — diseñar antes de codificar

> **[APRENDE]** La eterna pregunta: ¿database-first o code-first? En proyectos empresariales serios, el schema es la fuente de verdad. Los datos sobreviven a las aplicaciones. Diseña tu schema con cuidado y luego mapéalo a entidades JPA, no al revés.

El schema de la cafetería toma varias decisiones deliberadas que merece la pena entender:

**Por qué CHAR(36) para `id_cliente` e `id_producto` (UUID) en lugar de INT AUTO_INCREMENT**

Un UUID es un identificador universalmente único generado sin necesidad de consultar la base de datos. Las ventajas:
- Puedes generar el ID en la aplicación antes de persistir (sin roundtrip a la BD)
- Los registros son distribuibles y mezclables entre bases de datos sin colisiones
- No revela información sobre el volumen de negocio (nadie sabe cuántos clientes tienes mirando el ID)

El precio: más espacio (36 bytes vs 4 bytes) y joins ligeramente más lentos. Para una cafetería con miles de registros, irrelevante.

**Por qué la herencia se implementa como tablas separadas (JOINED)**

En el schema hay tres tablas para clientes: `cliente`, `empresa` y `particular`. Esto corresponde a la estrategia JOINED de JPA. Las alternativas eran:

- `SINGLE_TABLE`: todo en una tabla con una columna discriminador. Simple pero desperdicia columnas nulas y complica las constraints.
- `TABLE_PER_CLASS`: cada subclase tiene su tabla completa. Duplica columnas y dificulta las queries polimórficas.
- `JOINED` (elegida): la tabla base tiene los campos comunes, cada subclase tiene su propia tabla con solo sus campos adicionales. Limpio, normalizado, con integridad referencial real.

**Por qué `precio_unitario` en `linea_ticket` en lugar de una FK al precio del producto**

Porque los precios cambian. Si una línea de ticket apuntase directamente al precio del producto, una subida de precio modificaría retroactivamente todos los tickets históricos. Al guardar el precio en el momento de la compra, el ticket es inmutable e históricamente correcto. Esto se llama "snapshot de precio".

**Por qué las FKs de `cabecera_ticket` son `ON DELETE SET NULL`**

Si se elimina un cliente o un camarero, los tickets históricos no deben desaparecer. La FK se pone a NULL (el ticket existió, pero ya no podemos asociarlo a quién lo hizo). Es una decisión de negocio: los registros contables se preservan.

---

## 4. El Modelo — entidades JPA

### 4.1 Clase base abstracta: Cliente

> **[APRENDE]** JPA ofrece TRES estrategias de herencia. La elección afecta directamente al schema SQL y al rendimiento de las queries. No es un detalle menor.

Las tres estrategias de herencia en JPA:

| Estrategia | Schema | Queries polimórficas | Recomendada cuando |
|------------|--------|---------------------|--------------------|
| `SINGLE_TABLE` | 1 tabla con discriminador | Rápidas (sin JOIN) | Jerarquías simples, pocas columnas extra |
| `JOINED` | 1 tabla por clase | JOIN por nivel | Jerarquías normalizadas, muchos campos distintos |
| `TABLE_PER_CLASS` | 1 tabla completa por subclase | UNION ALL | Pocas queries polimórficas |

Este proyecto usa `JOINED`. El código real de `Cliente.java`:

```java
@Entity                              // esta clase es una entidad JPA — Hibernate la mapea a una tabla
@Table(name = "cliente")             // nombre de la tabla en BD (por convención, snake_case)
@Inheritance(strategy = InheritanceType.JOINED)  // estrategia de herencia JOINED
public abstract class Cliente {      // ABSTRACT — nunca instanciarás un Cliente directamente

    @Id
    @Column(name = "id_cliente", length = 36)
    private String idCliente;        // String porque es UUID — no @GeneratedValue porque lo generamos nosotros

    @Column(name = "numero_cliente", unique = true)
    private int numeroCliente;       // unique = true → constraint UNIQUE en la BD

    @Column(nullable = false, length = 100)
    private String nombre;           // nullable = false → columna NOT NULL

    @Column(name = "fecha_alta")
    private LocalDateTime fechaAlta; // LocalDateTime → DATETIME en MySQL

    @Column(nullable = false)
    private boolean activo;          // baja lógica — nunca borramos registros de clientes

    protected Cliente() {            // protected — solo accesible desde subclases
        this.idCliente = UUID.randomUUID().toString(); // UUID generado en la app, no en la BD
        this.fechaAlta = LocalDateTime.now();          // fecha de alta automática
        this.activo = true;                            // activo por defecto
    }
    // getters y setters...
}
```

**Por qué el UUID se genera en el constructor** y no con `@GeneratedValue(strategy = UUID)`: tener control explícito del ID permite conocerlo ANTES de persistir la entidad, lo que facilita la creación de objetos relacionados en memoria antes de hacer el flush a la BD.

### 4.2 Subclases: Empresa y Particular

> **[APRENDE]** `@PrimaryKeyJoinColumn` le dice a JPA cómo JOINear la tabla de la subclase con la tabla base. La PK de la subclase ES TAMBIÉN una FK a la tabla base.

```java
@Entity
@Table(name = "empresa")
@PrimaryKeyJoinColumn(name = "id_cliente")  // la PK de empresa se llama id_cliente y referencia a cliente.id_cliente
public class Empresa extends Cliente {

    @Column(nullable = false, precision = 5, scale = 2)
    private double descuento;   // campo propio de Empresa — va en la tabla empresa, no en cliente

    public Empresa() { super(); }  // llama al constructor de Cliente (genera UUID, fecha_alta, activo=true)

    // getters y setters...
}
```

`Particular` no añade ningún campo extra — solo existe para distinguir el tipo de cliente en el sistema de herencia:

```java
@Entity
@Table(name = "particular")
@PrimaryKeyJoinColumn(name = "id_cliente")
public class Particular extends Cliente {
    public Particular() { super(); }
}
```

Cuando Hibernate hace `findById` de un Cliente, emite automáticamente un `LEFT OUTER JOIN` entre `cliente` y `empresa`/`particular` para reconstruir el objeto correcto.

### 4.3 Relaciones entre entidades

> **[APRENDE]** Las relaciones JPA son el sitio donde más errores ocurren. `@OneToMany`, `@ManyToOne`, `fetchType`, `cascade`, `orphanRemoval` — cada opción tiene consecuencias. Vamos a verlas con el código real.

**CabeceraTicket → LineaTicket: @OneToMany con cascade y orphanRemoval**

```java
@OneToMany(
    mappedBy = "cabeceraTicket",   // el campo en LineaTicket que tiene la FK (lado "Many")
    cascade = CascadeType.ALL,     // cualquier operación sobre el ticket se propaga a sus líneas
    orphanRemoval = true           // si quitas una línea de la lista, Hibernate la borra de la BD
)
private List<LineaTicket> lineas = new ArrayList<>();
```

- `mappedBy` indica que LineaTicket es el lado PROPIETARIO de la relación (tiene la FK en la BD)
- `cascade = ALL` significa que al salvar el ticket, se salvan las líneas; al borrar el ticket, se borran las líneas
- `orphanRemoval = true` es más específico: si haces `ticket.getLineas().remove(linea)`, esa línea se borra de la BD automáticamente

**LineaTicket → CabeceraTicket: @ManyToOne**

```java
@ManyToOne(fetch = FetchType.LAZY)        // LAZY: no carga el ticket hasta que lo necesites
@JoinColumn(name = "num_ticket", nullable = false)  // columna FK en la tabla linea_ticket
private CabeceraTicket cabeceraTicket;
```

`FetchType.LAZY` es la opción correcta por defecto. `EAGER` carga el objeto relacionado inmediatamente, lo que puede generar queries masivas e innecesarias. Usa EAGER solo cuando tienes un caso de uso específico que lo justifique.

**CabeceraTicket → Cliente y Camarero: @ManyToOne nullable**

```java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "id_cliente")   // sin nullable = false → la FK puede ser NULL
private Cliente cliente;

@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "id_camarero")
private Camarero camarero;
```

Ambas FKs son nullable porque el schema define `ON DELETE SET NULL`. Un ticket puede existir sin cliente (venta anónima) o sin camarero asociado.

### 4.4 Lógica de negocio en el modelo

> **[APRENDE]** Hay dos filosofías: modelo anémico (entidades solo con getters/setters, lógica en servicios) y modelo rico (entidades con comportamiento). No hay una respuesta universal, pero poner lógica ÍNTIMAMENTE relacionada con el estado del objeto EN el propio objeto es más orientado a objetos y más testeable.

`CabeceraTicket` tiene dos métodos que son lógica de negocio pura:

```java
public void agregarLinea(LineaTicket linea) {
    linea.setCabeceraTicket(this);  // establece la relación bidireccional — crítico para Hibernate
    lineas.add(linea);
    recalcularTotal();              // mantiene el total consistente en memoria
}

public void recalcularTotal() {
    this.total = lineas.stream()
        .mapToDouble(LineaTicket::getSubtotal)
        .sum();
}
```

¿Por qué van en el modelo y no en el servicio? Porque `agregarLinea()` manipula estado interno del ticket (su lista de líneas y su total). Si esto estuviese en el servicio, tendrías lógica repartida y el objeto ticket podría quedarse en estado inconsistente (líneas añadidas sin total actualizado).

Nota crítica: `linea.setCabeceraTicket(this)` es OBLIGATORIO. En relaciones bidireccionales, Hibernate usa el lado propietario (el que tiene la FK) para generar el SQL. Si no establecemos `cabeceraTicket` en la línea, Hibernate no sabrá a qué ticket pertenece y dejará la FK a NULL.

---

## 5. Los Repositorios — Spring Data JPA

### 5.1 JpaRepository — qué te da gratis

> **[APRENDE]** Spring Data JPA genera la implementación de los repositorios EN TIEMPO DE COMPILACIÓN. No hay código oculto en runtime — puedes ver qué SQL genera con `show-sql=true`. La magia tiene explicación.

Con solo declarar una interfaz que extiende `JpaRepository<T, ID>`, obtienes automáticamente:

```java
// T = tipo de entidad, ID = tipo de la PK
public interface CamareroRepository extends JpaRepository<Camarero, Integer> {
    // Aquí ya tienes disponibles, sin escribir nada:
    // save(entity)         → INSERT o UPDATE según si tiene ID
    // findById(id)         → SELECT por PK, devuelve Optional<T>
    // findAll()            → SELECT * FROM tabla
    // findAll(Pageable)    → SELECT con paginación y ordenación
    // deleteById(id)       → DELETE por PK
    // existsById(id)       → SELECT COUNT > 0
    // count()              → SELECT COUNT(*)
    // saveAll(entities)    → INSERT/UPDATE en lote
}
```

### 5.2 Derived queries — consultas sin SQL

> **[APRENDE]** Spring Data JPA parsea el NOMBRE del método para generar el SQL. Es una DSL basada en convenciones de nomenclatura. Una vez la entiendes, es increíblemente poderosa y expresiva.

Las reglas básicas del naming:

- `findBy[Campo]` → `WHERE campo = ?`
- `findBy[Campo]True` → `WHERE campo = true`
- `findBy[CampoRelacion][CampoPK]` → JOIN + WHERE
- `findBy[Campo]Between` → `WHERE campo BETWEEN ? AND ?`
- `Optional<T>` como retorno → indica que puede no existir
- `List<T>` como retorno → siempre devuelve lista (vacía si no hay resultados)

Ejemplos reales del proyecto:

```java
// CamareroRepository
List<Camarero> findByActivoTrue();
// SQL: SELECT * FROM camarero WHERE activo = true

Optional<Camarero> findByNif(String nif);
// SQL: SELECT * FROM camarero WHERE nif = ?

// CabeceraTicketRepository
List<CabeceraTicket> findByClienteIdCliente(String idCliente);
// SQL: SELECT ct.* FROM cabecera_ticket ct WHERE ct.id_cliente = ?
// (Hibernate navega la relación: cliente → idCliente)

List<CabeceraTicket> findByCamareroIdCamarero(int idCamarero);
// SQL: SELECT ct.* FROM cabecera_ticket ct WHERE ct.id_camarero = ?

List<CabeceraTicket> findByFechaBetween(LocalDateTime desde, LocalDateTime hasta);
// SQL: SELECT * FROM cabecera_ticket WHERE fecha BETWEEN ? AND ?
```

### 5.3 Cuándo salir de los derived queries

> **[APRENDE]** Los derived queries tienen límites. Cuando la query se vuelve compleja (múltiples JOINs, funciones de agregación, subqueries) o simplemente el nombre del método sería ilegible, usa `@Query`.

```java
// Ejemplo de cuando la magia ya no es suficiente
@Query("SELECT ct FROM CabeceraTicket ct WHERE ct.total > :minimo ORDER BY ct.fecha DESC")
List<CabeceraTicket> findTicketsCaros(@Param("minimo") double minimo);

// O con SQL nativo cuando JPQL no es suficiente
@Query(value = "SELECT SUM(total) FROM cabecera_ticket WHERE MONTH(fecha) = :mes", nativeQuery = true)
Double totalMensual(@Param("mes") int mes);
```

Este proyecto aún no usa `@Query`, pero es el siguiente paso natural cuando las necesidades de consulta crezcan.

---

## 6. Los Servicios — lógica de negocio

### 6.1 Por qué una capa de servicio

> **[APRENDE]** El error más común en proyectos Spring de principiantes: poner lógica de negocio en el controller. El controller tiene UNA responsabilidad: traducir HTTP a llamadas de dominio y dominio a HTTP. Nada más.

Si pones lógica en el controller:
- No puedes reutilizarla sin duplicarla (si necesitas la misma lógica desde un job o un evento)
- No puedes testearla sin levantar el contexto HTTP completo
- Mezclas dos niveles de abstracción distintos

El servicio es el lugar donde vive la lógica de negocio, donde se coordinan operaciones sobre múltiples repositorios y donde se gestionan las transacciones.

### 6.2 Inyección de dependencias por constructor

> **[APRENDE]** Hay tres formas de inyectar dependencias en Spring: `@Autowired` en campo, `@Autowired` en setter, e inyección por constructor. La inyección por constructor es la ÚNICA forma correcta en la mayoría de casos. Las otras existen por razones históricas.

Por qué el constructor es superior:

1. **Inmutabilidad**: puedes declarar los campos como `final`, lo que garantiza que nunca cambian después de la construcción
2. **Testabilidad**: puedes instanciar la clase en un test unitario sin contexto Spring — solo pasas los mocks al constructor
3. **Detección de problemas**: las dependencias circulares se detectan en el arranque, no en runtime
4. **Claridad**: el constructor declara explícitamente de qué depende la clase

El patrón real en este proyecto:

```java
@Service
public class CamareroService {

    private final CamareroRepository repo;  // final — nunca cambiará tras la construcción

    // Spring detecta este constructor automáticamente (si hay solo uno, no necesita @Autowired)
    public CamareroService(CamareroRepository repo) {
        this.repo = repo;
    }

    // métodos de negocio...
}
```

Nota: con Spring Boot 3.x y un único constructor, `@Autowired` es opcional — Spring lo inyecta automáticamente.

### 6.3 @Transactional — cuándo y por qué

> **[APRENDE]** Una transacción garantiza que un conjunto de operaciones de base de datos se ejecutan como una unidad atómica: o todas tienen éxito, o ninguna se persiste. Sin transacciones, una excepción a mitad de operación puede dejar la BD en estado inconsistente.

El caso más claro del proyecto es `TicketService.agregarLinea()`:

```java
@Transactional
public CabeceraTicket agregarLinea(String numTicket, String idProducto, int cantidad) {
    // Operación 1: buscar el ticket
    CabeceraTicket ticket = ticketRepo.findById(numTicket)
            .orElseThrow(() -> new RuntimeException("Ticket no encontrado: " + numTicket));

    // Operación 2: buscar el producto
    Producto producto = productoRepo.findById(idProducto)
            .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + idProducto));

    // Operación 3: crear la línea y añadirla al ticket (actualiza el total)
    LineaTicket linea = new LineaTicket(producto, cantidad);
    ticket.agregarLinea(linea);

    // Operación 4: persistir el ticket con su nueva línea
    return ticketRepo.save(ticket);
}
```

Sin `@Transactional`, si la operación 4 falla (por ejemplo, violación de constraint), las operaciones anteriores ya habrían modificado el estado en memoria pero sin garantía de rollback consistente. Con `@Transactional`, si CUALQUIER operación lanza una excepción no chequeada (RuntimeException), Spring hace rollback de TODO lo que ocurrió en el método.

Reglas prácticas:
- Métodos que solo LEEN datos: `@Transactional(readOnly = true)` — optimiza el rendimiento
- Métodos que ESCRIBEN datos: `@Transactional` (sin readOnly)
- Métodos simples de un solo repositorio: pueden no necesitarlo (el repositorio ya tiene su transacción)

---

## 7. Los Controllers — la puerta de entrada

### 7.1 @RestController vs @Controller

> **[APRENDE]** `@Controller` es el anotación original de Spring MVC, pensada para aplicaciones que devuelven vistas (HTML, Thymeleaf, JSP). `@RestController` es `@Controller` + `@ResponseBody` en cada método — le dice a Spring que serialice el valor de retorno a JSON directamente.

```java
@Controller   // sin @ResponseBody → Spring busca una vista (template) con el nombre devuelto
public String metodo() {
    return "nombre-de-vista";  // busca templates/nombre-de-vista.html
}

@RestController  // equivale a @Controller + @ResponseBody en cada método
public Camarero metodo() {
    return camarero;  // Jackson serializa el objeto a JSON automáticamente
}
```

Para APIs REST, siempre `@RestController`.

### 7.2 Mapeo de rutas y métodos HTTP

> **[APRENDE]** REST no es solo "usar HTTP". Es un conjunto de convenciones sobre cómo usar los verbos HTTP, los códigos de estado y las URLs para representar operaciones sobre recursos. Apréndetelas.

Convenciones REST aplicadas en este proyecto:

| Verbo HTTP | Semántica | Ejemplo real |
|------------|-----------|--------------|
| `GET /recursos` | Listar todos | `GET /api/camareros` |
| `GET /recursos/{id}` | Obtener uno | `GET /api/camareros/5` |
| `POST /recursos` | Crear nuevo | `POST /api/camareros` |
| `PUT /recursos/{id}` | Reemplazar | `PUT /api/camareros/5` |
| `DELETE /recursos/{id}` | Eliminar/dar de baja | `DELETE /api/camareros/5` |

El controller completo de `CamareroController.java`, con cada anotación explicada:

```java
@RestController                      // este bean maneja requests HTTP y serializa respuestas a JSON
@RequestMapping("/api/camareros")    // prefijo base para TODAS las rutas de este controller
public class CamareroController {

    private final CamareroService service;

    public CamareroController(CamareroService service) {  // inyección por constructor
        this.service = service;
    }

    @GetMapping                      // GET /api/camareros → lista los activos
    public List<Camarero> listar() {
        return service.listarActivos();
        // retorno directo: Spring serializa la lista a JSON con código 200 OK
    }

    @GetMapping("/{id}")             // GET /api/camareros/{id} → obtiene uno por ID
    public ResponseEntity<Camarero> buscar(@PathVariable int id) {
        // @PathVariable extrae el valor {id} de la URL
        return service.buscarPorId(id)
                .map(ResponseEntity::ok)               // encontrado → 200 OK con el objeto
                .orElse(ResponseEntity.notFound().build()); // no encontrado → 404 Not Found
    }

    @PostMapping                     // POST /api/camareros → crea un nuevo camarero
    public Camarero crear(@RequestBody Camarero camarero) {
        // @RequestBody deserializa el JSON del body de la request al objeto Camarero
        return service.crear(camarero);
        // retorno directo → 200 OK con el camarero creado (incluye el ID generado)
    }

    @PutMapping("/{id}")             // PUT /api/camareros/{id} → actualiza un camarero
    public ResponseEntity<Camarero> actualizar(@PathVariable int id, @RequestBody Camarero camarero) {
        return service.buscarPorId(id)
                .map(existing -> {
                    camarero.setIdCamarero(id);        // asegura que el ID del body coincide con la URL
                    return ResponseEntity.ok(service.actualizar(camarero));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")          // DELETE /api/camareros/{id} → da de baja (baja lógica)
    public ResponseEntity<Void> darDeBaja(@PathVariable int id) {
        service.darDeBaja(id);
        return ResponseEntity.noContent().build();  // 204 No Content — éxito sin body
    }
}
```

### 7.3 ResponseEntity — control total sobre la respuesta HTTP

> **[APRENDE]** Los códigos de estado HTTP no son un detalle cosmético. Son el protocolo de comunicación entre el cliente y el servidor. Un 200 cuando debería ser un 404 es un bug, aunque el JSON "funcione".

Los patrones usados en el proyecto:

```java
// Patrón 1: recurso que puede no existir → 404 si no se encuentra
service.buscarPorId(id)
    .map(ResponseEntity::ok)               // Some(entity) → ResponseEntity.ok(entity) → 200
    .orElse(ResponseEntity.notFound().build()); // None → ResponseEntity.notFound() → 404

// Patrón 2: operación exitosa sin body de respuesta → 204
return ResponseEntity.noContent().build(); // DELETE, baja lógica

// Patrón 3: creación exitosa con el recurso creado → 200 (o idealmente 201)
return service.crear(camarero); // retorno directo → 200 OK
// Nota: la convención REST estricta usaría 201 Created con header Location
// Este proyecto usa 200 por simplicidad — ambas opciones son válidas
```

Códigos de estado que debes conocer:
- `200 OK` — éxito con body
- `201 Created` — recurso creado (idealmente con `Location` header al nuevo recurso)
- `204 No Content` — éxito sin body (DELETE, PUT sin respuesta)
- `400 Bad Request` — el cliente envió datos inválidos
- `404 Not Found` — el recurso no existe
- `409 Conflict` — conflicto (ej: NIF duplicado)
- `500 Internal Server Error` — error del servidor (nunca debería llegar al cliente en producción)

---

## 8. Añadir una nueva entidad — guía paso a paso

> **[APRENDE]** Esta sección es el corazón de la guía. Sigue estos pasos EN ORDEN cada vez que añadas algo nuevo al sistema. El orden importa: define el schema primero, luego la entidad, luego el repositorio, luego el servicio, luego el controller. De adentro hacia afuera.

Vamos a añadir la entidad `Mesa` al sistema: cada mesa del local tiene un número, una capacidad y un estado activo/inactivo.

### Paso 1: Añadir la tabla al schema SQL

Edita `scripts/init.sql` y añade:

```sql
-- TABLA MESA
CREATE TABLE mesa (
    id_mesa     INT AUTO_INCREMENT PRIMARY KEY,
    numero      INT NOT NULL UNIQUE,
    capacidad   INT NOT NULL,
    activo      BOOLEAN NOT NULL DEFAULT TRUE
);
```

Decisiones tomadas:
- `INT AUTO_INCREMENT` para el ID: las mesas son un recurso local sin necesidad de UUID
- `numero UNIQUE`: no pueden existir dos mesas con el mismo número
- `activo` para baja lógica: no borraremos mesas, las desactivaremos

Si el contenedor ya está corriendo, recréalo para que el init.sql se ejecute de nuevo:

```bash
docker compose down -v  # borra el volumen para forzar re-inicialización
docker compose up -d
```

Alternativamente, si no quieres perder datos, conéctate a la BD con Adminer (`http://localhost:8080`) y ejecuta el CREATE TABLE manualmente.

### Paso 2: Crear la entidad JPA — `model/Mesa.java`

```java
package com.cafeteria.model;

import jakarta.persistence.*;

@Entity
@Table(name = "mesa")
public class Mesa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // AUTO_INCREMENT en MySQL
    @Column(name = "id_mesa")
    private int idMesa;

    @Column(nullable = false, unique = true)
    private int numero;

    @Column(nullable = false)
    private int capacidad;

    @Column(nullable = false)
    private boolean activo;

    public Mesa() {
        this.activo = true; // activo por defecto al crear
    }

    public int getIdMesa() { return idMesa; }
    public void setIdMesa(int idMesa) { this.idMesa = idMesa; }
    public int getNumero() { return numero; }
    public void setNumero(int numero) { this.numero = numero; }
    public int getCapacidad() { return capacidad; }
    public void setCapacidad(int capacidad) { this.capacidad = capacidad; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    @Override
    public String toString() {
        return "Mesa[id=" + idMesa + ", numero=" + numero + ", capacidad=" + capacidad + "]";
    }
}
```

### Paso 3: Crear el repositorio — `repository/MesaRepository.java`

```java
package com.cafeteria.repository;

import com.cafeteria.model.Mesa;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface MesaRepository extends JpaRepository<Mesa, Integer> {
    List<Mesa> findByActivoTrue();                  // listar solo las mesas activas
    Optional<Mesa> findByNumero(int numero);        // buscar por número de mesa
}
```

No necesitas implementar nada. Spring Data JPA genera el código en tiempo de compilación.

### Paso 4: Crear el servicio — `service/MesaService.java`

```java
package com.cafeteria.service;

import com.cafeteria.model.Mesa;
import com.cafeteria.repository.MesaRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class MesaService {

    private final MesaRepository repo;

    public MesaService(MesaRepository repo) {
        this.repo = repo;
    }

    public Mesa crear(Mesa mesa) {
        return repo.save(mesa);
    }

    public Optional<Mesa> buscarPorId(int id) {
        return repo.findById(id);
    }

    public List<Mesa> listarActivas() {
        return repo.findByActivoTrue();
    }

    public List<Mesa> listarTodas() {
        return repo.findAll();
    }

    public Mesa actualizar(Mesa mesa) {
        return repo.save(mesa);
    }

    public void darDeBaja(int id) {
        repo.findById(id).ifPresent(m -> {
            m.setActivo(false);
            repo.save(m);
        });
    }
}
```

### Paso 5: Crear el controller — `controller/MesaController.java`

```java
package com.cafeteria.controller;

import com.cafeteria.model.Mesa;
import com.cafeteria.service.MesaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/mesas")
public class MesaController {

    private final MesaService service;

    public MesaController(MesaService service) {
        this.service = service;
    }

    @GetMapping
    public List<Mesa> listar() {
        return service.listarActivas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Mesa> buscar(@PathVariable int id) {
        return service.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mesa crear(@RequestBody Mesa mesa) {
        return service.crear(mesa);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Mesa> actualizar(@PathVariable int id, @RequestBody Mesa mesa) {
        return service.buscarPorId(id)
                .map(existing -> {
                    mesa.setIdMesa(id);
                    return ResponseEntity.ok(service.actualizar(mesa));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> darDeBaja(@PathVariable int id) {
        service.darDeBaja(id);
        return ResponseEntity.noContent().build();
    }
}
```

### Paso 6: Probar con curl

```bash
# Crear una mesa
curl -X POST http://localhost:9090/api/mesas \
  -H "Content-Type: application/json" \
  -d '{"numero": 1, "capacidad": 4}'

# Respuesta esperada:
# {"idMesa": 1, "numero": 1, "capacidad": 4, "activo": true}

# Listar mesas activas
curl http://localhost:9090/api/mesas

# Obtener una mesa por ID
curl http://localhost:9090/api/mesas/1

# Actualizar capacidad
curl -X PUT http://localhost:9090/api/mesas/1 \
  -H "Content-Type: application/json" \
  -d '{"numero": 1, "capacidad": 6}'

# Dar de baja (baja lógica — activo = false)
curl -X DELETE http://localhost:9090/api/mesas/1
# Respuesta: 204 No Content (sin body)

# Verificar que la mesa ya no aparece en la lista de activas
curl http://localhost:9090/api/mesas
```

---

## 9. Errores frecuentes en Spring Boot + JPA

> **[APRENDE]** Estos errores los va a encontrar TODO desarrollador que empiece con JPA. No es cuestión de si, sino de cuándo. Conócelos antes de que te bloqueen horas.

| Error | Causa | Solución |
|-------|-------|----------|
| `LazyInitializationException` | Acceder a una colección lazy fuera de la sesión JPA | Acceder dentro de `@Transactional`, usar `@EntityGraph`, o cambiar a EAGER (con criterio) |
| `could not initialize proxy - no Session` | Misma raíz que el anterior — el EntityManager ya se cerró | Misma solución — asegura que el acceso ocurre dentro de una transacción activa |
| `detached entity passed to persist` | Intentas persistir una entidad que JPA no gestiona (sin ID generado por BD o fuera de sesión) | Usa `save()` (que hace merge si tiene ID) en lugar de `persist()` directamente |
| `StackOverflowError` en serialización JSON | Relaciones bidireccionales — A serializa B que serializa A infinitamente | Añade `@JsonIgnore` en uno de los lados de la relación (normalmente el hijo apuntando al padre) |
| `Table 'cafeteria.X' doesn't exist` | `ddl-auto=validate` o `none` y la tabla no existe, o `ddl-auto=create` borró y recreó sin tu script | Verifica `ddl-auto` en `application.properties` — en desarrollo usa `update`, verifica que el schema SQL está aplicado |
| `Unique index or primary key violation` | Intentas insertar un registro con una clave duplicada | Verifica datos antes de insertar, captura la excepción con un handler específico |
| `No identifier specified for entity` | Falta `@Id` en la entidad | Añade `@Id` al campo que es la PK |
| `Unknown column 'X' in field list` | `ddl-auto=update` no añadió una columna nueva, o hay un mismatch entre entidad y schema | Revisa que el nombre del campo coincide con la columna SQL, o recrea el contenedor |

### LazyInitializationException en detalle

Este merece atención especial porque es el más común. Ocurre cuando haces algo así:

```java
// EN EL CONTROLLER (sin @Transactional):
CabeceraTicket ticket = service.buscarPorNumero("T001").get();
// En este punto la sesión JPA ya se cerró

ticket.getLineas().size(); // ← BOOM: LazyInitializationException
// Las líneas son LAZY y la sesión ya no existe para cargarlas
```

La solución correcta: acceder a las colecciones lazy DENTRO de la transacción (en el servicio):

```java
@Transactional(readOnly = true)
public CabeceraTicket buscarConLineas(String numTicket) {
    CabeceraTicket ticket = ticketRepo.findById(numTicket).orElseThrow(...);
    ticket.getLineas().size(); // fuerza la carga lazy DENTRO de la transacción
    return ticket;
}
```

O mejor aún, usa `@EntityGraph` en el repositorio para cargar las relaciones necesarias en una sola query:

```java
@EntityGraph(attributePaths = {"lineas"})
Optional<CabeceraTicket> findById(String id);
```

### StackOverflowError por ciclos JSON

Ocurre con relaciones bidireccionales. `CabeceraTicket` tiene `List<LineaTicket>` y `LineaTicket` tiene `CabeceraTicket` — Jackson entra en un bucle infinito al serializar.

La solución en este proyecto es añadir `@JsonIgnore` en `LineaTicket`:

```java
@JsonIgnore  // rompe el ciclo: al serializar LineaTicket, no incluye el ticket padre
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "num_ticket", nullable = false)
private CabeceraTicket cabeceraTicket;
```

Alternativamente, usa `@JsonManagedReference` / `@JsonBackReference` para un control más fino.

---

## 10. Checklist de buenas prácticas

> **[APRENDE]** Revisa esta lista antes de hacer cualquier PR. Cada punto tiene un motivo — si no sabes por qué está aquí, es que necesitas leerlo con más atención.

### Modelo / Entidades

- [ ] ¿Cada entidad tiene exactamente un `@Id`?
- [ ] ¿Los tipos Java coinciden con los tipos SQL? (`String` para `VARCHAR`/`CHAR`, `LocalDate` para `DATE`, `LocalDateTime` para `DATETIME`, `double` o `BigDecimal` para `DECIMAL`)
- [ ] ¿Las relaciones `@OneToMany` tienen `mappedBy` apuntando al campo correcto en la otra entidad?
- [ ] ¿Las relaciones bidireccionales establecen AMBOS lados en `agregarX()` o métodos similares?
- [ ] ¿Usas `FetchType.LAZY` por defecto en todas las relaciones?
- [ ] ¿Las colecciones están inicializadas en la declaración (`= new ArrayList<>()`)?
- [ ] ¿Las clases abstractas tienen constructor `protected` (no `public`)?

### Repositorios

- [ ] ¿El segundo parámetro de `JpaRepository<T, ID>` coincide con el tipo del `@Id`?
- [ ] ¿Los derived queries devuelven `Optional<T>` cuando puede no existir un resultado?
- [ ] ¿Los derived queries que filtran colecciones devuelven `List<T>`?

### Servicios

- [ ] ¿Las dependencias se inyectan por constructor (no `@Autowired` en campo)?
- [ ] ¿Los campos inyectados son `final`?
- [ ] ¿Los métodos que escriben en BD tienen `@Transactional`?
- [ ] ¿Los métodos que solo leen tienen `@Transactional(readOnly = true)`?
- [ ] ¿El servicio no importa nada de `org.springframework.web` (controllers) ni de Jackson?

### Controllers

- [ ] ¿Usas `@RestController`, no `@Controller`?
- [ ] ¿Las rutas siguen la convención `/api/{recurso-en-plural}`?
- [ ] ¿Las búsquedas por ID devuelven `ResponseEntity` con 404 si no existe?
- [ ] ¿Los DELETE devuelven `204 No Content` sin body?
- [ ] ¿El controller NO contiene lógica de negocio (solo traduce HTTP ↔ dominio)?

### Configuración

- [ ] ¿El `application.properties` no contiene credenciales reales de producción?
- [ ] ¿`ddl-auto` es `validate` o `none` en producción (nunca `create` ni `create-drop`)?
- [ ] ¿El `docker-compose.yml` tiene versiones concretas de imágenes (no `latest`)?

### SQL / Schema

- [ ] ¿Las FKs tienen la política `ON DELETE` correcta (CASCADE, SET NULL, RESTRICT)?
- [ ] ¿Los campos de precio usan `DECIMAL(x,y)` (no `FLOAT` ni `DOUBLE` — precisión financiera)?
- [ ] ¿Los IDs UUID son `CHAR(36)` (no `VARCHAR(255)` — desperdicio innecesario)?
- [ ] ¿El schema SQL está en `scripts/init.sql` y se aplica al levantar el contenedor?
