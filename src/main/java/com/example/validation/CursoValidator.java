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
package com.example.validation;

import com.example.model.Curso;
import com.nimbusframework.validation.Errors;
import com.nimbusframework.validation.Validator;

/**
 * Validación custom (no basada en anotaciones) para Curso, conectada al
 * controller vía @InitBinder + WebDataBinder.setValidator(...). Los códigos
 * pasados a reject/rejectValue se resuelven contra el bean "messageSource"
 * (ResourceBundleMessageSource -> application/locallization/trust_resource.properties).
 */
public class CursoValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return Curso.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Curso curso = (Curso) target;

        if (curso.getNombre() == null || curso.getNombre().trim().isEmpty()) {
            errors.rejectValue("nombre", "curso.nombre.obligatorio");
        }

        if ("BASICO".equalsIgnoreCase(curso.getNivel()) && curso.getDuracionHoras() > 40) {
            errors.reject("curso.duracion.excedida");
        }
    }
}
