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

/**
 * Igual que {@link CursoController}, pero respaldado por H2 real vía
 * {@code @Qualifier("cursoServiceJdbcImpl")} en vez de la implementación en memoria —
 * camino paralelo para demostrar JdbcTemplate/NamedParameterJdbcTemplate bajo carga.
 */
@Controller
public class CursoJdbcController {

    @Autowired
    @Qualifier("cursoServiceJdbcImpl")
    private CursoService cursoService;

    private final Validator validator;

    @Autowired
    public CursoJdbcController(@Qualifier("cursoValidator") Validator validator) {
        this.validator = validator;
    }

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.setValidator(validator);
    }

    @GetMapping("/cursos-jdbc/lista.do")
    public ModelAndView lista() {
        return new ModelAndView("cursos-jdbc/lista")
                .addObject("cursos", cursoService.listarTodos());
    }

    @GetMapping("/cursos-jdbc/nuevo.do")
    public ModelAndView nuevo() {
        return new ModelAndView("cursos-jdbc/formulario")
                .addObject("curso",      new Curso())
                .addObject("titulo",     "Nuevo Curso (H2/JDBC)")
                .addObject("formAction", "/cursos-jdbc/guardar.do");
    }

    @PostMapping("/cursos-jdbc/guardar.do")
    public String guardar(@ModelAttribute("curso") @Validated Curso curso, BindingResult result,
                          ModelMap map, HttpServletRequest request) {
        if (result.hasErrors()) {
            map.addAttribute("formfail",    "true");
            map.addAttribute("errores",     result.getAllErrors());
            map.addAttribute("titulo",      "Nuevo Curso (H2/JDBC)");
            map.addAttribute("formAction",  "/cursos-jdbc/guardar.do");
            map.addAttribute("requestInfo", request.getMethod() + " " + request.getRequestURI());
            return "cursos-jdbc/formulario";
        }
        cursoService.guardar(curso);
        return "redirect:/cursos-jdbc/lista.do";
    }

    @GetMapping("/cursos-jdbc/editar/{id}.do")
    public ModelAndView editar(@PathVariable("id") int id) {
        Curso curso = cursoService.buscarPorId(id);
        return new ModelAndView("cursos-jdbc/formulario")
                .addObject("curso",      curso)
                .addObject("titulo",     "Editar Curso (H2/JDBC)")
                .addObject("formAction", "/cursos-jdbc/actualizar.do");
    }

    @PostMapping("/cursos-jdbc/actualizar.do")
    public String actualizar(@ModelAttribute("curso") @Validated Curso curso, BindingResult result,
                             ModelMap map, HttpServletRequest request) {
        if (result.hasErrors()) {
            map.addAttribute("formfail",    "true");
            map.addAttribute("errores",     result.getAllErrors());
            map.addAttribute("titulo",      "Editar Curso (H2/JDBC)");
            map.addAttribute("formAction",  "/cursos-jdbc/actualizar.do");
            map.addAttribute("requestInfo", request.getMethod() + " " + request.getRequestURI());
            return "cursos-jdbc/formulario";
        }
        cursoService.actualizar(curso);
        return "redirect:/cursos-jdbc/lista.do";
    }

    @GetMapping("/cursos-jdbc/eliminar/{id}.do")
    public String eliminar(@PathVariable("id") int id) {
        cursoService.eliminar(id);
        return "redirect:/cursos-jdbc/lista.do";
    }

    @GetMapping("/cursos-jdbc/exportar.do")
    public String exportarCsv(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String nivelFiltro = request.getParameter("nivel");

        List<Curso> cursos = cursoService.listarTodos();
        if (nivelFiltro != null && !nivelFiltro.isEmpty()) {
            cursos = cursos.stream()
                    .filter(c -> nivelFiltro.equalsIgnoreCase(c.getNivel()))
                    .collect(Collectors.toList());
        }

        response.setContentType("text/csv;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"cursos-jdbc.csv\"");

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
