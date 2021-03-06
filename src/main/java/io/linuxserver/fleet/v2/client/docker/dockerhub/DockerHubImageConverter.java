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

package io.linuxserver.fleet.v2.client.docker.dockerhub;

import io.linuxserver.fleet.dockerhub.model.DockerHubV2Image;
import io.linuxserver.fleet.v2.client.docker.converter.AbstractDockerResponseConverter;
import io.linuxserver.fleet.v2.types.docker.DockerImage;

public class DockerHubImageConverter extends AbstractDockerResponseConverter<DockerHubV2Image, DockerImage> {

    @Override
    protected final DockerImage doPlainConvert(final DockerHubV2Image dockerApiImage) {

        return new DockerImage(dockerApiImage.getName(),
                               dockerApiImage.getNamespace(),
                               dockerApiImage.getDescription(),
                               dockerApiImage.getStarCount(),
                               dockerApiImage.getPullCount(),
                               parseDockerHubDate(dockerApiImage.getLastUpdated()));
    }

    @Override
    public Class<DockerHubV2Image> getConverterClass() {
        return DockerHubV2Image.class;
    }
}
