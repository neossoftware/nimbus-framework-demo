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

/**
 * DTO de error estándar para respuestas JSON de la API REST.
 */
public class ApiError {

    private int    status;
    private String error;
    private String message;
    private String path;

    public ApiError() {}

    public ApiError(int status, String error, String message, String path) {
        this.status  = status;
        this.error   = error;
        this.message = message;
        this.path    = path;
    }

    public int    getStatus()  { return status; }
    public String getError()   { return error; }
    public String getMessage() { return message; }
    public String getPath()    { return path; }

    public void setStatus(int status)       { this.status = status; }
    public void setError(String error)      { this.error = error; }
    public void setMessage(String message)  { this.message = message; }
    public void setPath(String path)        { this.path = path; }
}
