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
package com.example.service;

import com.example.model.Curso;
import com.nimbusframework.repository.Page;
import com.nimbusframework.repository.Sort;

import java.util.List;

public interface CursoService {
    List<Curso> listarTodos();
    Curso       buscarPorId(int id);
    void        guardar(Curso curso);
    void        actualizar(Curso curso);
    void        eliminar(int id);
    long        contar();

    /** Retorna una página de cursos ordenada por el campo y dirección indicados. */
    Page<Curso> listarPaginado(int pageNum, int pageSize, String sortField, String sortDirection);
}
