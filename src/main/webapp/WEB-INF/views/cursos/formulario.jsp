<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>${titulo} — Nimbus</title>
    <style>
        * { box-sizing: border-box; }
        body  { font-family: Arial, sans-serif; max-width: 600px; margin: 40px auto; padding: 0 20px; color: #222; }
        h1    { color: #2c5f8a; margin-bottom: 4px; }
        .sub  { color: #666; font-size: 14px; margin-bottom: 28px; }

        .card { background: #f4f7fb; border-radius: 8px; padding: 28px; border: 1px solid #dde4f0; }

        label  { display: block; margin-bottom: 5px; font-size: 13px; font-weight: bold; color: #444; }
        input[type=text], input[type=number], select, textarea {
            width: 100%; padding: 8px 10px; margin-bottom: 16px;
            border: 1px solid #ccd; border-radius: 4px; font-size: 13px; font-family: inherit;
        }
        textarea { height: 80px; resize: vertical; }
        select   { background: #fff; }

        .actions { display: flex; gap: 12px; margin-top: 4px; }
        .btn      { padding: 9px 18px; border: none; border-radius: 4px;
                    cursor: pointer; font-size: 13px; text-decoration: none; display: inline-block; }
        .btn-blue  { background: #2c5f8a; color: #fff; }
        .btn-blue:hover { background: #1a3d5c; }
        .btn-gray  { background: #e0e0e0; color: #333; }
        .btn-gray:hover { background: #ccc; }
        .errores { background: #fdecea; border: 1px solid #f5c2c0; color: #9c2b23;
                   border-radius: 4px; padding: 12px 16px; margin-bottom: 20px; font-size: 13px; }
        .errores ul { margin: 6px 0 0; padding-left: 18px; }
    </style>
</head>
<body>

    <h1><c:out value="${titulo}"/></h1>
    <p class="sub">Completa los datos del curso y guarda los cambios.</p>

    <c:if test="${formfail == 'true'}">
        <div class="errores">
            <strong>Revisa los siguientes errores:</strong>
            <ul>
                <c:forEach var="error" items="${errores}">
                    <li><c:out value="${error}"/></li>
                </c:forEach>
            </ul>
            <p style="margin: 8px 0 0; font-size: 11px; color: #a9433c;">
                (HttpServletRequest inyectado &mdash; <c:out value="${requestInfo}"/>)
            </p>
        </div>
    </c:if>

    <div class="card">
        <form action="${ctx}${formAction}" method="post">

            <%-- Campo oculto id: 0 para nuevo, valor real para editar --%>
            <input type="hidden" name="id" value="${curso.id}"/>

            <label for="nombre">Nombre del curso</label>
            <input type="text" id="nombre" name="nombre"
                   value="<c:out value="${curso.nombre}"/>" placeholder="Ej. Java Básico" required/>

            <label for="descripcion">Descripción</label>
            <textarea id="descripcion" name="descripcion"
                      placeholder="Breve descripción del curso"><c:out value="${curso.descripcion}"/></textarea>

            <label for="duracionHoras">Duración (horas)</label>
            <input type="number" id="duracionHoras" name="duracionHoras"
                   value="${curso.duracionHoras}" min="1" max="500" required/>

            <label for="nivel">Nivel</label>
            <select id="nivel" name="nivel" required>
                <option value="">-- Selecciona un nivel --</option>
                <option value="BASICO"      ${curso.nivel == 'BASICO'      ? 'selected' : ''}>Básico</option>
                <option value="INTERMEDIO"  ${curso.nivel == 'INTERMEDIO'  ? 'selected' : ''}>Intermedio</option>
                <option value="AVANZADO"    ${curso.nivel == 'AVANZADO'    ? 'selected' : ''}>Avanzado</option>
            </select>

            <div class="actions">
                <button type="submit" class="btn btn-blue">Guardar</button>
                <a href="${ctx}/cursos/lista.do" class="btn btn-gray">Cancelar</a>
            </div>

        </form>
    </div>

    <p style="margin-top: 20px;">
        <a href="${ctx}/cursos/lista.do" style="color:#2c5f8a;">&larr; Volver a la lista</a>
    </p>

</body>
</html>
