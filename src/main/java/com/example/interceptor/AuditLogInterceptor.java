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

import com.nimbusframework.web.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;

/**
 * Interceptor de auditoría: registra cada petición con método, path, status y tiempo.
 *
 * No bloquea ninguna petición (preHandle siempre retorna true).
 * Guarda el timestamp de inicio en un atributo del request para que
 * afterCompletion calcule la duración exacta.
 */
public class AuditLogInterceptor implements HandlerInterceptor {

    private static final Logger log = Logger.getLogger(AuditLogInterceptor.class.getName());

    private static final String ATTR_START = "nimbus.audit.startTime";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                              Object handler) {
        request.setAttribute(ATTR_START, System.currentTimeMillis());
        log.info("[AUDIT] --> " + request.getMethod() + " " + request.getRequestURI());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                 Object handler, Exception ex) {
        Long startTime = (Long) request.getAttribute(ATTR_START);
        long duration  = (startTime != null) ? System.currentTimeMillis() - startTime : -1;

        String status = String.valueOf(response.getStatus());
        String uri    = request.getRequestURI();
        String method = request.getMethod();

        if (ex != null) {
            log.warning("[AUDIT] <-- " + method + " " + uri
                + " | status=" + status
                + " | " + duration + "ms"
                + " | ERROR=" + ex.getClass().getSimpleName());
        } else {
            log.info("[AUDIT] <-- " + method + " " + uri
                + " | status=" + status
                + " | " + duration + "ms");
        }
    }
}
