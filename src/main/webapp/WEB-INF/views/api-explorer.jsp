<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Nimbus — REST API Explorer</title>
    <style>
        *, *::before, *::after { box-sizing: border-box; margin: 0; padding: 0; }

        body {
            font-family: Arial, sans-serif;
            background: #f0f4f8;
            color: #222;
            padding: 32px 20px 60px;
        }

        .page-wrap { max-width: 860px; margin: 0 auto; }

        /* ── Header ─────────────────────────────────── */
        .page-header {
            display: flex; align-items: flex-start; justify-content: space-between;
            margin-bottom: 28px;
        }
        .page-header h1 { font-size: 22px; color: #2c5f8a; margin-bottom: 4px; }
        .page-header p  { font-size: 13px; color: #666; }
        .back-link {
            font-size: 12px; color: #2c5f8a; text-decoration: none;
            border: 1px solid #2c5f8a; padding: 6px 12px; border-radius: 4px;
            white-space: nowrap;
        }
        .back-link:hover { background: #2c5f8a; color: #fff; }

        /* ── Base URL / Auth banner ─────────────────── */
        .base-url {
            background: #1e293b; color: #94d5a2; font-family: monospace;
            font-size: 13px; padding: 10px 16px; border-radius: 6px 6px 0 0;
        }
        .base-url span { color: #7dd3fc; }

        .auth-bar {
            background: #0f172a; border-top: 1px solid #334155;
            padding: 10px 16px; border-radius: 0 0 6px 6px;
            display: flex; align-items: center; gap: 10px;
            margin-bottom: 24px;
        }
        .auth-bar label {
            font-size: 12px; color: #94a3b8; white-space: nowrap;
            font-family: monospace;
        }
        .auth-bar input {
            flex: 1; background: #1e293b; border: 1px solid #475569;
            color: #e2e8f0; font-family: monospace; font-size: 12px;
            padding: 5px 10px; border-radius: 4px;
        }
        .auth-bar input:focus { outline: none; border-color: #7dd3fc; }
        .auth-bar .lock { color: #fbbf24; font-size: 14px; }

        /* ── Endpoint cards ─────────────────────────── */
        .endpoint {
            background: #fff; border-radius: 8px; margin-bottom: 16px;
            border: 1px solid #dde4f0; overflow: hidden;
        }

        .endpoint-header {
            display: flex; align-items: center; gap: 12px;
            padding: 14px 18px; cursor: pointer; user-select: none;
            border-bottom: 1px solid transparent;
            transition: background .15s;
        }
        .endpoint-header:hover { background: #f8fafd; }
        .endpoint.open .endpoint-header { border-bottom-color: #e0e8f4; }

        .method {
            font-family: monospace; font-size: 12px; font-weight: bold;
            padding: 3px 8px; border-radius: 4px; min-width: 62px;
            text-align: center; letter-spacing: .5px;
        }
        .GET    { background: #d1fadf; color: #166534; }
        .POST   { background: #fef9c3; color: #854d0e; }
        .PUT    { background: #dbeafe; color: #1e40af; }
        .DELETE { background: #fee2e2; color: #991b1b; }

        .endpoint-path {
            font-family: monospace; font-size: 14px; color: #1e293b; flex: 1;
        }
        .endpoint-desc { font-size: 12px; color: #64748b; }

        .chevron {
            font-size: 12px; color: #94a3b8;
            transition: transform .2s;
        }
        .endpoint.open .chevron { transform: rotate(180deg); }

        /* ── Endpoint body ──────────────────────────── */
        .endpoint-body {
            display: none; padding: 18px;
            background: #fafcff;
        }
        .endpoint.open .endpoint-body { display: block; }

        .field-group { margin-bottom: 12px; }
        .field-group label {
            display: block; font-size: 12px; font-weight: bold;
            color: #475569; margin-bottom: 4px;
        }
        .field-group input[type=number],
        .field-group input[type=text] {
            width: 100%; padding: 7px 10px; border: 1px solid #cbd5e1;
            border-radius: 5px; font-size: 13px; font-family: monospace;
            background: #fff;
        }
        .field-group input:focus { outline: none; border-color: #2c5f8a; }

        textarea.json-body {
            width: 100%; height: 130px; padding: 10px; border: 1px solid #cbd5e1;
            border-radius: 5px; font-size: 12px; font-family: monospace;
            resize: vertical; background: #1e293b; color: #e2e8f0;
            line-height: 1.6;
        }
        textarea.json-body:focus { outline: none; border-color: #2c5f8a; }

        .run-btn {
            padding: 8px 20px; border: none; border-radius: 5px;
            font-size: 13px; font-weight: bold; cursor: pointer;
            color: #fff; background: #2c5f8a;
            transition: background .15s;
        }
        .run-btn:hover   { background: #1a3d5c; }
        .run-btn:disabled { background: #94a3b8; cursor: default; }

        /* ── Response panel ─────────────────────────── */
        .response-panel {
            margin-top: 14px; border-radius: 6px; overflow: hidden;
            border: 1px solid #e2e8f0; display: none;
        }
        .response-panel.visible { display: block; }

        .response-meta {
            display: flex; align-items: center; gap: 10px;
            padding: 8px 14px; background: #1e293b; font-size: 12px;
        }
        .status-badge {
            font-family: monospace; font-weight: bold;
            padding: 2px 8px; border-radius: 3px;
        }
        .status-2xx { background: #166534; color: #d1fadf; }
        .status-4xx { background: #991b1b; color: #fee2e2; }
        .status-5xx { background: #7c3aed; color: #ede9fe; }
        .response-time { color: #94a3b8; font-size: 11px; }

        .response-body {
            background: #1e293b; color: #e2e8f0;
            font-family: monospace; font-size: 12px;
            padding: 14px; overflow-x: auto; max-height: 340px;
            white-space: pre; line-height: 1.7;
        }

        /* JSON colors */
        .j-key    { color: #7dd3fc; }
        .j-str    { color: #86efac; }
        .j-num    { color: #fca5a5; }
        .j-bool   { color: #c4b5fd; }
        .j-null   { color: #94a3b8; }

        /* ── it6 badge ──────────────────────────────── */
        .badge {
            display: inline-block; font-size: 10px; font-weight: bold;
            padding: 1px 6px; border-radius: 3px; margin-left: 4px;
        }
        .it6 { background: #fce7f3; color: #9d174d; }
    </style>
</head>
<body>
<div class="page-wrap">

    <!-- Header -->
    <div class="page-header">
        <div>
            <h1>REST API Explorer <span class="badge it6">it 6</span></h1>
            <p>Prueba los endpoints de <strong>CursoRestController</strong> directamente desde el navegador</p>
        </div>
        <a class="back-link" href="${ctx}/home.do">&#8592; Home</a>
    </div>

    <!-- Base URL + Auth -->
    <div class="base-url">
        Base URL: <span>${ctx}/api/cursos</span>
    </div>
    <div class="auth-bar">
        <span class="lock">&#128274;</span>
        <label>X-API-Key:</label>
        <input type="text" id="api-key" value="nimbus-secret-2024" placeholder="Introduce tu API key"/>
    </div>

    <!-- ================================================================
         ENDPOINTS
    ================================================================ -->

    <!-- GET /api/cursos -->
    <div class="endpoint open" id="ep-list">
        <div class="endpoint-header" onclick="toggle('ep-list')">
            <span class="method GET">GET</span>
            <span class="endpoint-path">/api/cursos</span>
            <span class="endpoint-desc">Listar todos los cursos</span>
            <span class="chevron">&#9650;</span>
        </div>
        <div class="endpoint-body">
            <button class="run-btn" onclick="run('ep-list')">&#9654; Ejecutar</button>
            <div class="response-panel" id="res-ep-list">
                <div class="response-meta">
                    <span class="status-badge" id="sta-ep-list"></span>
                    <span class="response-time" id="tim-ep-list"></span>
                </div>
                <div class="response-body" id="bod-ep-list"></div>
            </div>
        </div>
    </div>

    <!-- GET /api/cursos/{id} -->
    <div class="endpoint" id="ep-get">
        <div class="endpoint-header" onclick="toggle('ep-get')">
            <span class="method GET">GET</span>
            <span class="endpoint-path">/api/cursos/{id}</span>
            <span class="endpoint-desc">Buscar curso por ID</span>
            <span class="chevron">&#9650;</span>
        </div>
        <div class="endpoint-body">
            <div class="field-group">
                <label>ID del curso</label>
                <input type="number" id="get-id" placeholder="1" min="1"/>
            </div>
            <button class="run-btn" onclick="run('ep-get')">&#9654; Ejecutar</button>
            <div class="response-panel" id="res-ep-get">
                <div class="response-meta">
                    <span class="status-badge" id="sta-ep-get"></span>
                    <span class="response-time" id="tim-ep-get"></span>
                </div>
                <div class="response-body" id="bod-ep-get"></div>
            </div>
        </div>
    </div>

    <!-- POST /api/cursos -->
    <div class="endpoint" id="ep-post">
        <div class="endpoint-header" onclick="toggle('ep-post')">
            <span class="method POST">POST</span>
            <span class="endpoint-path">/api/cursos</span>
            <span class="endpoint-desc">Crear nuevo curso</span>
            <span class="chevron">&#9650;</span>
        </div>
        <div class="endpoint-body">
            <div class="field-group">
                <label>Request Body (JSON)</label>
                <textarea class="json-body" id="post-body">{
  "nombre": "Kubernetes Avanzado",
  "descripcion": "Orquestación de contenedores en producción",
  "duracionHoras": 45,
  "nivel": "AVANZADO"
}</textarea>
            </div>
            <button class="run-btn" onclick="run('ep-post')">&#9654; Ejecutar</button>
            <div class="response-panel" id="res-ep-post">
                <div class="response-meta">
                    <span class="status-badge" id="sta-ep-post"></span>
                    <span class="response-time" id="tim-ep-post"></span>
                </div>
                <div class="response-body" id="bod-ep-post"></div>
            </div>
        </div>
    </div>

    <!-- PUT /api/cursos/{id} -->
    <div class="endpoint" id="ep-put">
        <div class="endpoint-header" onclick="toggle('ep-put')">
            <span class="method PUT">PUT</span>
            <span class="endpoint-path">/api/cursos/{id}</span>
            <span class="endpoint-desc">Actualizar curso existente</span>
            <span class="chevron">&#9650;</span>
        </div>
        <div class="endpoint-body">
            <div class="field-group">
                <label>ID del curso</label>
                <input type="number" id="put-id" placeholder="1" min="1"/>
            </div>
            <div class="field-group">
                <label>Request Body (JSON)</label>
                <textarea class="json-body" id="put-body">{
  "nombre": "Java 17 — Módulos y Records",
  "descripcion": "Características modernas del lenguaje Java",
  "duracionHoras": 30,
  "nivel": "INTERMEDIO"
}</textarea>
            </div>
            <button class="run-btn" onclick="run('ep-put')">&#9654; Ejecutar</button>
            <div class="response-panel" id="res-ep-put">
                <div class="response-meta">
                    <span class="status-badge" id="sta-ep-put"></span>
                    <span class="response-time" id="tim-ep-put"></span>
                </div>
                <div class="response-body" id="bod-ep-put"></div>
            </div>
        </div>
    </div>

    <!-- DELETE /api/cursos/{id} -->
    <div class="endpoint" id="ep-delete">
        <div class="endpoint-header" onclick="toggle('ep-delete')">
            <span class="method DELETE">DELETE</span>
            <span class="endpoint-path">/api/cursos/{id}</span>
            <span class="endpoint-desc">Eliminar curso</span>
            <span class="chevron">&#9650;</span>
        </div>
        <div class="endpoint-body">
            <div class="field-group">
                <label>ID del curso</label>
                <input type="number" id="del-id" placeholder="1" min="1"/>
            </div>
            <button class="run-btn" onclick="run('ep-delete')">&#9654; Ejecutar</button>
            <div class="response-panel" id="res-ep-delete">
                <div class="response-meta">
                    <span class="status-badge" id="sta-ep-delete"></span>
                    <span class="response-time" id="tim-ep-delete"></span>
                </div>
                <div class="response-body" id="bod-ep-delete"></div>
            </div>
        </div>
    </div>

</div><!-- /page-wrap -->

<script>
    const CTX = '${ctx}';

    // ── Accordion ────────────────────────────────────────────────────────────
    function toggle(id) {
        document.getElementById(id).classList.toggle('open');
    }

    // ── Ejecutar petición ─────────────────────────────────────────────────────
    async function run(epId) {
        const btn  = document.querySelector('#' + epId + ' .run-btn');
        const panel = document.getElementById('res-' + epId);
        const sta   = document.getElementById('sta-' + epId);
        const tim   = document.getElementById('tim-' + epId);
        const bod   = document.getElementById('bod-' + epId);

        btn.disabled = true;
        btn.textContent = '⏳ Ejecutando…';

        const t0 = Date.now();
        let status, text;

        try {
            const opts = buildRequest(epId);
            const resp = await fetch(opts.url, opts.init);
            status = resp.status;
            text   = await resp.text();
        } catch (e) {
            status = 0;
            text   = 'Error de red: ' + e.message;
        }

        const ms = Date.now() - t0;

        // Status badge
        sta.textContent = status || 'ERR';
        sta.className   = 'status-badge ' + badgeClass(status);

        // Time
        tim.textContent = ms + ' ms';

        // Body
        bod.innerHTML = status === 204
            ? '<span style="color:#94a3b8">— Sin contenido (204 No Content) —</span>'
            : highlight(text);

        panel.classList.add('visible');
        btn.disabled = false;
        btn.textContent = '▶ Ejecutar';
    }

    function apiKey() {
        return document.getElementById('api-key').value.trim();
    }

    function buildRequest(epId) {
        const base    = CTX + '/api/cursos';
        const authHdr = { 'X-API-Key': apiKey() };

        if (epId === 'ep-list') {
            return { url: base, init: { method: 'GET', headers: authHdr } };
        }
        if (epId === 'ep-get') {
            const id = document.getElementById('get-id').value || '1';
            return { url: base + '/' + id, init: { method: 'GET', headers: authHdr } };
        }
        if (epId === 'ep-post') {
            return {
                url: base,
                init: {
                    method: 'POST',
                    headers: { ...authHdr, 'Content-Type': 'application/json' },
                    body: document.getElementById('post-body').value
                }
            };
        }
        if (epId === 'ep-put') {
            const id = document.getElementById('put-id').value || '1';
            return {
                url: base + '/' + id,
                init: {
                    method: 'PUT',
                    headers: { ...authHdr, 'Content-Type': 'application/json' },
                    body: document.getElementById('put-body').value
                }
            };
        }
        if (epId === 'ep-delete') {
            const id = document.getElementById('del-id').value || '1';
            return { url: base + '/' + id, init: { method: 'DELETE', headers: authHdr } };
        }
    }

    function badgeClass(status) {
        if (status >= 200 && status < 300) return 'status-2xx';
        if (status >= 400 && status < 500) return 'status-4xx';
        return 'status-5xx';
    }

    // ── JSON syntax highlight ─────────────────────────────────────────────────
    function highlight(text) {
        let json;
        try {
            json = JSON.stringify(JSON.parse(text), null, 2);
        } catch (e) {
            return escHtml(text);
        }
        return json.replace(
            /("(\\u[a-zA-Z0-9]{4}|\\[^u]|[^\\"])*"(\s*:)?|true|false|null|-?\d+(\.\d+)?([eE][+\-]?\d+)?)/g,
            function(match) {
                if (/^"/.test(match)) {
                    if (/:$/.test(match)) return '<span class="j-key">' + escHtml(match) + '</span>';
                    return '<span class="j-str">' + escHtml(match) + '</span>';
                }
                if (/true|false/.test(match)) return '<span class="j-bool">' + match + '</span>';
                if (/null/.test(match))        return '<span class="j-null">' + match + '</span>';
                return '<span class="j-num">' + match + '</span>';
            }
        );
    }

    function escHtml(str) {
        return str.replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;');
    }
</script>
</body>
</html>
