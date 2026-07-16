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
package com.example.config;

// Clase SIN anotaciones (@Component/@Service) a propósito: se configura
// enteramente desde XML (application/config/beans-config.xml) con <property>.
public class AppInfo {

    private String nombre;
    private String version;

    public void setNombre(String nombre)   { this.nombre = nombre; }
    public void setVersion(String version) { this.version = version; }

    public String getNombre()  { return nombre; }
    public String getVersion() { return version; }
}
