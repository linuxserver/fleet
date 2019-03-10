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

package io.linuxserver.fleet.db;

import com.zaxxer.hikari.HikariDataSource;
import io.linuxserver.fleet.core.FleetProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public abstract class PoolingDatabaseConnection {

    private static final Logger LOGGER = LoggerFactory.getLogger(PoolingDatabaseConnection.class);

    private final HikariDataSource dataSource;

    PoolingDatabaseConnection(FleetProperties properties) {

        dataSource = new HikariDataSource();
        dataSource.setDriverClassName(properties.getDatabaseDriverClassName());
        dataSource.setJdbcUrl(properties.getDatabaseUrl());
        dataSource.setUsername(properties.getDatabaseUsername());
        dataSource.setPassword(properties.getDatabasePassword());

        LOGGER.info("DataSource established: " + dataSource.getJdbcUrl());
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}
