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

import com.example.model.Curso;
import com.example.service.CursoService;
import com.nimbusframework.annotation.Service;
import com.nimbusframework.repository.Page;
import com.nimbusframework.repository.PageImpl;
import com.nimbusframework.repository.Pageable;
import com.nimbusframework.repository.PageRequest;
import com.nimbusframework.repository.Sort;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Implementación en memoria — sin base de datos. Los cursos viven en un Map
 * mientras el servidor está arriba; se reinician al redeployar/reiniciar.
 * Así el demo levanta sin necesidad de Postgres.
 */
@Service
public class CursoServiceImpl implements CursoService {

    private final Map<Integer, Curso> cursos = new ConcurrentHashMap<>();
    private final AtomicInteger       nextId = new AtomicInteger(0);

    public CursoServiceImpl() {
        guardar(new Curso("Java Básico", "Fundamentos del lenguaje Java: sintaxis, POO, colecciones", 40, "BASICO"));
        guardar(new Curso("Spring Boot", "Desarrollo de APIs REST con Spring Boot", 60, "INTERMEDIO"));
        guardar(new Curso("Arquitectura de Microservicios", "Diseño y despliegue de microservicios", 80, "AVANZADO"));
        guardar(new Curso("SQL para Desarrolladores", "Consultas, joins e índices", 30, "BASICO"));
    }

    @Override
    public List<Curso> listarTodos() {
        return new ArrayList<>(cursos.values());
    }

    @Override
    public Curso buscarPorId(int id) {
        Curso curso = cursos.get(id);
        if (curso == null) {
            throw new NoSuchElementException("Curso no encontrado con id: " + id);
        }
        return curso;
    }

    @Override
    public void guardar(Curso curso) {
        curso.setId(nextId.incrementAndGet());
        cursos.put(curso.getId(), curso);
    }

    @Override
    public void actualizar(Curso curso) {
        if (curso.getId() == null || !cursos.containsKey(curso.getId())) {
            throw new NoSuchElementException("Curso no encontrado con id: " + curso.getId());
        }
        cursos.put(curso.getId(), curso);
    }

    @Override
    public void eliminar(int id) {
        cursos.remove(id);
    }

    @Override
    public long contar() {
        return cursos.size();
    }

    @Override
    public Page<Curso> listarPaginado(int pageNum, int pageSize,
                                      String sortField, String sortDirection) {
        Sort sort = Sort.Direction.DESC.name().equalsIgnoreCase(sortDirection)
                ? Sort.by(sortField).descending()
                : Sort.by(sortField).ascending();

        List<Curso> todos = listarTodos();
        if (sort.isSorted()) {
            todos.sort(comparatorFor(sort));
        }

        Pageable    pageable = PageRequest.of(pageNum, pageSize, sort);
        int         from     = Math.min(pageable.getOffset(), todos.size());
        int         to       = Math.min(from + pageSize, todos.size());
        List<Curso> content  = todos.subList(from, to);

        return new PageImpl<>(content, pageable, todos.size());
    }

    private Comparator<Curso> comparatorFor(Sort sort) {
        Comparator<Curso> comparator;
        switch (sort.getField()) {
            case "nombre":
                comparator = Comparator.comparing(Curso::getNombre, String.CASE_INSENSITIVE_ORDER);
                break;
            case "descripcion":
                comparator = Comparator.comparing(Curso::getDescripcion,
                        Comparator.nullsFirst(String.CASE_INSENSITIVE_ORDER));
                break;
            case "duracionHoras":
                comparator = Comparator.comparingInt(Curso::getDuracionHoras);
                break;
            case "nivel":
                comparator = Comparator.comparing(Curso::getNivel, String.CASE_INSENSITIVE_ORDER);
                break;
            default:
                comparator = Comparator.comparing(Curso::getId);
                break;
        }
        return sort.getDirection() == Sort.Direction.DESC ? comparator.reversed() : comparator;
    }
}
