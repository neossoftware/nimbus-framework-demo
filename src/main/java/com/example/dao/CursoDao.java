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
package com.example.dao;

import com.example.model.Curso;
import com.nimbusframework.repository.Page;

import java.util.List;

/**
 * Acceso a datos de Curso respaldado por SQL real (H2), vía JdbcTemplate/
 * NamedParameterJdbcTemplate — implementación en {@link com.example.dao.impl.CursoJdbcDaoImpl}.
 * Mismos métodos que {@link com.example.service.CursoService} (la capa de service es fina,
 * delega directamente acá).
 */
public interface CursoDao {
    List<Curso> listarTodos();
    Curso       buscarPorId(int id);
    void        guardar(Curso curso);
    void        actualizar(Curso curso);
    void        eliminar(int id);
    long        contar();

    Page<Curso> listarPaginado(int pageNum, int pageSize, String sortField, String sortDirection);
}
