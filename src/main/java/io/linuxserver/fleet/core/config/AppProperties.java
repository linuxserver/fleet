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

import io.linuxserver.fleet.core.FleetRuntime;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class AppProperties {

    private Properties properties;

    public AppProperties(final Properties properties) {
        this.properties = properties;
    }

    public DatabaseConnectionProperties getDatabaseProperties() {

        return new DatabaseConnectionProperties(getDatabaseDriverClassName(),
                                                getDatabaseUrl(),
                                                getDatabaseUsername(),
                                                getDatabasePassword());
    }

    public final VersionProperties getVersionProperties() {

        return new VersionProperties(getStringProperty("app.version"),
                                     getStringProperty("app.build.user"),
                                     getStringProperty("app.build.date"),
                                     getStringProperty("app.build.os"));
    }

    private String getDatabaseDriverClassName() {
        return getStringProperty("fleet.database.driver");
    }

    private String getDatabaseUrl() {
        return getStringProperty("fleet.database.url");
    }

    private String getDatabaseUsername() {
        return getStringProperty("fleet.database.username");
    }

    private String getDatabasePassword() {
        return getStringProperty("fleet.database.password");
    }

    public final Path getStaticFilesPath() {
        return Paths.get(FleetRuntime.CONFIG_BASE, getStringProperty("fleet.static.dirname")).toAbsolutePath();
    }

    public String getAppSecret() {

        String secret = getStringProperty("fleet.admin.secret");
        return null == secret ? "" : secret;
    }

    public int getAppPort() {
        return Integer.parseInt(getStringProperty("fleet.app.port"));
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
                property = System.getenv(propertyKey.replace(".", "_"));
            }
        }

        return property;
    }
}
