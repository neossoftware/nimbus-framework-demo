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

import com.example.service.NotificationService;
import com.nimbusframework.annotation.Autowired;
import com.nimbusframework.annotation.Qualifier;
import com.nimbusframework.annotation.RequestMapping;
import com.nimbusframework.annotation.RequestParam;
import com.nimbusframework.annotation.RestController;
import com.nimbusframework.web.RequestMethod;
import com.nimbusframework.web.ResponseEntity;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Demuestra inyección de dependencias por CONSTRUCTOR y por CAMPO, ambas con
 * @Qualifier — necesario porque hay dos implementaciones de NotificationService
 * (EmailNotificationService y SmsNotificationService) y el framework no puede
 * elegir una por tipo sin ambigüedad.
 *
 * GET /api/notificaciones/demo?mensaje=Hola
 */
@RestController
@RequestMapping("/api/notificaciones")
public class NotificacionController {

    // Inyección por CAMPO + @Qualifier
    @Autowired
    @Qualifier("emailNotificationService")
    private NotificationService emailService;

    // Inyección por CONSTRUCTOR + @Qualifier
    private final NotificationService smsService;

    @Autowired
    public NotificacionController(@Qualifier("smsNotificationService") NotificationService smsService) {
        this.smsService = smsService;
    }

    @RequestMapping(value = "/demo", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<Map<String, String>> demo(@RequestParam(value = "mensaje", defaultValue = "Hola desde Nimbus") String mensaje) {
        Map<String, String> resultado = new LinkedHashMap<>();
        resultado.put("porCampoQualifier",       emailService.getClass().getSimpleName());
        resultado.put("porCampoResultado",       emailService.notificar(mensaje));
        resultado.put("porConstructorQualifier", smsService.getClass().getSimpleName());
        resultado.put("porConstructorResultado", smsService.notificar(mensaje));
        return ResponseEntity.ok(resultado);
    }
}
