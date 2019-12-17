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

import io.linuxserver.fleet.v2.client.docker.DockerImageNotFoundException;
import io.linuxserver.fleet.v2.key.ImageKey;
import io.linuxserver.fleet.v2.thread.AbstractAppTask;

public class DockerImageUpdateRequest extends AbstractAppTask<DockerApiDelegate, DockerImageUpdateResponse> {

    private final ImageKey imageKey;

    public DockerImageUpdateRequest(final ImageKey imageKey) {
        super(imageKey.toString());
        this.imageKey = imageKey;
    }

    @Override
    protected DockerImageUpdateResponse performTaskInternal(final DockerApiDelegate delegate) {

        try {
            return new DockerImageUpdateResponse(delegate.getController(), imageKey, delegate.getCurrentImageView(imageKey));
        } catch (DockerImageNotFoundException e) {
            getLogger().warn("Request responded with an empty response so assuming image has been removed upstream. Error message: {}", e.getMessage());
            return new DockerImageMissingUpdateResponse(delegate.getController(), imageKey);
        }

    }
}
