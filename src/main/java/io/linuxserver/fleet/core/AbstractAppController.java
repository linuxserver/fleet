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
import io.linuxserver.fleet.core.db.DatabaseProvider;
import io.linuxserver.fleet.core.db.DefaultDatabaseProvider;
import io.linuxserver.fleet.db.DefaultDatabaseConnection;

public abstract class AbstractAppController {

    private final AppProperties    appProperties;
    private final DatabaseProvider databaseProvider;

    public AbstractAppController() {

        this.appProperties      = new PropertiesLoader().getProperties();

        this.databaseProvider   = new DefaultDatabaseProvider(new DefaultDatabaseConnection(appProperties.getDatabaseProperties()));

    }

    public final DatabaseProvider getDatabaseProvider() {
        return databaseProvider;
    }

    public final AppProperties getAppProperties() {
        return appProperties;
    }

    protected void run() {

    }
}
