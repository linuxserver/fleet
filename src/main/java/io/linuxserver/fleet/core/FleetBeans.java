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

import io.linuxserver.fleet.db.DefaultDatabaseConnection;
import io.linuxserver.fleet.db.dao.DefaultImageDAO;
import io.linuxserver.fleet.db.dao.DefaultRepositoryDAO;
import io.linuxserver.fleet.db.migration.DatabaseVersion;
import io.linuxserver.fleet.delegate.*;
import io.linuxserver.fleet.dockerhub.DockerHubV2Client;
import io.linuxserver.fleet.thread.TaskManager;
import io.linuxserver.fleet.web.WebServer;

/**
 * <p>
 * Initialises all relevant dependencies for the Fleet application
 * </p>
 */
public class FleetBeans {

    /**
     * Contains all runtime properties that get loaded into the application on start-up
     */
    private final FleetProperties   properties;

    /**
     * Facade layer which handles delegation for image management.
     */
    private final ImageDelegate     imageDelegate;

    private final RepositoryDelegate repositoryDelegate;

    private final AuthenticationDelegate authenticationDelegate;

    /**
     * Facade layer for interaction with the Docker Hub APIs.
     */
    private final DockerHubDelegate dockerHubDelegate;

    private final SynchronisationDelegate synchronisationDelegate;

    private final WebServer webServer;

    private final TaskManager taskManager;

    /**
     * Ensures the database is kept up to date.
     */
    private final DatabaseVersion   databaseVersion;

    FleetBeans() {

        properties              = new PropertiesLoader().getProperties();

        final DefaultDatabaseConnection databaseConnection = new DefaultDatabaseConnection(properties);

        databaseVersion         = new DatabaseVersion(databaseConnection);
        imageDelegate           = new ImageDelegate(new DefaultImageDAO(databaseConnection));
        repositoryDelegate      = new RepositoryDelegate(new DefaultRepositoryDAO(databaseConnection));
        dockerHubDelegate       = new DockerHubDelegate(new DockerHubV2Client(properties.getDockerHubCredentials()));
        authenticationDelegate  = new PropertiesAuthenticationDelegate(properties.getAppUsername(), properties.getAppPassword());
        webServer               = new WebServer(properties.getAppPort());
        taskManager             = new TaskManager();
        synchronisationDelegate = new SynchronisationDelegate(imageDelegate, repositoryDelegate, dockerHubDelegate);
    }

    public FleetProperties getProperties() {
        return properties;
    }

    public ImageDelegate getImageDelegate() {
        return imageDelegate;
    }

    public DockerHubDelegate getDockerHubDelegate() {
        return dockerHubDelegate;
    }

    public RepositoryDelegate getRepositoryDelegate() {
        return repositoryDelegate;
    }

    public SynchronisationDelegate getSynchronisationDelegate() {
        return synchronisationDelegate;
    }

    public AuthenticationDelegate getAuthenticationDelegate() {
        return authenticationDelegate;
    }

    public DatabaseVersion getDatabaseVersion() {
        return databaseVersion;
    }

    public WebServer getWebServer() {
        return webServer;
    }

    public TaskManager getTaskManager() {
        return taskManager;
    }
}
