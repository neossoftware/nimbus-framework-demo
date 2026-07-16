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
package com.example.model;

import com.nimbusframework.annotation.Max;
import com.nimbusframework.annotation.Min;
import com.nimbusframework.annotation.NotBlank;
import com.nimbusframework.annotation.Size;

public class Curso {

    private Integer id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
    private String nombre;

    @Size(max = 500, message = "La descripción no puede superar 500 caracteres")
    private String descripcion;

    @Min(value = 1, message = "La duración mínima es 1 hora")
    @Max(value = 200, message = "La duración máxima es 200 horas")
    private int duracionHoras;

    @NotBlank(message = "El nivel es obligatorio (BASICO, INTERMEDIO, AVANZADO)")
    private String nivel;

    public Curso() {}

    public Curso(String nombre, String descripcion, int duracionHoras, String nivel) {
        this.nombre        = nombre;
        this.descripcion   = descripcion;
        this.duracionHoras = duracionHoras;
        this.nivel         = nivel;
    }

    public Integer getId()                          { return id; }
    public void    setId(Integer id)                { this.id = id; }

    public String getNombre()                       { return nombre; }
    public void   setNombre(String nombre)          { this.nombre = nombre; }

    public String getDescripcion()                  { return descripcion; }
    public void   setDescripcion(String d)          { this.descripcion = d; }

    public int  getDuracionHoras()                  { return duracionHoras; }
    public void setDuracionHoras(int duracionHoras) { this.duracionHoras = duracionHoras; }

    public String getNivel()                        { return nivel; }
    public void   setNivel(String nivel)            { this.nivel = nivel; }
}
