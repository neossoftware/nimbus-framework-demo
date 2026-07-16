# Pruebas de carga (JMeter)

Plan de JMeter (`nimbus-loadtest.jmx`) que simula tráfico realista sobre `nimbus-example`
corriendo en Tomcat: páginas MVC resueltas por JSP y llamadas a la API REST, más un mix
liviano de escrituras.

## Diseño

Tres Thread Groups corren en simultáneo, 120s cada uno (más rampa de arranque):

| Thread Group | Hilos | Rampa | Qué hace |
|---|---|---|---|
| `MVC-JSP (paginas)` | 30 | 15s | GET a `/home.do`, `/cursos/lista.do`, `/usuario/{id}.do`, `/cursos/editar/{id}.do`, `/about.do`, `/api-explorer.do`, `/cursos-jdbc/lista.do`, `/cursos-jdbc/editar/{id}.do` — todas resuelven una vista JSP |
| `REST-API (lecturas)` | 15 | 10s | GET a `/api/cursos`, `/api/cursos/{id}`, `/api/scopes/info`, `/api/notificaciones/demo`, `/api/cursos-jdbc`, `/api/cursos-jdbc/{id}` (header `X-API-Key`) |
| `Writes-API (POST+DELETE)` | 5 | 5s | POST+DELETE autolimpiante en `/api/cursos` (en memoria) y en `/api/cursos-jdbc` (H2/JDBC) — no infla los datos de ninguno de los dos backends |

Las rutas `cursos-jdbc`/`api/cursos-jdbc` son el camino paralelo respaldado por H2 real vía
`JdbcTemplate`/`NamedParameterJdbcTemplate` (ver `com.example.dao.CursoJdbcDaoImpl` en el
código fuente) — mismo modelo y validación que la variante en memoria, para comparar
ambos caminos bajo la misma carga.

**50 usuarios virtuales concurrentes en total**, con *think time* aleatorio entre
requests (300–1200ms según el grupo) — muy por encima de la concurrencia real esperada
en PRD (~100 usuarios registrados, no todos concurrentes al mismo tiempo).

Cada sampler tiene un `Response Assertion` sobre el código HTTP esperado (200 para
lecturas, 201 para el POST, 204 para el DELETE), así cualquier error queda marcado como
fallo en el reporte en vez de pasar desapercibido.

## Cómo correrlo

```bash
# 1. Asegurate de que Tomcat esté arriba con nimbus-example desplegado (ver README del proyecto)
curl -s -o /dev/null -w "%{http_code}\n" http://localhost:8090/nimbus-example-1.0.0/home.do

# 2. Correr el plan (modo no-GUI, como se recomienda para pruebas de carga reales)
cd loadtest
rm -rf results.jtl report
~/software/apache-jmeter-5.6.3/bin/jmeter -n -t nimbus-loadtest.jmx -l results.jtl -j jmeter.log

# 3. Generar el dashboard HTML a partir de los resultados
~/software/apache-jmeter-5.6.3/bin/jmeter -g results.jtl -o report

# 4. Abrir report/index.html en el navegador
```

## Nota sobre el mix de escrituras

El grupo `Writes-API` crea un curso y lo borra en el mismo ciclo, en **ambos backends**
(`/api/cursos` en memoria y `/api/cursos-jdbc` en H2). Si la duración del thread group
corta a mitad de un ciclo (un hilo hizo el POST pero todavía no llegó al DELETE), pueden
quedar 1–5 cursos de prueba (nombre `"Curso JMeter ..."`) sin borrar en cualquiera de los
dos. Verificá y limpiá manualmente si hace falta:

```bash
for path in cursos cursos-jdbc; do
  curl -s -H "X-API-Key: nimbus-secret-2024" "http://localhost:8090/nimbus-example-1.0.0/api/$path" \
    | python3 -c "import sys,json; [print(c['id']) for c in json.load(sys.stdin) if 'JMeter' in c['nombre']]"
done
# borrar cada id con:
# curl -X DELETE -H "X-API-Key: nimbus-secret-2024" http://localhost:8090/nimbus-example-1.0.0/api/{cursos|cursos-jdbc}/{id}
```

## Resultado de referencia (2026-07-16, esta máquina)

### Contexto de la corrida

- **Objetivo**: demostrar que el framework sostiene carga muy por encima de la
  concurrencia real esperada en PRD (~100 usuarios registrados, de los cuales no todos
  están activos al mismo tiempo) — en los dos backends de Curso: en memoria y H2 real
  vía `JdbcTemplate`/`NamedParameterJdbcTemplate`.
- **Usuarios virtuales concurrentes**: 50 en total, repartidos en los 3 escenarios de la
  tabla de arriba (30 navegando páginas JSP, 15 consumiendo la API REST, 5 haciendo
  altas/bajas) — corriendo todos en simultáneo, no en secuencia.
- **Duración**: 120s de estado estable por grupo (más 5–15s de rampa de arranque),
  ~2 minutos de wall-clock total.
- **Pruebas ejecutadas**: 18 endpoints distintos — 8 vistas MVC/JSP (4 en memoria + 4
  H2/JDBC), 6 lecturas REST (4 en memoria + 2 H2/JDBC) y 2 pares POST/DELETE de escritura
  (uno por backend) — ver tabla de escenarios más arriba para el detalle de cada uno.
- **Entorno**: Tomcat 9.0.118 local, JDK 25, un solo nodo (sin balanceo de carga; H2 en
  memoria sin pool de conexiones — cada request abre su propia conexión JDBC, ver
  limitaciones más abajo).

### Resultado global

| Métrica | Valor |
|---|---|
| Requests totales | 8407 |
| Errores | **0 (0.00%)** |
| Tiempo de respuesta promedio | 1.63ms |
| p90 / p95 / p99 | 3ms / 4ms / 16ms |
| Máximo | 135ms |
| Throughput sostenido | ~70.4 req/s |

### Desglose por endpoint

| Endpoint | Backend | Requests | Error % | Avg (ms) | p90 (ms) | p95 (ms) | p99 (ms) | Max (ms) | Throughput (req/s) |
|---|---|---|---|---|---|---|---|---|---|
| GET /home.do | — | 569 | 0.0% | 1.34 | 2 | 3 | 16 | 42 | 4.77 |
| GET /cursos/lista.do | memoria | 566 | 0.0% | 1.43 | 2 | 3 | 16 | 52 | 4.79 |
| GET /usuario/{id}.do | — | 564 | 0.0% | 1.24 | 2 | 3 | 12 | 56 | 4.77 |
| GET /cursos/editar/{id}.do | memoria | 560 | 0.0% | 1.22 | 2 | 3 | 9 | 34 | 4.76 |
| GET /about.do | — | 558 | 0.0% | 1.25 | 2 | 3 | 12 | 62 | 4.80 |
| GET /api-explorer.do | — | 555 | 0.0% | 1.33 | 2 | 3 | 20 | 35 | 4.78 |
| GET /cursos-jdbc/lista.do | **H2/JDBC** | 553 | 0.0% | 2.91 | 4 | 5 | 39 | 80 | 4.78 |
| GET /cursos-jdbc/editar/{id}.do | **H2/JDBC** | 545 | 0.0% | 3.17 | 4 | 6 | 46 | 135 | 4.75 |
| GET /api/cursos | memoria | 577 | 0.0% | 0.84 | 1 | 2 | 6 | 32 | 4.84 |
| GET /api/cursos/{id} | memoria | 575 | 0.0% | 0.97 | 1 | 2 | 14 | 37 | 4.83 |
| GET /api/scopes/info | — | 572 | 0.0% | 0.93 | 2 | 2 | 12 | 40 | 4.84 |
| GET /api/notificaciones/demo | — | 570 | 0.0% | 1.06 | 1 | 2 | 11 | 53 | 4.84 |
| GET /api/cursos-jdbc | **H2/JDBC** | 569 | 0.0% | 2.77 | 3 | 5 | 47 | 78 | 4.84 |
| GET /api/cursos-jdbc/{id} | **H2/JDBC** | 565 | 0.0% | 2.15 | 3 | 5 | 17 | 60 | 4.83 |
| POST /api/cursos | memoria | 128 | 0.0% | 1.66 | 4 | 6 | 13 | 14 | 1.10 |
| DELETE /api/cursos/{cursoId} | memoria | 128 | 0.0% | 1.25 | 2 | 3 | 31 | 40 | 1.10 |
| POST /api/cursos-jdbc | **H2/JDBC** | 127 | 0.0% | 2.82 | 4 | 7 | 20 | 22 | 1.10 |
| DELETE /api/cursos-jdbc/{cursoJdbcId} | **H2/JDBC** | 126 | 0.0% | 2.37 | 3 | 3 | 55 | 69 | 1.10 |
| **Total** | | **8407** | **0.0%** | **1.63** | **3** | **4** | **16** | **135** | **70.40** |

Reporte completo (gráficos de tiempo de respuesta, throughput en el tiempo, distribución
de códigos, etc.) en `report/index.html` tras correr el plan; los números crudos en
`report/statistics.json`.

### Lectura de los resultados

- **0% de error** en las 8407 requests, en los dos backends: ni las vistas JSP ni la API
  REST devolvieron un código inesperado bajo 50 usuarios concurrentes sostenidos —
  incluyendo el camino nuevo respaldado por H2 real.
- **H2/JDBC es consistentemente un poco más lento que memoria pura** (2.1–3.2ms promedio
  vs 0.8–1.4ms), como es esperable: cada request hace un round-trip SQL real (parseo +
  ejecución + mapeo de filas) en vez de una simple lectura de `ConcurrentHashMap`. La
  diferencia es de milisegundos, no perceptible para un usuario, y confirma que
  `JdbcTemplate`/`NamedParameterJdbcTemplate` no introducen overhead significativo más
  allá del propio costo inherente de hablar con una base de datos.
- Los tiempos p90/p95 de 1–7ms (en ambos backends) muestran que la enorme mayoría de
  requests son prácticamente instantáneas; los picos de p99 (hasta ~55ms) y el máximo
  puntual de 135ms son esperables bajo concurrencia (GC, contención puntual de I/O) y no
  afectan la experiencia típica.
- Las vistas JSP no muestran latencia significativamente mayor que los endpoints REST
  equivalentes dentro del mismo backend — el `DispatcherServlet`/`JspViewResolver` del
  framework no es el cuello de botella en ninguno de los dos caminos.
- Ambos mix de escrituras (POST/DELETE, en memoria y H2/JDBC) sostuvieron su throughput
  sin errores — incluyendo el `GeneratedKeyHolder` de `NamedParameterJdbcTemplate` para
  recuperar el id autogenerado en cada INSERT concurrente, sin colisiones.

### Limitaciones de esta corrida (a tener en cuenta)

- El camino H2/JDBC usa `DriverManagerDataSource` (sin pool de conexiones — cada
  operación abre y cierra su propia conexión JDBC); un pool real (o el del contenedor vía
  `JndiObjectFactoryBean`, ver comentario en `beans-config.xml`) probablemente reduzca
  aún más la latencia del camino JDBC bajo carga sostenida.
- El camino en memoria (`ConcurrentHashMap`) sigue sin validar comportamiento con I/O de
  base de datos real bajo carga (locks, contención de pool, etc.) — para eso está el
  camino H2/JDBC en esta misma corrida.
- Cliente y servidor en la misma máquina (sin latencia de red real ni límites de un
  balanceador/proxy intermedio).
- Corrida en un único nodo Tomcat, sin clustering ni balanceo de carga.

Para un test de estrés más agresivo, subí `ThreadGroup.num_threads` en cada grupo del
`.jmx` (o dupli­cá el archivo con más hilos) y volvé a correr — el resto del procedimiento
es igual.
