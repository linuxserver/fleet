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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.Properties;

/**
 * <p>
 * Loads in the application properties from disk. Properties must be provided as JVM arguments
 * as these will be used to create a connection to the underlying database, and any other
 * required connections.
 * </p>
 *
 * @author Josh Stark
 */
class PropertiesLoader extends BaseRuntimeLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(PropertiesLoader.class);

    private final AppProperties properties;

    PropertiesLoader() {

        super();

        try {

            createConfigFileIfNotProvided();

            Properties properties = new Properties();
            properties.load(new FileInputStream(FleetRuntime.CONFIG_BASE + "/fleet.properties"));
            properties.load(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("version.properties")));
            properties.setProperty("fleet.static.dirname", "fleet_static");

            this.properties = new AppProperties(properties);

            printProperties();

            if (!createStaticFileDirectory() || !createLogsDirectory()) {
                throw new RuntimeException("Unable to create config sub directories for Fleet. Check permissions");
            }

        } catch (IOException e) {

            LOGGER.error("Unable to load config! Check JVM args and config directory", e);
            throw new RuntimeException(e);
        }
    }

    private void createConfigFileIfNotProvided() {

        try {

            File configFile = new File(FleetRuntime.CONFIG_BASE + "/fleet.properties");
            if (!configFile.exists()) {

                if (!configFile.createNewFile()) {
                    throw new RuntimeException("Unable to create base config for fleet.");
                }
            }

        } catch (IOException e) {
            throw new RuntimeException("Unable to create base config for fleet.", e);
        }
    }

    private boolean createStaticFileDirectory() {

        File staticFilesDir = new File(properties.getStaticFilesPath().toString());

        if (staticFilesDir.exists()) {
            return true;
        }

        return staticFilesDir.mkdir();
    }

    private boolean createLogsDirectory() {

        File logsDirectory = new File(FleetRuntime.CONFIG_BASE + "/logs");

        if (logsDirectory.exists()) {
            return true;
        }

        return logsDirectory.mkdir();
    }

    /**
     * <p>
     * The loaded properties, with accessible fields.
     * </p>
     *
     * @return
     *      All application properties.
     */
    AppProperties getProperties() {
        return properties;
    }

    /**
     * <p>
     * Prints out the loaded properties to the log. Useful when the application loads up for the first time.
     * </p>
     */
    private void printProperties() {

        LOGGER.info("fleet.app.port           : " + properties.getAppPort());
        LOGGER.info("fleet.database.url       : " + properties.getDatabaseProperties().getDatabaseUrl());
        LOGGER.info("fleet.database.username  : " + properties.getDatabaseProperties().getDatabaseUsername());
        LOGGER.info("fleet.database.password  : " + (showPasswords() ? properties.getDatabaseProperties().getDatabasePassword() : "***"));
        LOGGER.info("app.version              : " + getProperties().getVersionProperties());
    }

    /**
     * <p>
     * Ideally in a production environment you don't want to log passwords or keys.
     * </p>
     *
     * @return
     *      true if passwords can be logged to the terminal.
     */
    private boolean showPasswords() {
        return FleetRuntime.SHOW_PASSWORDS;
    }
}
