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
package com.example.controller;

import com.example.component.RequestTracker;
import com.nimbusframework.annotation.Autowired;
import com.nimbusframework.annotation.RequestMapping;
import com.nimbusframework.annotation.RestController;
import com.nimbusframework.context.ApplicationContext;
import com.nimbusframework.web.RequestMethod;
import com.nimbusframework.web.ResponseEntity;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Demuestra @Scope("prototype") vs singleton.
 *
 * GET /api/scopes/info
 *   → identityHashCode de los dos RequestTracker inyectados al arrancar.
 *     Deben ser DISTINTOS entre sí (cada @Autowired recibió una instancia diferente).
 *
 * GET /api/scopes/new
 *   → crea una instancia fresca bajo demanda vía ApplicationContext.getBean().
 *     Cada llamada HTTP obtiene un instanceId diferente.
 */
@RestController
@RequestMapping("/api/scopes")
public class ScopeController {

    /**
     * Dos campos del mismo tipo prototype → dos instancias distintas en startup.
     * Demuestra que la inyección @Autowired de un prototype NO comparte la instancia.
     */
    @Autowired
    private RequestTracker trackerA;

    @Autowired
    private RequestTracker trackerB;

    /**
     * Contexto inyectado para crear prototypes frescos en tiempo de ejecución.
     * Este es el patrón correcto cuando un singleton necesita una instancia NUEVA
     * en cada operación (no solo al arrancar).
     */
    @Autowired
    private ApplicationContext applicationContext;

    /** Muestra las dos instancias inyectadas al arrancar el servidor. */
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> info() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("scope", "prototype");
        result.put("trackerA", trackerA.toString());
        result.put("trackerB", trackerB.toString());
        result.put("mismaInstancia", trackerA == trackerB);
        result.put("nota",
            "trackerA y trackerB fueron inyectados por @Autowired al arrancar. "
            + "Si son distintos, prototype funciona correctamente.");
        return ResponseEntity.ok(result);
    }

    /** Crea una instancia fresca de RequestTracker en CADA llamada HTTP. */
    @RequestMapping(value = "/new", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> newInstance() {
        RequestTracker fresh = applicationContext.getBean(RequestTracker.class);
        if (fresh == null) {
            return ResponseEntity.status(com.nimbusframework.web.HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(singleEntry("error", "RequestTracker no encontrado en el contexto"));
        }
        fresh.incrementAndGet();    // registra una petición en esta instancia

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("scope", "prototype");
        result.put("instanceId", fresh.getInstanceId());
        result.put("requestCount", fresh.getRequestCount());
        result.put("identity", "@" + Integer.toHexString(System.identityHashCode(fresh)));
        result.put("nota",
            "Cada llamada a /api/scopes/new genera una instancia diferente con nuevo instanceId.");
        return ResponseEntity.ok(result);
    }

    private static Map<String, Object> singleEntry(String k, Object v) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put(k, v);
        return m;
    }
}
