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
package com.example.component;

import com.nimbusframework.annotation.BeanScope;
import com.nimbusframework.annotation.Component;
import com.nimbusframework.annotation.Scope;

import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

/**
 * Demo de @Scope("prototype"):
 *   - Cada vez que el contenedor inyecta este bean o se llama getBean(RequestTracker.class)
 *     se crea una instancia NUEVA con su propio instanceId.
 *   - Dos singletons que inyecten RequestTracker obtienen instancias distintas.
 *   - Contrasta con el comportamiento singleton (misma instancia en todos los sitios).
 */
@Component
@Scope(BeanScope.PROTOTYPE)
public class RequestTracker {

    private static final Logger log = Logger.getLogger(RequestTracker.class.getName());
    private static final AtomicLong COUNTER = new AtomicLong(0);

    private final long instanceId;
    private long requestCount = 0;

    public RequestTracker() {
        this.instanceId = COUNTER.incrementAndGet();
        log.info("RequestTracker CREADO → instanceId=" + instanceId
            + "  @" + Integer.toHexString(System.identityHashCode(this)));
    }

    public long getInstanceId() { return instanceId; }

    public long incrementAndGet() { return ++requestCount; }

    public long getRequestCount() { return requestCount; }

    @Override
    public String toString() {
        return "RequestTracker{instanceId=" + instanceId
            + ", requestCount=" + requestCount
            + ", identity=@" + Integer.toHexString(System.identityHashCode(this)) + "}";
    }
}
