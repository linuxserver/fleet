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

package io.linuxserver.fleet.v2.service;

import io.linuxserver.fleet.core.FleetAppController;
import io.linuxserver.fleet.v2.client.docker.queue.DockerApiDelegate;
import io.linuxserver.fleet.v2.client.docker.queue.DockerApiTaskConsumer;
import io.linuxserver.fleet.v2.client.docker.queue.DockerImageUpdateRequest;
import io.linuxserver.fleet.v2.client.docker.queue.TaskQueue;
import io.linuxserver.fleet.v2.key.ImageKey;
import io.linuxserver.fleet.v2.key.ImageLookupKey;
import io.linuxserver.fleet.v2.types.Image;
import io.linuxserver.fleet.v2.types.Repository;
import io.linuxserver.fleet.v2.types.docker.DockerImage;
import io.linuxserver.fleet.v2.types.internal.ImageOutlineRequest;

import java.util.List;

public class SynchronisationService extends AbstractAppService {

    private final TaskQueue<DockerImageUpdateRequest> syncQueue;
    private final DockerApiTaskConsumer               taskConsumer;

    public SynchronisationService(FleetAppController controller) {
        super(controller);

        syncQueue = new TaskQueue<>();
        taskConsumer = new DockerApiTaskConsumer(this);
        taskConsumer.start();
    }

    public final void synchroniseUpstreamRepository(final Repository repository) {

        if (repository.isSyncEnabled()) {

            final List<DockerImage> apiImages = getController().getConfiguredDockerDelegate().getImagesForRepository(repository.getKey());
            for (DockerImage apiImage : apiImages) {

                final Image cachedImage = getController().getImageService()
                        .lookupImage(new ImageLookupKey(apiImage.getRepository() + "/" + apiImage.getName()));

                if (null == cachedImage) {

                    getLogger().info("Found image from API which is not currently cached. Will add to system: {}", apiImage);
                    final ImageOutlineRequest outlineRequest = new ImageOutlineRequest(repository.getKey(),
                                                                                       apiImage.getName(),
                                                                                       apiImage.getDescription(),
                                                                                       apiImage.getBuildDate());

                    final Image imageOutline = getController().getImageService().createImageOutline(outlineRequest);
                    synchroniseImage(imageOutline.getKey());
                }
            }

        } else {
            getLogger().info("Will not check upstream repository {} as synchronisation is disabled", repository);
        }
    }

    public final void synchroniseCachedRepository(final Repository repository) {

        if (repository.isSyncEnabled()) {
            for (Image image : repository.getImages()) {
                if (image.isSyncEnabled()) {
                    boolean submitted = synchroniseImage(image.getKey());
                    if (!submitted) {
                        getLogger().warn("Unable to place sync request for image {} on queue", image.getKey());
                    }
                } else {
                    getLogger().info("Ignoring sync request for {} as it has synchronisation disabled.", image);
                }
            }
        } else {
            getLogger().info("Will not synchronise images in {} as it has synchronisation disabled", repository);
        }
    }

    public final boolean synchroniseImage(final ImageKey imageKey) {
        return syncQueue.submitTask(new DockerImageUpdateRequest(imageKey));
    }

    public final TaskQueue<DockerImageUpdateRequest> getSyncQueue() {
        return syncQueue;
    }

    public final DockerApiDelegate getConfiguredDockerDelegate() {
        return getController().getConfiguredDockerDelegate();
    }

    public final boolean isConsumerRunning() {
        return taskConsumer.isThreadRunning();
    }

    public final boolean isSyncQueueEmpty() {
        return getSyncQueue().isEmpty();
    }
}
