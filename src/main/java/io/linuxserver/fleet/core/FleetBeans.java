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

import io.linuxserver.fleet.auth.authenticator.AuthenticatorFactory;
import io.linuxserver.fleet.auth.security.PKCS5S2PasswordEncoder;
import io.linuxserver.fleet.auth.security.PasswordEncoder;
import io.linuxserver.fleet.db.DefaultDatabaseConnection;
import io.linuxserver.fleet.db.dao.DefaultImageDAO;
import io.linuxserver.fleet.db.dao.DefaultRepositoryDAO;
import io.linuxserver.fleet.db.dao.DefaultUserDAO;
import io.linuxserver.fleet.db.migration.DatabaseVersion;
import io.linuxserver.fleet.delegate.*;
import io.linuxserver.fleet.dockerhub.DockerHubV2Client;
import io.linuxserver.fleet.thread.TaskManager;

/**
 * <p>
 * Initialises all relevant dependencies for the Fleet application
 * </p>
 */
public class FleetBeans {

    private final FleetProperties           properties;
    private final ImageDelegate             imageDelegate;
    private final RepositoryDelegate        repositoryDelegate;
    private final AuthenticationDelegate    authenticationDelegate;
    private final DockerHubDelegate         dockerHubDelegate;
    private final SynchronisationDelegate   synchronisationDelegate;
    private final TaskManager               taskManager;
    private final TaskDelegate              taskDelegate;
    private final UserDelegate              userDelegate;
    private final PasswordEncoder           passwordEncoder;

    /**
     * Ensures the database is kept up to date.
     */
    private final DatabaseVersion   databaseVersion;

    FleetBeans() {

        properties              = new PropertiesLoader().getProperties();

        final DefaultDatabaseConnection databaseConnection = new DefaultDatabaseConnection(properties);

        passwordEncoder         = new PKCS5S2PasswordEncoder(properties.getAppSecret());
        databaseVersion         = new DatabaseVersion(databaseConnection);
        imageDelegate           = new ImageDelegate(new DefaultImageDAO(databaseConnection));
        repositoryDelegate      = new RepositoryDelegate(new DefaultRepositoryDAO(databaseConnection));
        dockerHubDelegate       = new DockerHubDelegate(new DockerHubV2Client(properties.getDockerHubCredentials()));
        taskManager             = new TaskManager();
        synchronisationDelegate = new SynchronisationDelegate(imageDelegate, repositoryDelegate, dockerHubDelegate);
        userDelegate            = new UserDelegate(passwordEncoder, new DefaultUserDAO(databaseConnection));
        taskDelegate            = new TaskDelegate(this);
        authenticationDelegate  = new DefaultAuthenticationDelegate(AuthenticatorFactory.getAuthenticator(this));
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

    public TaskManager getTaskManager() {
        return taskManager;
    }

    public TaskDelegate getTaskDelegate() {
        return taskDelegate;
    }

    public UserDelegate getUserDelegate() {
        return userDelegate;
    }

    public PasswordEncoder getPasswordEncoder() {
        return passwordEncoder;
    }
}
