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

import io.linuxserver.fleet.delegate.ImageDelegate;
import io.linuxserver.fleet.exception.SaveException;
import io.linuxserver.fleet.model.internal.Image;
import io.linuxserver.fleet.queue.AbstractQueueConsumer;
import io.linuxserver.fleet.queue.RequestQueue;

public class DockerHubSyncConsumer extends AbstractQueueConsumer<DockerHubSyncResponse, DockerHubSyncRequest> {

    private final ImageDelegate imageDelegate;

    public DockerHubSyncConsumer(ImageDelegate imageDelegate, RequestQueue<DockerHubSyncRequest> requestQueue, String name) {
        super(requestQueue, name);
        this.imageDelegate = imageDelegate;
    }

    @Override
    protected void handleResponse(DockerHubSyncResponse response) {

        try {

            final Image image = response.getImage();
            imageDelegate.saveImage(image);

        } catch (SaveException e) {
            getLogger().error("handleResponse unable to save image: {}", e.getMessage());
        }
    }
}
