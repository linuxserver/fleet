/*
 * Copyright (c) 2019 LinuxServer.io
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

package io.linuxserver.fleet.core.config;

public class DatabaseConnectionProperties {

    private final String driverClass;
    private final String url;
    private final String username;
    private final String password;

    public DatabaseConnectionProperties(final String driverClass,
                                        final String url,
                                        final String username,
                                        final String password) {
        this.driverClass = driverClass;
        this.url         = url;
        this.username    = username;
        this.password    = password;
    }

    public final String getDatabaseDriverClass() {
        return driverClass;
    }

    public final String getDatabaseUrl() {
        return url;
    }

    public final String getDatabaseUsername() {
        return username;
    }

    public final String getDatabasePassword() {
        return password;
    }
}
