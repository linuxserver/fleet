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

import io.linuxserver.fleet.auth.AuthenticationResult;
import io.linuxserver.fleet.core.config.WebConfiguration;
import io.linuxserver.fleet.v2.client.docker.DockerApiClient;
import io.linuxserver.fleet.v2.client.docker.dockerhub.DockerHubApiClient;
import io.linuxserver.fleet.v2.client.docker.dockerhub.DockerHubAuthenticator;
import io.linuxserver.fleet.v2.client.docker.dockerhub.IDockerHubAuthenticator;
import io.linuxserver.fleet.v2.client.docker.dockerhub.NoOpDockerHubAuthenticator;
import io.linuxserver.fleet.v2.client.docker.queue.DockerApiDelegate;
import io.linuxserver.fleet.v2.client.rest.RestClient;
import io.linuxserver.fleet.v2.db.DefaultImageDAO;
import io.linuxserver.fleet.v2.db.DefaultScheduleDAO;
import io.linuxserver.fleet.v2.db.DefaultUserDAO;
import io.linuxserver.fleet.v2.file.FileManager;
import io.linuxserver.fleet.v2.key.ImageKey;
import io.linuxserver.fleet.v2.service.ImageService;
import io.linuxserver.fleet.v2.service.ScheduleService;
import io.linuxserver.fleet.v2.service.SynchronisationService;
import io.linuxserver.fleet.v2.service.UserService;
import io.linuxserver.fleet.v2.types.Image;
import io.linuxserver.fleet.v2.types.Repository;
import io.linuxserver.fleet.v2.types.internal.RepositoryOutlineRequest;
import io.linuxserver.fleet.v2.web.WebRouteController;

/**
 * <p>
 * Primary entry point for the application. All contexts and resources are loaded
 * through this class.
 * </p>
 */
public class FleetAppController extends AbstractAppController implements ServiceProvider {

    private final DockerApiDelegate      dockerApiDelegate;
    private final ImageService           imageService;
    private final ScheduleService        scheduleService;
    private final SynchronisationService syncService;
    private final UserService            userService;
    private final FileManager            fileManager;

    public FleetAppController() {

        fileManager       = new FileManager(this);
        imageService      = new ImageService(this, new DefaultImageDAO(getDatabaseProvider()));
        scheduleService   = new ScheduleService(this, new DefaultScheduleDAO(getDatabaseProvider()));
        dockerApiDelegate = new DockerApiDelegate(this, configureDockerApiClient());
        syncService       = new SynchronisationService(this);
        userService       = new UserService(this, new DefaultUserDAO(getDatabaseProvider()));
    }

    private static FleetAppController instance;

    public static FleetAppController instance() {

        if (null == instance) {

            synchronized (FleetAppController.class) {

                if (null == instance) {
                    instance = new FleetAppController();
                }
            }
        }

        return instance;
    }

    @Override
    protected final void run() {
        super.run();
        configureWeb();
        scheduleService.initialiseSchedules();
    }

    public final WebConfiguration getWebConfiguration() {
        return new WebConfiguration(getAppProperties());
    }

    private void configureWeb() {
        new WebRouteController(this);
    }

    public final void handleException(final Exception e) {

    }

    public final boolean synchroniseImage(final ImageKey imageKey) {
        return syncService.synchroniseImage(imageKey);
    }

    public final void synchroniseRepository(final Repository repository) {
        syncService.synchroniseCachedRepository(repository);
    }

    public final DockerApiDelegate getConfiguredDockerDelegate() {
        return dockerApiDelegate;
    }

    public final ImageService getImageService() {
        return imageService;
    }

    public final Image storeUpdatedImage(final Image updatedImage) {
        return imageService.storeImage(updatedImage);
    }

    @Override
    public ScheduleService getScheduleService() {
        return scheduleService;
    }

    public final Repository verifyRepositoryAndCreateOutline(final RepositoryOutlineRequest request) {

        if (getConfiguredDockerDelegate().isRepositoryValid(request.getRepositoryName())) {

            final Repository repositoryOutline = getImageService()
                    .createRepositoryOutline(new RepositoryOutlineRequest(request.getRepositoryName()));

            getSynchronisationService().synchroniseUpstreamRepository(repositoryOutline);
            return repositoryOutline;

        }

        throw new IllegalArgumentException("Repository " + request.getRepositoryName() + " does not exist upstream");
    }

    @Override
    public final SynchronisationService getSynchronisationService() {
        return syncService;
    }

    @Override
    public final UserService getUserService() {
        return userService;
    }

    @Override
    public FileManager getFileManager() {
        return fileManager;
    }

    public final AuthenticationResult authenticateCredentials(final String username, final String password) {
        return userService.authenticateCredentials(username, password);
    }

    public final void trackBranch(final ImageKey imageKey, final String branchName) {

        getImageService().trackBranchOnImage(imageKey, branchName);
        synchroniseImage(imageKey);
    }

    private DockerApiClient configureDockerApiClient() {

        final RestClient dockerHubApiRestClient = new RestClient();
        final IDockerHubAuthenticator dockerHubAuthenticator;
        if (getAppProperties().isDockerHubAuthEnabled()) {
            dockerHubAuthenticator = new DockerHubAuthenticator(getAppProperties().getDockerHubCredentials(), dockerHubApiRestClient);
        } else {
            dockerHubAuthenticator = new NoOpDockerHubAuthenticator();
        }
        return new DockerHubApiClient(dockerHubApiRestClient, dockerHubAuthenticator);
    }
}
