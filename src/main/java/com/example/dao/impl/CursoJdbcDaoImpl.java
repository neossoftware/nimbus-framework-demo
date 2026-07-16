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
package com.example.dao.impl;

import com.example.dao.CursoDao;
import com.example.model.Curso;
import com.nimbusframework.annotation.Autowired;
import com.nimbusframework.annotation.Repository;
import com.nimbusframework.jdbc.EmptyResultDataAccessException;
import com.nimbusframework.jdbc.GeneratedKeyHolder;
import com.nimbusframework.jdbc.JdbcTemplate;
import com.nimbusframework.jdbc.KeyHolder;
import com.nimbusframework.jdbc.RowMapper;
import com.nimbusframework.jdbc.namedparam.MapSqlParameterSource;
import com.nimbusframework.jdbc.namedparam.NamedParameterJdbcTemplate;
import com.nimbusframework.repository.Page;
import com.nimbusframework.repository.PageImpl;
import com.nimbusframework.repository.PageRequest;
import com.nimbusframework.repository.Pageable;
import com.nimbusframework.repository.Sort;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * DAO de Curso respaldado por H2 real. Reparte deliberadamente entre {@link JdbcTemplate}
 * (SQL simple, 0-1 parámetro posicional) y {@link NamedParameterJdbcTemplate} (INSERT/UPDATE
 * con varios campos con nombre, más el paginado con LIMIT/OFFSET nombrados), para mostrar
 * ambos estilos con uso genuino en un mismo DAO.
 *
 * Lanza {@link NoSuchElementException} con el mismo mensaje que el service en memoria
 * ({@code CursoServiceImpl}) para que ambos caminos se comporten igual ante un id inexistente.
 */
@Repository
public class CursoJdbcDaoImpl implements CursoDao {

    private static final RowMapper<Curso> CURSO_ROW_MAPPER = (rs, rowNum) -> {
        Curso curso = new Curso();
        curso.setId(rs.getInt("id"));
        curso.setNombre(rs.getString("nombre"));
        curso.setDescripcion(rs.getString("descripcion"));
        curso.setDuracionHoras(rs.getInt("duracion_horas"));
        curso.setNivel(rs.getString("nivel"));
        return curso;
    };

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public List<Curso> listarTodos() {
        return jdbcTemplate.query("SELECT * FROM cursos ORDER BY id", CURSO_ROW_MAPPER);
    }

    @Override
    public Curso buscarPorId(int id) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM cursos WHERE id = ?", CURSO_ROW_MAPPER, id);
        } catch (EmptyResultDataAccessException e) {
            throw new NoSuchElementException("Curso no encontrado con id: " + id);
        }
    }

    @Override
    public void guardar(Curso curso) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("nombre", curso.getNombre())
                .addValue("descripcion", curso.getDescripcion())
                .addValue("duracionHoras", curso.getDuracionHoras())
                .addValue("nivel", curso.getNivel());

        KeyHolder keyHolder = new GeneratedKeyHolder();
        namedParameterJdbcTemplate.update(
                "INSERT INTO cursos (nombre, descripcion, duracion_horas, nivel) "
                        + "VALUES (:nombre, :descripcion, :duracionHoras, :nivel)",
                params, keyHolder);
        curso.setId(keyHolder.getKey().intValue());
    }

    @Override
    public void actualizar(Curso curso) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", curso.getId())
                .addValue("nombre", curso.getNombre())
                .addValue("descripcion", curso.getDescripcion())
                .addValue("duracionHoras", curso.getDuracionHoras())
                .addValue("nivel", curso.getNivel());

        int rows = namedParameterJdbcTemplate.update(
                "UPDATE cursos SET nombre = :nombre, descripcion = :descripcion, "
                        + "duracion_horas = :duracionHoras, nivel = :nivel WHERE id = :id",
                params);
        if (rows == 0) {
            throw new NoSuchElementException("Curso no encontrado con id: " + curso.getId());
        }
    }

    @Override
    public void eliminar(int id) {
        jdbcTemplate.update("DELETE FROM cursos WHERE id = ?", id);
    }

    @Override
    public long contar() {
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM cursos", Long.class);
    }

    @Override
    public Page<Curso> listarPaginado(int pageNum, int pageSize, String sortField, String sortDirection) {
        Sort sort = "DESC".equalsIgnoreCase(sortDirection)
                ? Sort.by(sortField).descending()
                : Sort.by(sortField).ascending();
        Pageable pageable = PageRequest.of(pageNum, pageSize, sort);

        String orderBy = columnFor(sort.getField()) + " " + (sort.getDirection() == Sort.Direction.DESC ? "DESC" : "ASC");

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("limit", pageable.getPageSize())
                .addValue("offset", pageable.getOffset());
        List<Curso> content = namedParameterJdbcTemplate.query(
                "SELECT * FROM cursos ORDER BY " + orderBy + " LIMIT :limit OFFSET :offset",
                params, CURSO_ROW_MAPPER);

        return new PageImpl<>(content, pageable, contar());
    }

    /** Whitelist de columnas ordenables — nunca se interpola sortField crudo en el SQL. */
    private static String columnFor(String sortField) {
        if (sortField == null) return "id";
        switch (sortField) {
            case "nombre":        return "nombre";
            case "descripcion":   return "descripcion";
            case "duracionHoras": return "duracion_horas";
            case "nivel":         return "nivel";
            default:              return "id";
        }
    }
}
