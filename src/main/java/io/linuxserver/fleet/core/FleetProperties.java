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
        return getStringProperty("fleet.database.driver");
    }

    public String getDatabaseUrl() {
        return getStringProperty("fleet.database.url");
    }

    public String getDatabaseUsername() {
        return getStringProperty("fleet.database.username");
    }

    public String getDatabasePassword() {
        return getStringProperty("fleet.database.password");
    }

    public String getAppUsername() {
        return getStringProperty("fleet.admin.username");
    }

    public String getAppPassword() {
        return getStringProperty("fleet.admin.password");
    }

    public int getAppPort() {
        return Integer.parseInt(getStringProperty("fleet.app.port"));
    }

    public int getRefreshIntervalInMinutes() {
        return Integer.parseInt(getStringProperty("fleet.refresh.interval"));
    }

    public DockerHubCredentials getDockerHubCredentials() {

        String username = getStringProperty("fleet.dockerhub.username");
        String password = getStringProperty("fleet.dockerhub.password");

        return new DockerHubCredentials(username, password);
    }

    /**
     * <p>
     * Obtains the property value from three separate sources: first from the config file. If not present, it will look
     * at the JVM runtime. If that is not present, it will finally check the system environment.
     * </p>
     */
    private String getStringProperty(String propertyKey) {

        String property = properties.getProperty(propertyKey);
        if (null == property) {

            property = System.getProperty(propertyKey);
            if (null == property) {
                property = System.getenv(propertyKey);
            }
        }

        return property;
    }
}
