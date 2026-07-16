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

import com.example.exception.ApiError;
import com.example.model.Curso;
import com.example.service.CursoService;
import com.nimbusframework.annotation.Autowired;
import com.nimbusframework.annotation.ExceptionHandler;
import com.nimbusframework.annotation.PathVariable;
import com.nimbusframework.annotation.RequestBody;
import com.nimbusframework.annotation.RequestMapping;
import com.nimbusframework.annotation.ResponseBody;
import com.nimbusframework.annotation.RestController;
import com.nimbusframework.annotation.Valid;
import com.nimbusframework.web.HttpStatus;
import com.nimbusframework.web.RequestMethod;
import com.nimbusframework.web.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * API REST de Cursos.
 *
 * Base path: /api/cursos  (configurado en web.xml: /api/*)
 *
 * GET    /api/cursos          → lista todos
 * GET    /api/cursos/{id}     → busca por id
 * POST   /api/cursos          → crea nuevo
 * PUT    /api/cursos/{id}     → actualiza
 * DELETE /api/cursos/{id}     → elimina
 */
@RestController
@RequestMapping("/api/cursos")
public class CursoRestController {

    @Autowired
    private CursoService cursoService;

    @RequestMapping(value = "", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<List<Curso>> listar() {
        List<Curso> cursos = cursoService.listarTodos();
        return ResponseEntity.ok(cursos);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<Curso> buscar(@PathVariable("id") int id) {
        // NoSuchElementException si no existe → capturada por GlobalExceptionHandler → 404
        Curso curso = cursoService.buscarPorId(id);
        return ResponseEntity.ok(curso);
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

    // -----------------------------------------------------------------------
    // Manejo local de excepciones — prioridad sobre @ControllerAdvice global
    // -----------------------------------------------------------------------

    /**
     * Handler LOCAL para este controller.
     * Intercepta NoSuchElementException ANTES que GlobalExceptionHandler.handleNotFound().
     * Devuelve un mensaje específico del dominio "Cursos" con una pista de navegación.
     */
    @ExceptionHandler(NoSuchElementException.class)
    @ResponseBody
    public ResponseEntity<ApiError> handleCursoNoEncontrado(NoSuchElementException ex,
                                                             HttpServletRequest req) {
        ApiError error = new ApiError(
            HttpStatus.NOT_FOUND.value(),
            "Curso No Encontrado",
            ex.getMessage() + " — consulta GET /api/cursos para ver IDs disponibles",
            req.getRequestURI()
        );
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }
}
