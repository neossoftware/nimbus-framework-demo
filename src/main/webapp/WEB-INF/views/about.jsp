<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Acerca de</title>
    <style>
        body { font-family: Arial, sans-serif; max-width: 600px; margin: 40px auto; }
        h1   { color: #2c5f8a; }
        a    { color: #2c5f8a; }
        table { border-collapse: collapse; width: 100%; margin-top: 16px; }
        td, th { border: 1px solid #ccc; padding: 8px 12px; text-align: left; }
        th { background: #2c5f8a; color: #fff; }
    </style>
</head>
<body>
    <h1>Acerca de Nimbus</h1>

    <table>
        <tr><th>Propiedad</th><th>Valor</th></tr>
        <tr><td>Versión del framework</td><td><c:out value="${version}"/></td></tr>
        <tr><td>Java Version</td>          <td><c:out value="${javaVersion}"/></td></tr>
        <tr><td>Compatibilidad</td>        <td>Java 8 / IBM WAS 8.5</td></tr>
        <tr><td>Motor de vistas</td>       <td>JSP + JSTL</td></tr>
        <tr><td>Bean vía &lt;import&gt;/&lt;property&gt;</td> <td><c:out value="${welcomeMensaje}"/></td></tr>
        <tr><td>&lt;component-scan&gt; dentro del import</td> <td><c:out value="${otroPaqueteMensaje}"/></td></tr>
    </table>

    <p><a href="home.do">&larr; Volver al inicio</a></p>
</body>
</html>
