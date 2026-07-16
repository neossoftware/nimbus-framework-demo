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
package com.example.service.impl;

import com.example.dao.CursoDao;
import com.example.model.Curso;
import com.example.service.CursoService;
import com.nimbusframework.annotation.Autowired;
import com.nimbusframework.annotation.Service;
import com.nimbusframework.repository.Page;

import java.util.List;

/**
 * Implementación respaldada por H2 real (vía {@link CursoDao}, JdbcTemplate/
 * NamedParameterJdbcTemplate) — camino paralelo a {@code CursoServiceImpl} (en memoria).
 * Seleccionada explícitamente con {@code @Qualifier("cursoServiceJdbcImpl")} en los
 * controllers de la variante "-jdbc" (el nombre del bean es siempre el derivado del
 * nombre de la clase — {@code XmlApplicationContext} no lee {@code value()} de
 * {@code @Service} para nombrar el bean), igual patrón que Email/SmsNotificationService.
 */
@Service
public class CursoServiceJdbcImpl implements CursoService {

    @Autowired
    private CursoDao cursoDao;

    @Override
    public List<Curso> listarTodos() {
        return cursoDao.listarTodos();
    }

    @Override
    public Curso buscarPorId(int id) {
        return cursoDao.buscarPorId(id);
    }

    @Override
    public void guardar(Curso curso) {
        cursoDao.guardar(curso);
    }

    @Override
    public void actualizar(Curso curso) {
        cursoDao.actualizar(curso);
    }

    @Override
    public void eliminar(int id) {
        cursoDao.eliminar(id);
    }

    @Override
    public long contar() {
        return cursoDao.contar();
    }

    @Override
    public Page<Curso> listarPaginado(int pageNum, int pageSize, String sortField, String sortDirection) {
        return cursoDao.listarPaginado(pageNum, pageSize, sortField, sortDirection);
    }
}
