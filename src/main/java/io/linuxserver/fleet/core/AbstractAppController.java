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

package io.linuxserver.fleet.core;

import io.linuxserver.fleet.core.config.AppProperties;
import io.linuxserver.fleet.db.DefaultDatabaseConnection;
import io.linuxserver.fleet.db.migration.DatabaseVersion;
import io.linuxserver.fleet.v2.db.DefaultImageDAO;
import io.linuxserver.fleet.v2.service.RepositoryManager;
import io.linuxserver.fleet.v2.types.Image;

public abstract class AbstractAppController {

    private final AppProperties             appProperties;
    private final DefaultDatabaseConnection databaseConnection;
    private final DatabaseVersion           databaseVersion;

    private final RepositoryManager repositoryManager;

    public AbstractAppController() {

        this.appProperties      = new PropertiesLoader().getProperties();
        this.databaseConnection = new DefaultDatabaseConnection(appProperties.getDatabaseProperties());
        this.databaseVersion    = new DatabaseVersion(databaseConnection);

        this.repositoryManager = new RepositoryManager(new DefaultImageDAO(databaseConnection));

    }

    public final AppProperties getAppProperties() {
        return appProperties;
    }

    public final RepositoryManager getRepositoryManager() {
        return repositoryManager;
    }

    public final Image storeUpdatedImage(final Image updatedImage) {
        return repositoryManager.storeImage(updatedImage);
    }

    protected void run() {

        databaseVersion.migrate();
    }
}
