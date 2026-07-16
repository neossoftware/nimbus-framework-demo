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

import com.example.model.Curso;
import com.example.service.CursoService;
import com.nimbusframework.annotation.Autowired;
import com.nimbusframework.annotation.Controller;
import com.nimbusframework.annotation.GetMapping;
import com.nimbusframework.annotation.InitBinder;
import com.nimbusframework.annotation.ModelAttribute;
import com.nimbusframework.annotation.PathVariable;
import com.nimbusframework.annotation.PostMapping;
import com.nimbusframework.annotation.Qualifier;
import com.nimbusframework.annotation.Validated;
import com.nimbusframework.validation.BindingResult;
import com.nimbusframework.validation.Validator;
import com.nimbusframework.web.ModelAndView;
import com.nimbusframework.web.ModelMap;
import com.nimbusframework.web.WebDataBinder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class CursoController {

    @Autowired
    private CursoService cursoService;

    // Validador custom (com.example.validation.CursoValidator), cargado como bean XML
    // y conectado acá vía @InitBinder — reemplaza la validación por anotaciones para
    // los @ModelAttribute @Validated Curso de este controller.
    private final Validator validator;

    @Autowired
    public CursoController(@Qualifier("cursoValidator") Validator validator) {
        this.validator = validator;
    }

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.setValidator(validator);
    }

    @GetMapping("/cursos/lista.do")
    public ModelAndView lista() {
        return new ModelAndView("cursos/lista")
                .addObject("cursos", cursoService.listarTodos());
    }

    @GetMapping("/cursos/nuevo.do")
    public ModelAndView nuevo() {
        return new ModelAndView("cursos/formulario")
                .addObject("curso",      new Curso())
                .addObject("titulo",     "Nuevo Curso")
                .addObject("formAction", "/cursos/guardar.do");
    }

    @PostMapping("/cursos/guardar.do")
    public String guardar(@ModelAttribute("curso") @Validated Curso curso, BindingResult result,
                          ModelMap map, HttpServletRequest request) {
        if (result.hasErrors()) {
            map.addAttribute("formfail",    "true");
            map.addAttribute("errores",     result.getAllErrors());
            map.addAttribute("titulo",      "Nuevo Curso");
            map.addAttribute("formAction",  "/cursos/guardar.do");
            map.addAttribute("requestInfo", request.getMethod() + " " + request.getRequestURI());
            return "cursos/formulario";
        }
        cursoService.guardar(curso);
        return "redirect:/cursos/lista.do";
    }

    @GetMapping("/cursos/editar/{id}.do")
    public ModelAndView editar(@PathVariable("id") int id) {
        Curso curso = cursoService.buscarPorId(id);
        return new ModelAndView("cursos/formulario")
                .addObject("curso",      curso)
                .addObject("titulo",     "Editar Curso")
                .addObject("formAction", "/cursos/actualizar.do");
    }

    @PostMapping("/cursos/actualizar.do")
    public String actualizar(@ModelAttribute("curso") @Validated Curso curso, BindingResult result,
                             ModelMap map, HttpServletRequest request) {
        if (result.hasErrors()) {
            map.addAttribute("formfail",    "true");
            map.addAttribute("errores",     result.getAllErrors());
            map.addAttribute("titulo",      "Editar Curso");
            map.addAttribute("formAction",  "/cursos/actualizar.do");
            map.addAttribute("requestInfo", request.getMethod() + " " + request.getRequestURI());
            return "cursos/formulario";
        }
        cursoService.actualizar(curso);
        return "redirect:/cursos/lista.do";
    }

    @GetMapping("/cursos/eliminar/{id}.do")
    public String eliminar(@PathVariable("id") int id) {
        cursoService.eliminar(id);
        return "redirect:/cursos/lista.do";
    }

    // -----------------------------------------------------------------------
    // Descarga de archivo: HttpServletRequest + HttpServletResponse como
    // parámetros, sin Model ni vista JSP. El método escribe directo al
    // response (headers + bytes) y retorna null — el framework detecta el
    // null en DispatcherServlet.render() y no intenta resolver ninguna vista.
    //
    //   GET /cursos/exportar.do             -> exporta todos los cursos
    //   GET /cursos/exportar.do?nivel=BASICO -> filtra por nivel
    // -----------------------------------------------------------------------

    @GetMapping("/cursos/exportar.do")
    public String exportarCsv(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String nivelFiltro = request.getParameter("nivel");

        List<Curso> cursos = cursoService.listarTodos();
        if (nivelFiltro != null && !nivelFiltro.isEmpty()) {
            cursos = cursos.stream()
                    .filter(c -> nivelFiltro.equalsIgnoreCase(c.getNivel()))
                    .collect(Collectors.toList());
        }

        response.setContentType("text/csv;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"cursos.csv\"");

        PrintWriter writer = response.getWriter();
        writer.println("id,nombre,descripcion,duracionHoras,nivel");
        for (Curso c : cursos) {
            writer.println(String.join(",",
                    String.valueOf(c.getId()),
                    csvEscape(c.getNombre()),
                    csvEscape(c.getDescripcion()),
                    String.valueOf(c.getDuracionHoras()),
                    csvEscape(c.getNivel())));
        }
        writer.flush();

        return null;
    }

    private static String csvEscape(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
