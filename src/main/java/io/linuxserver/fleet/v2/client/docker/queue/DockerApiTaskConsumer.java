/*
 * Copyright (c)  2019 LinuxServer.io
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

package io.linuxserver.fleet.v2.client.docker.queue;

import io.linuxserver.fleet.v2.service.SynchronisationService;
import io.linuxserver.fleet.v2.thread.AbstractTaskQueueConsumer;
import io.linuxserver.fleet.v2.thread.TaskExecutionException;

public final class DockerApiTaskConsumer extends AbstractTaskQueueConsumer<DockerApiDelegate, DockerImageUpdateResponse, DockerImageUpdateRequest> {

    public DockerApiTaskConsumer(final SynchronisationService syncService) {

        super(syncService.getController(),
              syncService.getConfiguredDockerDelegate(),
              syncService.getSyncQueue(),
              "DockerSyncConsumer");
    }

    @Override
    protected void handleTaskResponse(final DockerImageUpdateResponse response) {

        try {
            response.handleDockerApiResponse();
        } catch (Exception e) {

            getLogger().error("handleTaskResponse caught unhandled error, but not something worthy of stalling thread", e);
            throw new TaskExecutionException(e);
        }
    }
}
