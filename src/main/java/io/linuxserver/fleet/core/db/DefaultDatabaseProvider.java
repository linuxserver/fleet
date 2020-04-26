/*
 * Copyright (c)  2019 LinuxServer.io
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

package io.linuxserver.fleet.core.db;

import io.linuxserver.fleet.db.migration.DatabaseVersion;

public class DefaultDatabaseProvider implements DatabaseProvider {

    private final DatabaseConnection databaseConnection;
    private final DatabaseVersion    databaseVersion;

    public DefaultDatabaseProvider(final DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
        this.databaseVersion    = new DatabaseVersion(databaseConnection);
    }

    @Override
    public DatabaseConnection getDatabaseConnection() {
        return databaseConnection;
    }

    @Override
    public DatabaseVersion getVersionHandler() {
        return databaseVersion;
    }
}
