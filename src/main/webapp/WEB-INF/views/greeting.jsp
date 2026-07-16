<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Saludo</title>
    <style>
        body  { font-family: Arial, sans-serif; max-width: 600px; margin: 40px auto; }
        h1    { color: #2c5f8a; }
        .card { background: #f4f7fb; border-radius: 8px; padding: 20px; margin-top: 20px; }
        a     { color: #2c5f8a; }
    </style>
</head>
<body>
    <h1>Resultado</h1>

    <div class="card">
        <p><strong>Saludo:</strong> <c:out value="${saludo}"/></p>
        <p><strong>Info:</strong>   <c:out value="${info}"/></p>
    </div>

    <p><a href="home.do">&larr; Volver al inicio</a></p>
</body>
</html>
