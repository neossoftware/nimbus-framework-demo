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
package com.example.exception;

import com.nimbusframework.annotation.ControllerAdvice;
import com.nimbusframework.annotation.ExceptionHandler;
import com.nimbusframework.annotation.ResponseBody;
import com.nimbusframework.validation.ValidationException;
import com.nimbusframework.web.HttpStatus;
import com.nimbusframework.web.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * Manejador global de excepciones para toda la aplicación.
 *
 * Prioridad:
 *   1. @ExceptionHandler en el mismo @Controller que lanzó la excepción
 *   2. Handlers de esta clase (@ControllerAdvice), tipo más específico primero
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /** Validación @Valid fallida — 422 Unprocessable Entity */
    @ExceptionHandler(ValidationException.class)
    @ResponseBody
    public ResponseEntity<ApiError> handleValidation(ValidationException ex,
                                                      HttpServletRequest req) {
        String violations = ex.getBindingResult().getAllErrors().stream()
            .map(Object::toString)
            .collect(Collectors.joining(" | "));

        ApiError err = new ApiError(
            HttpStatus.UNPROCESSABLE_ENTITY.value(),
            "Validation Failed",
            violations,
            req.getRequestURI()
        );
        return new ResponseEntity<>(err, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    /** Recurso no encontrado — 404 */
    @ExceptionHandler(NoSuchElementException.class)
    @ResponseBody
    public ResponseEntity<ApiError> handleNotFound(NoSuchElementException ex,
                                                    HttpServletRequest req) {
        ApiError err = new ApiError(
            HttpStatus.NOT_FOUND.value(),
            HttpStatus.NOT_FOUND.getReasonPhrase(),
            ex.getMessage() != null ? ex.getMessage() : "Recurso no encontrado",
            req.getRequestURI()
        );
        return new ResponseEntity<>(err, HttpStatus.NOT_FOUND);
    }

    /** Datos de entrada inválidos — 400 */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseBody
    public ResponseEntity<ApiError> handleBadRequest(IllegalArgumentException ex,
                                                      HttpServletRequest req) {
        ApiError err = new ApiError(
            HttpStatus.BAD_REQUEST.value(),
            HttpStatus.BAD_REQUEST.getReasonPhrase(),
            ex.getMessage(),
            req.getRequestURI()
        );
        return new ResponseEntity<>(err, HttpStatus.BAD_REQUEST);
    }

    /** Cualquier otra excepción no manejada — 500 */
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<ApiError> handleGeneral(Exception ex, HttpServletRequest req) {
        ApiError err = new ApiError(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
            ex.getMessage() != null ? ex.getMessage() : "Error interno del servidor",
            req.getRequestURI()
        );
        return new ResponseEntity<>(err, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
