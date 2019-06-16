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

package io.linuxserver.fleet.dockerhub.queue;

import io.linuxserver.fleet.delegate.DockerHubDelegate;
import io.linuxserver.fleet.model.internal.Image;
import io.linuxserver.fleet.queue.FleetRequest;

import java.util.Objects;

public class DockerHubSyncRequest implements FleetRequest<DockerHubSyncResponse> {

    private final DockerHubDelegate dockerHubDelegate;
    private final Image             image;

    public DockerHubSyncRequest(DockerHubDelegate dockerHubDelegate, Image image) {

        this.dockerHubDelegate  = dockerHubDelegate;
        this.image              = image;
    }

    @Override
    public DockerHubSyncResponse execute() {
        return new DockerHubSyncResponse(dockerHubDelegate, image);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DockerHubSyncRequest that = (DockerHubSyncRequest) o;
        return Objects.equals(image, that.image);
    }

    @Override
    public int hashCode() {
        return Objects.hash(image);
    }
}
