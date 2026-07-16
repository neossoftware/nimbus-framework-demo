<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Resultado</title>
    <style>
        body  { font-family: Arial, sans-serif; max-width: 600px; margin: 40px auto; }
        h1    { color: #2c5f8a; }
        .card { background: #f4f7fb; border-radius: 8px; padding: 20px; margin-top: 20px; }
        .row  { margin-bottom: 10px; }
        .label { font-weight: bold; color: #555; display: inline-block; width: 80px; }
        .badge { background: #2c5f8a; color: #fff; font-size: 11px;
                 padding: 2px 8px; border-radius: 4px; margin-left: 8px; }
        a { color: #2c5f8a; }
    </style>
</head>
<body>
    <h1>Resultado <span class="badge">POST-Redirect-GET</span></h1>

    <div class="card">
        <div class="row">
            <span class="label">Nombre:</span>
            <c:out value="${nombre}"/>
        </div>
        <div class="row">
            <span class="label">ID:</span>
            <c:out value="${userId}"/>
        </div>
        <div class="row">
            <span class="label">Saludo:</span>
            <c:out value="${saludo}"/>
        </div>
        <div class="row">
            <span class="label">Info:</span>
            <c:out value="${info}"/>
        </div>
    </div>

    <%-- Los datos llegan via @RequestParam desde la query string del redirect --%>
    <p style="color:#888; font-size:12px;">
        URL: <c:out value="${pageContext.request.requestURI}"/>
             <c:out value="${pageContext.request.queryString}"/>
    </p>

    <p><a href="home.do">&larr; Volver al inicio</a></p>
</body>
</html>
