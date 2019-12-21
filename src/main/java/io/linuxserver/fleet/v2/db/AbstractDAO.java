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

package io.linuxserver.fleet.v2.db;

import io.linuxserver.fleet.core.db.DatabaseProvider;
import io.linuxserver.fleet.v2.LoggerOwner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

public class AbstractDAO implements LoggerOwner {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final DatabaseProvider databaseProvider;

    public AbstractDAO(final DatabaseProvider databaseProvider) {
        this.databaseProvider = databaseProvider;
    }

    protected final Connection getConnection() throws SQLException {
        return databaseProvider.getDatabaseConnection().getConnection();
    }

    @Override
    public final Logger getLogger() {
        return logger;
    }
}
