/*
 * Copyright (c)  2021 LinuxServer.io
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package io.linuxserver.fleet.v2.types.api.external.templates;

public class ApiPortTemplate {

    private final int    port;
    private final String protocol;
    private final String description;

    public ApiPortTemplate(final int port, final String protocol, final String description) {
        this.port = port;
        this.protocol = protocol;
        this.description = description;
    }

    public int getPort() {
        return port;
    }

    public String getProtocol() {
        return protocol;
    }

    public String getDescription() {
        return description;
    }
}
