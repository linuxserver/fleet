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
import io.linuxserver.fleet.v2.types.docker.DockerImage;
import io.linuxserver.fleet.v2.types.docker.DockerTag;
import io.linuxserver.fleet.model.internal.Image;
import io.linuxserver.fleet.v2.types.Tag;
import io.linuxserver.fleet.queue.FleetResponse;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DockerHubSyncResponse implements FleetResponse {

    private final DockerHubDelegate dockerHubDelegate;
    private final Image image;

    public DockerHubSyncResponse(DockerHubDelegate dockerHubDelegate, Image image) {

        this.dockerHubDelegate  = dockerHubDelegate;
        this.image              = Image.copyOf(image);
    }

    @Override
    public void handle() {

        DockerImage dockerImage = dockerHubDelegate.fetchImageFromRepository(image.getKey().getRepositoryKey().getName(), image.getName());

        String versionMask      = getVersionMask(null, image.getVersionMask());
        Tag maskedVersion       = getLatestTagAndCreateMaskedVersion(versionMask);

        image.withPullCount(dockerImage.getPullCount());
        image.updateTag(maskedVersion);
    }

    public Image getImage() {
        return image;
    }

    private String getVersionMask(String repositoryMask, String imageMask) {
        return imageMask == null ? repositoryMask : imageMask;
    }

    private Tag getLatestTagAndCreateMaskedVersion(String versionMask) {

        DockerTag tag = dockerHubDelegate.fetchLatestImageTag(image.getKey().getRepositoryKey().getName(), image.getName());

        if (null == tag)
            return Tag.NONE;

        if (isTagJustLatestAndNotAVersion(tag) || null == versionMask)
            return new Tag(tag.getName(), tag.getName(), tag.getBuildDate());

        return new Tag(tag.getName(), extractMaskedVersion(tag.getName(), versionMask), tag.getBuildDate());
    }

    private String extractMaskedVersion(String fullTag, String versionMask) {

        Pattern pattern = Pattern.compile(versionMask);
        Matcher matcher = pattern.matcher(fullTag);

        if (matcher.matches()) {

            StringBuilder tagBuilder = new StringBuilder();

            for (int groupNum = 1; groupNum <= matcher.groupCount(); groupNum++)
                tagBuilder.append(matcher.group(groupNum));

            return tagBuilder.toString();
        }

        return fullTag;
    }

    /**
     * <p>
     * If the top-level tag is not versioned, a mask can't be applied.
     * </p>
     */
    private boolean isTagJustLatestAndNotAVersion(DockerTag tag) {
        return "latest".equals(tag.getName());
    }
}
