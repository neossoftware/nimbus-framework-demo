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
import com.nimbusframework.annotation.Controller;
import com.nimbusframework.annotation.GetMapping;
import com.nimbusframework.annotation.Qualifier;
import com.nimbusframework.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Mismo patrón de inyección que NotificacionController (campo + constructor,
 * ambos con @Qualifier), pero en un @Controller MVC normal en vez de un
 * @RestController — prueba que la inyección de dependencias no distingue
 * entre estereotipos: ambas anotaciones son solo @Component por debajo.
 *
 * GET /notificaciones/demo.do?mensaje=Hola
 */
@Controller
public class NotificacionMvcController {

    // Inyección por CAMPO + @Qualifier
    @Autowired
    @Qualifier("emailNotificationService")
    private NotificationService emailService;

    // Inyección por CONSTRUCTOR + @Qualifier
    private final NotificationService smsService;

    @Autowired
    public NotificacionMvcController(@Qualifier("smsNotificationService") NotificationService smsService) {
        this.smsService = smsService;
    }

    @GetMapping("/notificaciones/demo.do")
    public String demo(@RequestParam(value = "mensaje", defaultValue = "Hola desde Nimbus") String mensaje,
                        HttpServletResponse response) throws IOException {
        response.setContentType("text/plain;charset=UTF-8");
        response.getWriter().println("porCampoQualifier (@Controller MVC)      = " + emailService.getClass().getSimpleName());
        response.getWriter().println("porCampoResultado                       = " + emailService.notificar(mensaje));
        response.getWriter().println("porConstructorQualifier (@Controller MVC) = " + smsService.getClass().getSimpleName());
        response.getWriter().println("porConstructorResultado                  = " + smsService.notificar(mensaje));
        return null;
    }
}
