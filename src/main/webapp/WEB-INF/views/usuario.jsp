<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Usuario</title>
    <style>
        body  { font-family: Arial, sans-serif; max-width: 600px; margin: 40px auto; }
        h1    { color: #2c5f8a; }
        .card { background: #f4f7fb; border-radius: 8px; padding: 20px; margin-top: 20px; }
        .badge { background: #856404; color: #fff; font-size: 11px;
                 padding: 2px 8px; border-radius: 4px; margin-left: 8px; }
        .row  { margin-bottom: 10px; }
        .label { font-weight: bold; color: #555; display: inline-block; width: 90px; }
        a { color: #2c5f8a; }
    </style>
</head>
<body>
    <h1>Detalle de Usuario <span class="badge">@PathVariable</span></h1>

    <div class="card">
        <div class="row">
            <span class="label">ID:</span>
            <c:out value="${userId}"/>
        </div>
        <div class="row">
            <span class="label">Info:</span>
            <c:out value="${info}"/>
        </div>
    </div>

    <%-- El id llegó desde la URL /usuario/{id}.do via @PathVariable --%>
    <p style="color:#888; font-size:12px;">
        URL: <c:out value="${pageContext.request.requestURI}"/>
    </p>

    <p>
        <a href="${pageContext.request.contextPath}/usuario/USR-001.do">USR-001</a> |
        <a href="${pageContext.request.contextPath}/usuario/USR-002.do">USR-002</a> |
        <a href="${pageContext.request.contextPath}/curso/JAVA-101/alumno/USR-003.do">Curso con 2 vars</a>
    </p>

    <p><a href="${pageContext.request.contextPath}/home.do">&larr; Inicio</a></p>
</body>
</html>
