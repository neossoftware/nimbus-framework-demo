<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Nimbus — Ejemplos</title>
    <style>
        * { box-sizing: border-box; }
        body  { font-family: Arial, sans-serif; max-width: 1000px; margin: 40px auto; padding: 0 20px; color: #222; }
        h1    { color: #2c5f8a; margin-bottom: 4px; }
        .sub  { color: #666; font-size: 14px; margin-bottom: 20px; }

        /* Leyenda de categorías */
        .leyenda { display: flex; gap: 10px; flex-wrap: wrap; margin-bottom: 24px; }
        .leyenda span { font-size: 11px; }

        table  { width: 100%; border-collapse: collapse; margin-bottom: 32px; }
        thead  { background: #2c5f8a; color: #fff; }
        thead th { padding: 10px 14px; text-align: left; font-size: 13px; }
        tbody tr:nth-child(even) { background: #f4f7fb; }
        tbody tr:hover           { background: #e8f0fb; }
        td    { padding: 10px 14px; font-size: 13px; vertical-align: top; border-bottom: 1px solid #e0e6ef; }
        td.verb  { font-family: monospace; font-weight: bold; white-space: nowrap; }
        td.verb.get    { color: #166534; }
        td.verb.post   { color: #854d0e; }
        td.verb.put    { color: #1e40af; }
        td.verb.delete { color: #991b1b; }
        td code  { background: #eef2ff; padding: 2px 6px; border-radius: 3px; font-size: 12px; }
        td.accion a { display: inline-block; color: #fff; background: #2c5f8a; padding: 5px 12px;
                      border-radius: 4px; text-decoration: none; font-size: 12px;
                      white-space: nowrap; margin: 2px 0; }
        td.accion a:hover { background: #1a3d5c; }
        td.accion .curl { display: block; background: #1e1e1e; color: #9ecbff; padding: 6px 8px;
                           border-radius: 4px; font-size: 11px; white-space: nowrap; overflow-x: auto; }

        /* Badge de categoría — agrupa por qué feature del framework demuestra la fila */
        .badge { display: inline-block; font-size: 10px; font-weight: bold;
                 padding: 2px 8px; border-radius: 3px; white-space: nowrap; }
        .cat-binding    { background: #cce5ff; color: #004085; }
        .cat-inyeccion  { background: #e2d9f3; color: #6f42c1; }
        .cat-validacion { background: #f8d7da; color: #721c24; }
        .cat-archivos   { background: #ffe8cc; color: #8a4b00; }
        .cat-rest       { background: #d4edda; color: #155724; }
        .cat-config     { background: #d1ecf1; color: #0c5460; }

        /* Formularios (únicos endpoints POST que no se pueden probar con un link) */
        .forms { display: flex; gap: 24px; flex-wrap: wrap; }
        .card  { flex: 1; min-width: 260px; background: #f4f7fb; border-radius: 8px;
                 padding: 20px; border: 1px solid #dde4f0; }
        .card h3 { margin-top: 0; color: #2c5f8a; font-size: 15px; }
        label   { display: block; margin-bottom: 5px; font-size: 13px; font-weight: bold; }
        input[type=text] { width: 100%; padding: 7px 9px; margin-bottom: 10px;
                           border: 1px solid #ccd; border-radius: 4px; font-size: 13px; }
        .btn     { padding: 8px 16px; border: none; border-radius: 4px; cursor: pointer;
                   color: #fff; font-size: 13px; }
        .btn-blue   { background: #2c5f8a; }
        .btn-yellow { background: #856404; }
        .note { font-size: 11px; color: #888; margin: 8px 0 0 0; }

        hr { border: none; border-top: 1px solid #dde; margin: 32px 0; }
    </style>
</head>
<body>

    <h1>Nimbus</h1>
    <p class="sub">Framework MVC ligero &mdash; compatible Java 8 / IBM WAS 8.5 / Tomcat 7-9</p>

    <div class="leyenda">
        <span class="badge cat-binding">Binding &amp; MVC</span>
        <span class="badge cat-inyeccion">Inyección de dependencias</span>
        <span class="badge cat-validacion">Validación</span>
        <span class="badge cat-archivos">Archivos</span>
        <span class="badge cat-rest">REST</span>
        <span class="badge cat-config">Config. XML</span>
    </div>

    <!-- ================================================================
         TABLA DE EJEMPLOS — una fila por feature, agrupadas por categoría
    ================================================================ -->
    <table>
        <thead>
            <tr>
                <th>Verbo</th>
                <th>URL</th>
                <th>Controller</th>
                <th>Categoría</th>
                <th>Qué demuestra</th>
                <th>Probar</th>
            </tr>
        </thead>
        <tbody>

            <tr>
                <td class="verb get">GET</td>
                <td><code>/home.do</code></td>
                <td><code>HomeController</code></td>
                <td><span class="badge cat-binding">Binding</span></td>
                <td>Retorna <code>ModelAndView</code> sin necesitar request/response en la firma</td>
                <td class="accion"><a href="${ctx}/home.do">Abrir</a></td>
            </tr>

            <tr>
                <td class="verb post">POST</td>
                <td><code>/greeting.do</code></td>
                <td><code>HomeController</code></td>
                <td><span class="badge cat-binding">Binding</span></td>
                <td><code>@ModelAttribute</code> + <code>Model</code> → forward directo a la vista</td>
                <td class="accion">↓ Formulario A</td>
            </tr>

            <tr>
                <td class="verb post">POST</td>
                <td><code>/greeting-prg.do</code></td>
                <td><code>HomeController</code></td>
                <td><span class="badge cat-binding">Binding</span></td>
                <td>Patrón POST → <code>redirect:</code> → GET (Post/Redirect/Get)</td>
                <td class="accion">↓ Formulario B</td>
            </tr>

            <tr>
                <td class="verb get">GET</td>
                <td><code>/resultado.do?…</code></td>
                <td><code>HomeController</code></td>
                <td><span class="badge cat-binding">Binding</span></td>
                <td>Varios <code>@RequestParam</code> requeridos, sin <code>Model</code> manual</td>
                <td class="accion">
                    <a href="${ctx}/resultado.do?nombre=Demo&userId=USR-99&saludo=Hola+Demo&info=Activo">Abrir</a>
                </td>
            </tr>

            <tr>
                <td class="verb get">GET</td>
                <td><code>/usuario/<strong>{id}</strong>.do</code></td>
                <td><code>HomeController</code></td>
                <td><span class="badge cat-binding">Binding</span></td>
                <td><code>@PathVariable</code> — una variable de ruta</td>
                <td class="accion">
                    <a href="${ctx}/usuario/USR-001.do">USR-001</a>
                    <a href="${ctx}/usuario/USR-042.do">USR-042</a>
                </td>
            </tr>

            <tr>
                <td class="verb get">GET</td>
                <td><code>/curso/<strong>{cursoId}</strong>/alumno/<strong>{alumnoId}</strong>.do</code></td>
                <td><code>HomeController</code></td>
                <td><span class="badge cat-binding">Binding</span></td>
                <td>Múltiples <code>@PathVariable</code> en la misma ruta</td>
                <td class="accion"><a href="${ctx}/curso/JAVA-101/alumno/USR-003.do">JAVA-101 / USR-003</a></td>
            </tr>

            <tr>
                <td class="verb get">GET</td>
                <td><code>/cursos/lista.do</code></td>
                <td><code>CursoController</code></td>
                <td><span class="badge cat-binding">Binding</span></td>
                <td>Listado + CRUD completo, datos en memoria (sin base de datos)</td>
                <td class="accion"><a href="${ctx}/cursos/lista.do">Ver cursos</a></td>
            </tr>

            <tr>
                <td class="verb post">POST</td>
                <td><code>/cursos/guardar.do</code><br/><code style="opacity:.7">/cursos/actualizar.do</code></td>
                <td><code>CursoController</code></td>
                <td><span class="badge cat-validacion">Validación</span></td>
                <td>
                    <code>@ModelAttribute @Validated</code> + <code>BindingResult</code> + <code>ModelMap</code> +
                    <code>HttpServletRequest</code>. La validación corre por un <code>Validator</code> custom
                    (<code>CursoValidator</code>) conectado con <code>@InitBinder</code>, con mensajes resueltos
                    desde un <code>MessageSource</code> (bean XML).
                </td>
                <td class="accion"><a href="${ctx}/cursos/nuevo.do">Nuevo curso</a></td>
            </tr>

            <tr>
                <td class="verb get">GET</td>
                <td><code>/cursos/exportar.do</code></td>
                <td><code>CursoController</code></td>
                <td><span class="badge cat-archivos">Archivos</span></td>
                <td>
                    <code>HttpServletRequest</code> + <code>HttpServletResponse</code> como parámetros; escribe el
                    archivo directo al response y retorna <code>null</code> — sin vista JSP.
                </td>
                <td class="accion"><a href="${ctx}/cursos/exportar.do">Descargar CSV</a></td>
            </tr>

            <tr>
                <td class="verb get">GET</td>
                <td><code>/about.do</code></td>
                <td><code>HomeController</code></td>
                <td><span class="badge cat-config">Config. XML</span></td>
                <td>
                    Bean 100% XML (<code>&lt;bean&gt;</code> + <code>&lt;property value&gt;</code>/<code>ref&gt;</code>),
                    cargado vía <code>&lt;import resource="..."&gt;</code>, incluyendo un
                    <code>&lt;component-scan&gt;</code> declarado DENTRO del archivo importado.
                </td>
                <td class="accion"><a href="${ctx}/about.do">Abrir</a></td>
            </tr>

            <tr>
                <td class="verb get">GET</td>
                <td><code>/notificaciones/demo.do?mensaje=</code></td>
                <td><code>NotificacionMvcController</code></td>
                <td><span class="badge cat-inyeccion">Inyección</span></td>
                <td>
                    Dos implementaciones de la misma interfaz (<code>NotificationService</code>), desambiguadas con
                    <code>@Qualifier</code> por CAMPO y por CONSTRUCTOR en el mismo controller
                    (<code>@Controller</code> normal, no REST).
                </td>
                <td class="accion"><a href="${ctx}/notificaciones/demo.do?mensaje=Hola">Abrir</a></td>
            </tr>

            <tr style="background:#fff5f9;">
                <td class="verb get" style="color:#166534;">GET</td>
                <td><code>/api/cursos</code><br/><code style="opacity:.7">/api/cursos/<strong>{id}</strong></code></td>
                <td rowspan="3" style="vertical-align:middle;"><code>CursoRestController</code></td>
                <td rowspan="3" style="vertical-align:middle;"><span class="badge cat-rest">REST</span></td>
                <td rowspan="3" style="vertical-align:middle;">
                    <code>@RestController</code> + <code>@RequestMapping</code>, <code>@RequestBody @Valid</code>,
                    <code>ResponseEntity&lt;T&gt;</code>. Verbos: GET · POST · PUT · DELETE.
                    <span style="font-size:11px;color:#64748b;">Probá también <code>/api/cursos/999</code> para ver
                    el <code>@ExceptionHandler</code> local (404).</span>
                </td>
                <td rowspan="3" class="accion" style="vertical-align:middle;">
                    <a href="${ctx}/api-explorer.do" style="background:#9d174d;">API Explorer &#8599;</a>
                </td>
            </tr>
            <tr style="background:#fff5f9;">
                <td class="verb post" style="color:#854d0e;">POST / PUT</td>
                <td><code>/api/cursos</code><br/><code style="opacity:.7">/api/cursos/<strong>{id}</strong></code></td>
            </tr>
            <tr style="background:#fff5f9;">
                <td class="verb delete" style="color:#991b1b;">DELETE</td>
                <td><code>/api/cursos/<strong>{id}</strong></code></td>
            </tr>

            <tr>
                <td class="verb get">GET</td>
                <td><code>/api/notificaciones/demo?mensaje=</code></td>
                <td><code>NotificacionController</code></td>
                <td><span class="badge cat-inyeccion">Inyección</span></td>
                <td>Mismo patrón de <code>@Qualifier</code> (campo + constructor) que arriba, pero expuesto como
                    <code>@RestController</code> — requiere header <code>X-API-Key</code>.</td>
                <td class="accion">
                    <code class="curl">curl -H "X-API-Key: nimbus-secret-2024" \
http://localhost:8090${ctx}/api/notificaciones/demo</code>
                </td>
            </tr>

            <tr>
                <td class="verb get">GET</td>
                <td><code>/api/scopes/info</code><br/><code style="opacity:.7">/api/scopes/new</code></td>
                <td><code>ScopeController</code></td>
                <td><span class="badge cat-inyeccion">Inyección</span></td>
                <td><code>@Scope("prototype")</code> — cada inyección/<code>getBean()</code> entrega una instancia
                    distinta; requiere header <code>X-API-Key</code>.</td>
                <td class="accion">
                    <code class="curl">curl -H "X-API-Key: nimbus-secret-2024" \
http://localhost:8090${ctx}/api/scopes/info</code>
                </td>
            </tr>

        </tbody>
    </table>

    <hr/>

    <!-- ================================================================
         FORMULARIOS — únicos endpoints POST sin página propia
    ================================================================ -->
    <div class="forms">

        <div class="card">
            <h3>Formulario A <span class="badge cat-binding">Binding</span></h3>
            <p class="note">POST directo → <code>@ModelAttribute</code> + <code>Model</code> → forward a <code>greeting.jsp</code></p>
            <form action="${ctx}/greeting.do" method="post">
                <label>Nombre:</label>
                <input type="text" name="nombre" placeholder="Tu nombre" required/>
                <label>ID usuario:</label>
                <input type="text" name="userId" placeholder="USR-001"/>
                <button class="btn btn-blue" type="submit">Enviar (forward)</button>
            </form>
        </div>

        <div class="card">
            <h3>Formulario B <span class="badge cat-binding">Binding</span></h3>
            <p class="note">POST → <code>redirect:</code> → GET con <code>@RequestParam</code> → <code>ModelAndView</code></p>
            <form action="${ctx}/greeting-prg.do" method="post">
                <label>Nombre:</label>
                <input type="text" name="nombre" placeholder="Tu nombre" required/>
                <label>ID usuario:</label>
                <input type="text" name="userId" placeholder="USR-002"/>
                <button class="btn btn-yellow" type="submit">Enviar (PRG)</button>
            </form>
        </div>

    </div>

</body>
</html>
