# Nimbus Framework — Example App

Aplicación de ejemplo, completa y ejecutable, para el [Nimbus Framework](https://github.com/neossoftware/nimbus-framework).
Cubre cada feature del framework con endpoints reales: MVC clásico con JSP, API
REST con JSON, inyección de dependencias (por campo, constructor, XML y
component-scan en múltiples paquetes), validación por anotaciones y custom,
interceptores, manejo de errores y scopes de bean.

El dominio "Curso" está expuesto por **dos caminos en paralelo**, para comparar
enfoques: uno 100% en memoria (sin base de datos, la app levanta sola en cualquier
contenedor Servlet 3.0+ sin configuración adicional) y otro respaldado por H2 real vía
`JdbcTemplate`/`NamedParameterJdbcTemplate` (ver sección "CRUD de Cursos (H2/JDBC)" más
abajo) — mismo modelo, misma validación, distinta implementación de servicio
seleccionada con `@Qualifier`.

## Requisitos

- Java 8+
- Maven 3.x
- Un contenedor de servlets (Tomcat 7-9, IBM WAS 8.5, o cualquier compatible con Servlet API 3.0+)
- [`nimbus-framework`](https://github.com/neossoftware/nimbus-framework) instalado en el repositorio local de Maven (`mvn install` en ese proyecto)

## Ejecutar

```bash
# 1. Instalar nimbus-framework en el repo local (una sola vez, o tras cambios al framework)
cd ../nimbus && mvn install

# 2. Empaquetar este ejemplo
cd ../nimbus-example && mvn package
```

Esto genera `target/nimbus-example-1.0.0.war`. Copialo al directorio `webapps/`
de tu Tomcat (o equivalente) y levantá el servidor:

```bash
cp target/nimbus-example-1.0.0.war $CATALINA_HOME/webapps/
$CATALINA_HOME/bin/startup.sh
```

La app queda disponible en `http://localhost:8080/nimbus-example-1.0.0/`
(la ruta de bienvenida `index.jsp` redirige a `/home.do`).

## Endpoints

### MVC clásico (`*.do`, vistas JSP)

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/home.do` | Página principal |
| POST | `/greeting.do` | Binding de formulario con `@ModelAttribute` |
| POST | `/greeting-prg.do` | Patrón Post/Redirect/Get |
| GET | `/resultado.do` | Lectura de `@RequestParam` |
| GET | `/usuario/{id}.do` | `@PathVariable` simple |
| GET | `/curso/{cursoId}/alumno/{alumnoId}.do` | `@PathVariable` múltiple |
| GET | `/about.do` | Inyección de `HttpServletRequest`/`HttpServletResponse`, beans XML |
| GET | `/api-explorer.do` | Página con listado de endpoints REST |
| GET | `/notificaciones/demo.do?mensaje=...` | DI con `@Qualifier` en un `@Controller` MVC |

### CRUD de Cursos (MVC + REST + validación)

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/cursos/lista.do` | Listado |
| GET | `/cursos/nuevo.do` | Formulario de alta |
| POST | `/cursos/guardar.do` | Alta, con validación custom (`CursoValidator`) |
| GET | `/cursos/editar/{id}.do` | Formulario de edición |
| POST | `/cursos/actualizar.do` | Edición |
| GET | `/cursos/eliminar/{id}.do` | Baja |
| GET | `/cursos/exportar.do?nivel=...` | Descarga CSV, respuesta directa al `response` |

### CRUD de Cursos (H2/JDBC)

Mismas rutas y comportamiento que el CRUD de Cursos en memoria de arriba, pero bajo
`/cursos-jdbc/*` y `/api/cursos-jdbc`, respaldado por H2 real vía `CursoJdbcDaoImpl`
(`JdbcTemplate` + `NamedParameterJdbcTemplate`) — ver "Qué demuestra cada pieza" abajo.

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/cursos-jdbc/lista.do` | Listado (SQL vía `JdbcTemplate`) |
| GET | `/cursos-jdbc/nuevo.do` | Formulario de alta |
| POST | `/cursos-jdbc/guardar.do` | Alta — `NamedParameterJdbcTemplate` + `GeneratedKeyHolder` para el id autogenerado |
| GET | `/cursos-jdbc/editar/{id}.do` | Formulario de edición |
| POST | `/cursos-jdbc/actualizar.do` | Edición — `NamedParameterJdbcTemplate` (UPDATE con parámetros nombrados) |
| GET | `/cursos-jdbc/eliminar/{id}.do` | Baja |
| GET | `/cursos-jdbc/exportar.do?nivel=...` | Descarga CSV |
| GET | `/api/cursos-jdbc` | Lista todos |
| GET | `/api/cursos-jdbc/{id}` | Busca por id (404 vía `GlobalExceptionHandler`) |
| POST | `/api/cursos-jdbc` | Crea, con `@Valid @RequestBody` |
| PUT | `/api/cursos-jdbc/{id}` | Actualiza |
| DELETE | `/api/cursos-jdbc/{id}` | Elimina |

### API REST (`/api/*`, requiere header `X-API-Key`)

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/cursos` | Lista todos |
| GET | `/api/cursos/{id}` | Busca por id (404 vía `@ExceptionHandler` local) |
| POST | `/api/cursos` | Crea, con `@Valid @RequestBody` |
| PUT | `/api/cursos/{id}` | Actualiza |
| DELETE | `/api/cursos/{id}` | Elimina |
| GET | `/api/notificaciones/demo?mensaje=...` | DI con `@Qualifier` en un `@RestController` |
| GET | `/api/scopes/info` | Beans `@Scope("prototype")` inyectados al arrancar |
| GET | `/api/scopes/new` | Bean prototype creado bajo demanda vía `ApplicationContext.getBean()` |

La clave por defecto (`auth.api-key` en `app.properties`) es `nimbus-secret-2024`:

```bash
curl -H "X-API-Key: nimbus-secret-2024" http://localhost:8080/nimbus-example-1.0.0/api/cursos
```

## Qué demuestra cada pieza

- **`framework-config.xml`** — configuración raíz: properties, view resolver,
  `component-scan` de `com.example`, `import` de `beans-config.xml`, interceptores.
- **`beans-config.xml`** (cargado vía `<import>`) — beans 100% XML sin anotaciones
  (`AppInfo`, `WelcomeBanner`), un `component-scan` de un paquete distinto
  (`com.otropaquete`) declarado *dentro* del import, y el `messageSource` +
  `cursoValidator` usados por la validación custom.
- **`AuditLogInterceptor` / `AuthInterceptor`** — interceptores registrados en
  orden; el segundo protege `/api/*` con una API key inyectada por `@Value`.
- **`CursoController` / `CursoRestController`** — el mismo dominio (Curso)
  expuesto primero como MVC con JSP y validación custom (`@InitBinder` +
  `Validator`), y luego como REST con validación por anotaciones (`@Valid`)
  y manejo de errores local con `@ExceptionHandler`.
- **`GlobalExceptionHandler`** (`@ControllerAdvice`) — captura lo que no
  manejó ningún controller: `ValidationException`, `NoSuchElementException`,
  `IllegalArgumentException` y `Exception` genérica, cada una con su status HTTP.
- **`NotificacionController` / `NotificacionMvcController`** — dos
  implementaciones de `NotificationService` (`Email`, `Sms`) desambiguadas con
  `@Qualifier`, tanto en un `@RestController` como en un `@Controller` MVC.
- **`ScopeController` / `RequestTracker`** — bean `@Scope("prototype")`:
  cada inyección y cada `getBean()` produce una instancia distinta.
- **`CursoServiceImpl`** — persistencia en memoria (`ConcurrentHashMap`),
  incluida paginación con `Page`/`Pageable`/`PageRequest`/`Sort`.
- **`CursoJdbcDaoImpl` / `CursoServiceJdbcImpl` / `CursoJdbcController` /
  `CursoJdbcRestController`** — camino paralelo respaldado por H2 real. El DAO reparte
  deliberadamente entre `JdbcTemplate` (SQL simple, 0-1 parámetro posicional:
  `listarTodos`/`buscarPorId`/`eliminar`/`contar`) y `NamedParameterJdbcTemplate`
  (INSERT/UPDATE con varios campos con nombre y el paginado con `LIMIT`/`OFFSET`
  nombrados), más `GeneratedKeyHolder` para recuperar el id autogenerado en cada
  alta. `CursoServiceJdbcImpl` implementa la misma interfaz `CursoService` que la
  versión en memoria; los controllers "-jdbc" la seleccionan con
  `@Qualifier("cursoServiceJdbcImpl")` — igual patrón que `Email`/`SmsNotificationService`.
- **`dataSource` / `jdbcTemplate` / `namedParameterJdbcTemplate`** (beans XML en
  `beans-config.xml`) — `DriverManagerDataSource` sobre H2 en memoria
  (`db.driver`/`db.url`/`db.user`/`db.password` en `app.properties`); el propio XML
  documenta en un comentario cómo reemplazar ese bean por
  `JndiObjectFactoryBean` para usar el pool de conexiones del contenedor en
  WAS 8.5/Tomcat productivo, sin tocar nada más.
- **`schema.sql`** — crea la tabla `cursos` y siembra los mismos 4 cursos que
  `CursoServiceImpl`, ejecutado por H2 en cada conexión vía
  `INIT=RUNSCRIPT FROM 'classpath:schema.sql'` (idempotente: `CREATE TABLE IF NOT
  EXISTS` + `MERGE` + `ALTER TABLE ... RESTART WITH` recalculado sobre el `MAX(id)`
  real, necesario porque `DriverManagerDataSource` no usa pool — cada operación abre
  una conexión nueva).

## Estructura

```
src/main/java/com/example
├── component      RequestTracker (@Scope prototype)
├── config         AppInfo, WelcomeBanner — beans 100% XML
├── controller      HomeController, CursoController, CursoRestController,
│                   CursoJdbcController, CursoJdbcRestController,
│                   NotificacionController, NotificacionMvcController, ScopeController
├── dao            CursoDao (+ impl/CursoJdbcDaoImpl — JdbcTemplate/NamedParameterJdbcTemplate)
├── exception       ApiError, GlobalExceptionHandler (@ControllerAdvice)
├── interceptor     AuditLogInterceptor, AuthInterceptor
├── model           Curso, Greeting
├── service         CursoService, NotificationService, UserService
│                   (+ impl/: CursoServiceImpl en memoria, CursoServiceJdbcImpl vía CursoDao)
└── validation      CursoValidator

src/main/java/com/otropaquete
└── OtroPaqueteService   detectado por un component-scan declarado en beans-config.xml

src/main/resources
├── app.properties
├── schema.sql       tabla + seed de "cursos" para H2 (INIT=RUNSCRIPT, ver app.properties)
└── application
    ├── config/beans-config.xml   (dataSource/jdbcTemplate/namedParameterJdbcTemplate acá)
    └── locallization/trust_resource.properties

src/main/webapp
├── index.jsp
└── WEB-INF
    ├── web.xml
    ├── framework-config.xml
    └── views/   (JSPs de cada pantalla; cursos/ y cursos-jdbc/ son pares 1:1)

loadtest/   plan de JMeter + resultados de referencia (ver loadtest/README.md)
```

## Pruebas de carga

Plan de JMeter (`loadtest/nimbus-loadtest.jmx`) que ejercita ambos caminos de Curso
(memoria y H2/JDBC) más el resto de endpoints, con 50 usuarios virtuales concurrentes
sostenidos — muy por encima de la concurrencia real esperada en PRD. Última corrida de
referencia: 8407 requests, 0% de error, throughput ~70 req/s. Ver
[`loadtest/README.md`](loadtest/README.md) para el detalle completo, cómo correrlo y el
desglose por endpoint.

## Licencia

GNU General Public License v3.0 (GPL-3.0). Ver el proyecto [`nimbus`](https://github.com/neossoftware/nimbus-framework).

## Autor

neossoftware
