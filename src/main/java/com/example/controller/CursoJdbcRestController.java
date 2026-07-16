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
import com.nimbusframework.annotation.PathVariable;
import com.nimbusframework.annotation.Qualifier;
import com.nimbusframework.annotation.RequestBody;
import com.nimbusframework.annotation.RequestMapping;
import com.nimbusframework.annotation.RestController;
import com.nimbusframework.annotation.Valid;
import com.nimbusframework.web.HttpStatus;
import com.nimbusframework.web.RequestMethod;
import com.nimbusframework.web.ResponseEntity;

import java.util.List;

/**
 * Igual que {@link CursoRestController}, pero respaldado por H2 real vía
 * {@code @Qualifier("cursoServiceJdbcImpl")}. Sin handler local de
 * {@code NoSuchElementException}: se apoya en {@code GlobalExceptionHandler}
 * (@ControllerAdvice), que ya cubre ese caso para toda la app.
 *
 * Base path: /api/cursos-jdbc
 */
@RestController
@RequestMapping("/api/cursos-jdbc")
public class CursoJdbcRestController {

    @Autowired
    @Qualifier("cursoServiceJdbcImpl")
    private CursoService cursoService;

    @RequestMapping(value = "", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<List<Curso>> listar() {
        return ResponseEntity.ok(cursoService.listarTodos());
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<Curso> buscar(@PathVariable("id") int id) {
        return ResponseEntity.ok(cursoService.buscarPorId(id));
    }

    @RequestMapping(value = "", method = RequestMethod.POST,
                    produces = "application/json", consumes = "application/json")
    public ResponseEntity<Curso> guardar(@Valid @RequestBody Curso curso) {
        cursoService.guardar(curso);
        return new ResponseEntity<>(curso, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT,
                    produces = "application/json", consumes = "application/json")
    public ResponseEntity<Curso> actualizar(@PathVariable("id") int id,
                                             @Valid @RequestBody Curso curso) {
        curso.setId(id);
        cursoService.actualizar(curso);
        return ResponseEntity.ok(curso);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> eliminar(@PathVariable("id") int id) {
        cursoService.eliminar(id);
        return ResponseEntity.noContent();
    }
}
