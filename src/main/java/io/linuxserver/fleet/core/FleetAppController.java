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

import io.linuxserver.fleet.core.config.WebConfiguration;
import io.linuxserver.fleet.v2.client.docker.DockerApiClient;
import io.linuxserver.fleet.v2.client.docker.dockerhub.DockerHubApiClient;
import io.linuxserver.fleet.v2.client.docker.queue.DockerApiDelegate;
import io.linuxserver.fleet.v2.client.docker.queue.DockerApiTaskConsumer;
import io.linuxserver.fleet.v2.client.docker.queue.DockerImageUpdateRequest;
import io.linuxserver.fleet.v2.client.docker.queue.TaskQueue;
import io.linuxserver.fleet.v2.key.ImageKey;
import io.linuxserver.fleet.v2.key.RepositoryKey;
import io.linuxserver.fleet.v2.web.WebRouteController;

/**
 * <p>
 * Primary entry point for the application. All contexts and resources are loaded
 * through this class.
 * </p>
 */
public class FleetAppController extends AbstractAppController {

    public  final DockerApiDelegate                   dockerApiDelegate;
    private final TaskQueue<DockerImageUpdateRequest> syncQueue;
    private final DockerApiTaskConsumer               syncConsumer;

    public FleetAppController() {

        syncQueue         = new TaskQueue<>();
        dockerApiDelegate = new DockerApiDelegate(this);
        syncConsumer      = new DockerApiTaskConsumer(this);
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

        syncConsumer.start();

//        getRepositoryManager().getAllRepositories().forEach(r -> {
//            r.getImages().forEach(i -> {
//                submitSyncRequest(new DockerImageUpdateRequest(i.getKey()));
//            });
//        });
    }

    public final WebConfiguration getWebConfiguration() {
        return new WebConfiguration(getAppProperties());
    }

    private void configureWeb() {
        new WebRouteController(this);
    }

    public final void handleException(final Exception e) {

    }

    public final DockerApiClient getDockerClient() {
        return new DockerHubApiClient();
    }

    public final boolean submitSyncRequest(final DockerImageUpdateRequest request) {
        return syncQueue.submitTask(request);
    }

    public final TaskQueue<DockerImageUpdateRequest> getSyncQueue() {
        return syncQueue;
    }

    public final DockerApiDelegate getConfiguredDockerDelegate() {
        return dockerApiDelegate;
    }
}
