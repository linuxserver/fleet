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

import io.linuxserver.fleet.dockerhub.DockerHubCredentials;

import java.util.Properties;

public class FleetProperties {

    private Properties properties;

    FleetProperties(Properties properties) {
        this.properties = properties;
    }

    public String getDatabaseDriverClassName() {
        return properties.getProperty("fleet.database.driver");
    }

    public String getDatabaseUrl() {
        return properties.getProperty("fleet.database.url");
    }

    public String getDatabaseUsername() {
        return properties.getProperty("fleet.database.username");
    }

    public String getDatabasePassword() {
        return properties.getProperty("fleet.database.password");
    }

    public String getAppUsername() {
        return properties.getProperty("fleet.admin.username");
    }

    public String getAppPassword() {
        return properties.getProperty("fleet.admin.password");
    }

    public int getAppPort() {
        return Integer.parseInt(properties.getProperty("fleet.app.port"));
    }

    public int getRefreshIntervalInMinutes() {
        return Integer.parseInt(properties.getProperty("fleet.refresh.interval"));
    }

    public DockerHubCredentials getDockerHubCredentials() {

        String username = properties.getProperty("fleet.dockerhub.username");
        String password = properties.getProperty("fleet.dockerhub.password");

        return new DockerHubCredentials(username, password);
    }
}
