# Nimbus Framework — Example App

Aplicación de ejemplo, completa y ejecutable, para el [Nimbus Framework](../nimbus).
Cubre cada feature del framework con endpoints reales: MVC clásico con JSP, API
REST con JSON, inyección de dependencias (por campo, constructor, XML y
component-scan en múltiples paquetes), validación por anotaciones y custom,
interceptores, manejo de errores y scopes de bean.

No usa base de datos: los servicios guardan datos en memoria, así que la app
levanta sola en cualquier contenedor Servlet 3.0+ sin configuración adicional.

## Requisitos

- Java 8+
- Maven 3.x
- Un contenedor de servlets (Tomcat 7-9, IBM WAS 8.5, o cualquier compatible con Servlet API 3.0+)
- [`nimbus-framework`](../nimbus) instalado en el repositorio local de Maven (`mvn install` en ese proyecto)

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

## Estructura

```
src/main/java/com/example
├── component      RequestTracker (@Scope prototype)
├── config         AppInfo, WelcomeBanner — beans 100% XML
├── controller      HomeController, CursoController, CursoRestController,
│                   NotificacionController, NotificacionMvcController, ScopeController
├── exception       ApiError, GlobalExceptionHandler (@ControllerAdvice)
├── interceptor     AuditLogInterceptor, AuthInterceptor
├── model           Curso, Greeting
├── service         CursoService, NotificationService, UserService (+ impl/)
└── validation      CursoValidator

src/main/java/com/otropaquete
└── OtroPaqueteService   detectado por un component-scan declarado en beans-config.xml

src/main/resources
├── app.properties
└── application
    ├── config/beans-config.xml
    └── locallization/trust_resource.properties

src/main/webapp
├── index.jsp
└── WEB-INF
    ├── web.xml
    ├── framework-config.xml
    └── views/   (JSPs de cada pantalla)
```

## Licencia

GNU General Public License v3.0 (GPL-3.0). Ver el proyecto [`nimbus`](../nimbus/LICENSE).

## Autor

neossoftware
