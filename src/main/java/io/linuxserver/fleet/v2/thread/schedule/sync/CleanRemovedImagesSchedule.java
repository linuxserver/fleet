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
import io.linuxserver.fleet.v2.thread.schedule.AbstractAppSchedule;
import io.linuxserver.fleet.v2.thread.schedule.ScheduleSpec;
import io.linuxserver.fleet.v2.types.Image;
import io.linuxserver.fleet.v2.types.Repository;
import io.linuxserver.fleet.v2.types.docker.DockerImage;

import java.util.List;
import java.util.stream.Collectors;

public final class CleanRemovedImagesSchedule extends AbstractAppSchedule {

    public CleanRemovedImagesSchedule(final ScheduleSpec spec,
                                      final FleetAppController controller) {
        super(spec, controller);
    }

    @Override
    public void executeSchedule() {

        final List<Repository> allRepositories = getController().getImageService().getAllRepositories();
        for (Repository repository : allRepositories) {

            getLogger().info("Checking for removed upstream images in " + repository);
            final List<DockerImage> apiImages = getController().getConfiguredDockerDelegate().getImagesForRepository(repository.getKey());
            if (apiImages.isEmpty()) {
                getLogger().warn("executeSchedule found no images for repository " + repository + " upstream. Playing it safe and ignoring clean function.");
            } else {

                final List<String> imageNames = apiImages.stream().map(DockerImage::getName).collect(Collectors.toList());
                for (Image cachedImage : repository.getImages()) {
                    if (!imageNames.contains(cachedImage.getName())) {

                        getLogger().info("Found removed image upstream. Deleting from cache: " + cachedImage);
                        getController().getImageService().removeImage(cachedImage.getKey());
                    }
                }
            }
        }
    }
}
