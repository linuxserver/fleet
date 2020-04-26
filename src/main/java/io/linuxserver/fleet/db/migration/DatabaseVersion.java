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

package io.linuxserver.fleet.db.migration;

import io.linuxserver.fleet.core.FleetRuntime;
import io.linuxserver.fleet.core.db.DatabaseConnection;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.FlywayException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * Manages versioning of the database to ensure it is kept up-to-date
 * </p>
 */
public class DatabaseVersion {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseVersion.class);

    private final Flyway flyway;

    public DatabaseVersion(final DatabaseConnection databaseConnection) {

        flyway = Flyway.configure().dataSource(databaseConnection.getDataSource()).load();
        migrate();
    }

    /**
     * <p>
     * Runs the migration process which runs any necessary scripts to get the database configured.
     * </p>
     */
    public void migrate() {

        try {

            if (FleetRuntime.NUKE_DATABASE) {
                flyway.clean();
            }

            flyway.migrate();

        } catch (FlywayException e) {

            LOGGER.error(e.getMessage());
            throw new RuntimeException("Unable to start application because the database has gone out of sync.", e);
        }
    }
}
