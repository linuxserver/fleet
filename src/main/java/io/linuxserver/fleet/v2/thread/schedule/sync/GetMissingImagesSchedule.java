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

package io.linuxserver.fleet.v2.thread.schedule.sync;

import io.linuxserver.fleet.core.FleetAppController;
import io.linuxserver.fleet.v2.client.docker.queue.DockerApiDelegate;
import io.linuxserver.fleet.v2.key.ImageLookupKey;
import io.linuxserver.fleet.v2.service.RepositoryService;
import io.linuxserver.fleet.v2.thread.schedule.AbstractAppSchedule;
import io.linuxserver.fleet.v2.thread.schedule.ScheduleSpec;
import io.linuxserver.fleet.v2.types.Image;
import io.linuxserver.fleet.v2.types.Repository;
import io.linuxserver.fleet.v2.types.docker.DockerImage;
import io.linuxserver.fleet.v2.types.internal.ImageOutlineRequest;

import java.util.List;

public class GetMissingImagesSchedule extends AbstractAppSchedule {

    private final DockerApiDelegate dockerApiDelegate;
    private final RepositoryService repositoryService;

    public GetMissingImagesSchedule(final ScheduleSpec spec,
                                    final FleetAppController controller) {
        super(spec, controller);
        dockerApiDelegate = controller.getConfiguredDockerDelegate();
        repositoryService = controller.getRepositoryService();
    }

    @Override
    public void executeSchedule() {

        final List<Repository> cachedRepositories = repositoryService.getAllRepositories();
        for (Repository repository : cachedRepositories) {

            if (repository.isSyncEnabled()) {

                final List<DockerImage> apiImages = dockerApiDelegate.getImagesForRepository(repository.getKey());
                for (DockerImage apiImage : apiImages) {

                    final Image cachedImage = repositoryService.lookupImage(new ImageLookupKey(apiImage.getRepository() + "/" + apiImage.getName()));
                    if (null == cachedImage) {

                        getLogger().info("Found image from API which is not currently cached. Will add to system: {}", apiImage);
                        final ImageOutlineRequest outlineRequest = new ImageOutlineRequest(repository.getKey(),
                                                                                           apiImage.getName(),
                                                                                           apiImage.getDescription(),
                                                                                           apiImage.getBuildDate());

                        final Image imageOutline = repositoryService.createImageOutline(outlineRequest);
                        getController().submitSyncRequestForImage(imageOutline.getKey());
                    }
                }

            } else {
                getLogger().info("Will not check upstream repository {} as synchronisation is disabled", repository);
            }
        }
    }
}
