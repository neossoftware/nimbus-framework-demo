/*
 * Copyright (C) 2026 neossoftware
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 * @author neossoftware
 */
package com.example.interceptor;

import com.nimbusframework.annotation.Value;
import com.nimbusframework.web.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Interceptor de autenticación por API Key.
 *
 * Protege todos los paths /api/* exigiendo el header "X-API-Key".
 * La clave esperada se configura en app.properties (auth.api-key)
 * e inyectada con @Value — sin hardcoding.
 *
 * Peticiones no-API (*.do, vistas JSP) pasan sin revisión.
 *
 * Respuesta al fallar:
 *   HTTP 401 + JSON {"error":"Unauthorized","message":"..."}
 */
public class AuthInterceptor implements HandlerInterceptor {

    private static final Logger log = Logger.getLogger(AuthInterceptor.class.getName());

    /** Inyectada desde app.properties → auth.api-key; si no existe usa el literal. */
    @Value("${auth.api-key:nimbus-secret}")
    private String expectedApiKey;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                              Object handler) throws IOException {

        String uri = request.getRequestURI();
        String ctxPath = request.getContextPath();
        // Solo proteger /api/*
        String relativePath = uri.startsWith(ctxPath) ? uri.substring(ctxPath.length()) : uri;
        if (!relativePath.startsWith("/api/")) {
            return true;
        }

        String apiKey = request.getHeader("X-API-Key");
        if (expectedApiKey != null && expectedApiKey.equals(apiKey)) {
            log.fine("[AUTH] OK: " + request.getMethod() + " " + uri);
            return true;
        }

        log.warning("[AUTH] RECHAZADO: " + request.getMethod() + " " + uri
            + " | X-API-Key=" + apiKey);

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(
            "{\"status\":401,\"error\":\"Unauthorized\","
            + "\"message\":\"Header 'X-API-Key' requerido o inválido\","
            + "\"path\":\"" + uri + "\"}");
        return false;
    }
}
