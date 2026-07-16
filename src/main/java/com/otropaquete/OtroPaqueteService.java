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
package com.otropaquete;

import com.nimbusframework.annotation.Service;

// Paquete deliberadamente FUERA de com.example (que ya escanea el
// component-scan raíz de framework-config.xml). Solo se detecta si el
// <component-scan base-package="com.otropaquete"/> dentro de
// beans-config.xml (cargado vía <import>) realmente se ejecuta.
@Service
public class OtroPaqueteService {
    public String mensaje() {
        return "OtroPaqueteService activo (escaneado desde beans-config.xml, no desde framework-config.xml)";
    }
}
