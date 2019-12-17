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

import io.linuxserver.fleet.dockerhub.model.DockerHubV2Tag;
import io.linuxserver.fleet.dockerhub.model.DockerHubV2TagDigest;
import io.linuxserver.fleet.v2.types.docker.DockerTag;
import io.linuxserver.fleet.v2.types.docker.DockerTagManifestDigest;

public class DockerHubTagConverter extends AbstractDockerHubConverter<DockerHubV2Tag, DockerTag> {

    @Override
    protected final DockerTag doPlainConvert(final DockerHubV2Tag dockerHubV2Tag) {

        final DockerTag dockerTag = new DockerTag(dockerHubV2Tag.getName(),
                                                  dockerHubV2Tag.getFullSize(),
                                                  parseDockerHubDate(dockerHubV2Tag.getLastUpdated()));

        for (DockerHubV2TagDigest tagImageDigest : dockerHubV2Tag.getImages()) {

            dockerTag.addDigest(new DockerTagManifestDigest(tagImageDigest.getSize(),
                                                            tagImageDigest.getDigest(),
                                                            tagImageDigest.getArchitecture(),
                                                            tagImageDigest.getVariant()));
        }

        return dockerTag;
    }

    @Override
    public Class<DockerHubV2Tag> getConverterClass() {
        return DockerHubV2Tag.class;
    }
}
