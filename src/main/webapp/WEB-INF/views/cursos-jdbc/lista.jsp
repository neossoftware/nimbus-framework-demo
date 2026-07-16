<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Cursos (H2/JDBC) — Nimbus</title>
    <style>
        * { box-sizing: border-box; }
        body { font-family: Arial, sans-serif; max-width: 900px; margin: 40px auto; padding: 0 20px; color: #222; }
        h1   { color: #2c5f8a; margin-bottom: 4px; }
        .sub { color: #666; font-size: 14px; margin-bottom: 24px; }

        .toolbar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
        .btn       { display: inline-block; padding: 8px 16px; border-radius: 4px; text-decoration: none;
                     font-size: 13px; cursor: pointer; border: none; }
        .btn-green { background: #28a745; color: #fff; }
        .btn-green:hover { background: #1e7e34; }
        .btn-blue  { background: #2c5f8a; color: #fff; }
        .btn-blue:hover  { background: #1a3d5c; }

        table { width: 100%; border-collapse: collapse; }
        thead { background: #2c5f8a; color: #fff; }
        thead th { padding: 10px 14px; text-align: left; font-size: 13px; }
        tbody tr:nth-child(even) { background: #f4f7fb; }
        tbody tr:hover           { background: #e8f0fb; }
        td { padding: 10px 14px; font-size: 13px; border-bottom: 1px solid #e0e6ef; vertical-align: middle; }

        .badge-nivel { display: inline-block; padding: 2px 8px; border-radius: 3px;
                       font-size: 11px; font-weight: bold; }
        .BASICO      { background: #d4edda; color: #155724; }
        .INTERMEDIO  { background: #fff3cd; color: #856404; }
        .AVANZADO    { background: #f8d7da; color: #721c24; }

        .actions a  { color: #2c5f8a; text-decoration: none; margin-right: 10px; font-size: 12px; }
        .actions a.del { color: #c0392b; }
        .actions a:hover { text-decoration: underline; }

        .empty { text-align: center; padding: 40px; color: #888; font-style: italic; }
    </style>
</head>
<body>

    <h1>Gestión de Cursos (H2/JDBC)</h1>
    <p class="sub">CRUD completo con datos persistidos en H2 vía JdbcTemplate/NamedParameterJdbcTemplate</p>

    <div class="toolbar">
        <span><strong><c:out value="${cursos.size()}"/> curso(s) registrados</strong></span>
        <span>
            <a href="${ctx}/cursos-jdbc/exportar.do" class="btn btn-blue">⭳ Exportar CSV</a>
            <a href="${ctx}/cursos-jdbc/nuevo.do" class="btn btn-green">+ Nuevo Curso</a>
        </span>
    </div>

    <table>
        <thead>
            <tr>
                <th>#</th>
                <th>Nombre</th>
                <th>Descripción</th>
                <th>Horas</th>
                <th>Nivel</th>
                <th>Acciones</th>
            </tr>
        </thead>
        <tbody>
            <c:choose>
                <c:when test="${empty cursos}">
                    <tr>
                        <td colspan="6" class="empty">No hay cursos registrados. <a href="${ctx}/cursos-jdbc/nuevo.do">Crea el primero</a>.</td>
                    </tr>
                </c:when>
                <c:otherwise>
                    <c:forEach var="c" items="${cursos}">
                        <tr>
                            <td>${c.id}</td>
                            <td><strong><c:out value="${c.nombre}"/></strong></td>
                            <td><c:out value="${c.descripcion}"/></td>
                            <td>${c.duracionHoras} h</td>
                            <td>
                                <span class="badge-nivel ${c.nivel}">
                                    <c:out value="${c.nivel}"/>
                                </span>
                            </td>
                            <td class="actions">
                                <a href="${ctx}/cursos-jdbc/editar/${c.id}.do">Editar</a>
                                <a href="${ctx}/cursos-jdbc/eliminar/${c.id}.do" class="del"
                                   onclick="return confirm('¿Eliminar el curso &quot;<c:out value="${c.nombre}"/>&quot;?')">Eliminar</a>
                            </td>
                        </tr>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
        </tbody>
    </table>

    <p style="margin-top: 24px;">
        <a href="${ctx}/home.do" style="color:#2c5f8a;">&larr; Inicio</a>
        &nbsp;|&nbsp;
        <a href="${ctx}/cursos/lista.do" style="color:#2c5f8a;">Ver la variante en memoria &rarr;</a>
    </p>

</body>
</html>
