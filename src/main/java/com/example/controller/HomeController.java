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

import com.example.config.WelcomeBanner;
import com.example.model.Greeting;
import com.example.service.UserService;
import com.otropaquete.OtroPaqueteService;
import com.nimbusframework.annotation.Autowired;
import com.nimbusframework.annotation.Controller;
import com.nimbusframework.annotation.GetMapping;
import com.nimbusframework.annotation.ModelAttribute;
import com.nimbusframework.annotation.PostMapping;
import com.nimbusframework.annotation.PathVariable;
import com.nimbusframework.annotation.RequestParam;
import com.nimbusframework.web.Model;
import com.nimbusframework.web.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class HomeController {

    @Autowired
    private UserService userService;

    // Bean configurado 100% por XML (application/config/beans-config.xml, cargado
    // vía <import> en framework-config.xml) — no lleva @Component/@Service.
    @Autowired
    private WelcomeBanner welcomeBanner;

    // Escaneado desde <component-scan base-package="com.otropaquete"/>, declarado
    // DENTRO de beans-config.xml (no en framework-config.xml).
    @Autowired
    private OtroPaqueteService otroPaqueteService;

    // -----------------------------------------------------------------------
    // Sin request ni response — el framework no los inyecta si no se piden
    // -----------------------------------------------------------------------

    @GetMapping("/home.do")
    public ModelAndView home() {
        return new ModelAndView("home")
                .addObject("titulo",   "Página Principal")
                .addObject("greeting", new Greeting("Emiliano","20"));
    }

    // -----------------------------------------------------------------------
    // Solo Model + @ModelAttribute — sin tocar el request manualmente
    // -----------------------------------------------------------------------

    @PostMapping("/greeting.do")
    public String greeting(@ModelAttribute Greeting greeting, Model model) {
        model.addAttribute("saludo", userService.greet(greeting.getNombre()));
        model.addAttribute("info",   userService.getUserInfo(greeting.getUserId()));
        return "greeting";
    }

    // -----------------------------------------------------------------------
    // Solo @ModelAttribute — redirect no necesita ni Model ni request/response
    // -----------------------------------------------------------------------

    @PostMapping("/greeting-prg.do")
    public String greetingPrg(@ModelAttribute Greeting greeting) {
        String saludo = userService.greet(greeting.getNombre());
        String info   = userService.getUserInfo(greeting.getUserId());
        return "redirect:/resultado.do"
             + "?nombre=" + encode(greeting.getNombre())
             + "&userId=" + encode(greeting.getUserId())
             + "&saludo=" + encode(saludo)
             + "&info="   + encode(info);
    }

    // -----------------------------------------------------------------------
    // Solo @RequestParam — sin request/response ni Model
    // -----------------------------------------------------------------------

    @GetMapping("/resultado.do")
    public ModelAndView resultado(@RequestParam("nombre") String nombre,
                                  @RequestParam("userId") String userId,
                                  @RequestParam("saludo") String saludo,
                                  @RequestParam("info")   String info) {
        return new ModelAndView("result")
                .addObject("nombre", nombre)
                .addObject("userId", userId)
                .addObject("saludo", saludo)
                .addObject("info",   info);
    }

    // -----------------------------------------------------------------------
    // @PathVariable — extrae segmento de la URL directamente al parámetro
    // Template:  /usuario/{id}.do
    // URL real:  /usuario/USR-001.do   →  id = "USR-001"
    // -----------------------------------------------------------------------

    @GetMapping("/usuario/{id}.do")
    public ModelAndView showUsuario(@PathVariable("id") String id, Model model) {
        String info = userService.getUserInfo(id);
        return new ModelAndView("usuario")
                .addObject("userId", id)
                .addObject("info",   info);
    }

    // Múltiples variables en la misma ruta
    // Template:  /curso/{cursoId}/alumno/{alumnoId}.do
    @GetMapping("/curso/{cursoId}/alumno/{alumnoId}.do")
    public ModelAndView showAlumno(@PathVariable("cursoId")  String cursoId,
                                   @PathVariable("alumnoId") String alumnoId) {
        return new ModelAndView("alumno")
                .addObject("cursoId",  cursoId)
                .addObject("alumnoId", alumnoId)
                .addObject("info",     userService.getUserInfo(alumnoId));
    }

    // -----------------------------------------------------------------------
    // Cuando SÍ necesitas el request/response los agregas — sigue funcionando
    // -----------------------------------------------------------------------

    @GetMapping("/api-explorer.do")
    public String apiExplorer() {
        return "api-explorer";
    }

    @GetMapping("/about.do")
    public String about(HttpServletRequest request, HttpServletResponse response, Model model) {
        model.addAttribute("version",     "1.0.0");
        model.addAttribute("javaVersion", System.getProperty("java.version"));
        model.addAttribute("contextPath", request.getContextPath());
        model.addAttribute("welcomeMensaje", welcomeBanner.mensaje());
        model.addAttribute("otroPaqueteMensaje", otroPaqueteService.mensaje());
        return "about";
    }

    private static String encode(String value) {
        if (value == null) return "";
        try {
            return java.net.URLEncoder.encode(value, "UTF-8");
        } catch (Exception e) {
            return value;
        }
    }
}
