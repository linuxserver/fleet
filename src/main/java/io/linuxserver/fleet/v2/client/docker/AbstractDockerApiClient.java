/*
 * Copyright (c)  2020 LinuxServer.io
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

package io.linuxserver.fleet.v2.client.docker;

import io.linuxserver.fleet.v2.client.docker.converter.DockerResponseConverter;
import io.linuxserver.fleet.v2.types.docker.DockerImage;
import io.linuxserver.fleet.v2.types.docker.DockerTag;

import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractDockerApiClient<D, T, IC extends DockerResponseConverter<D, DockerImage>, TC extends DockerResponseConverter<T, DockerTag>> implements DockerApiClient {

    private final IC imageConverter;
    private final TC tagConverter;

    public AbstractDockerApiClient(final IC imageConverter, final TC tagConverter) {
        this.imageConverter = imageConverter;
        this.tagConverter   = tagConverter;
    }

    @Override
    public final DockerImage fetchImage(String imageName) {

        final D dockerModel = fetchImageFromApi(imageName);
        if (null == dockerModel) {
            return null;
        }
        return imageConverter.convert(dockerModel);
    }

    @Override
    public final List<DockerImage> fetchAllImages(String repositoryName) {
        return fetchAllImagesFromApi(repositoryName).stream().map(imageConverter::convert).collect(Collectors.toList());
    }

    @Override
    public final List<DockerTag> fetchImageTags(String imageName) {
        return fetchTagsFromApi(imageName).stream().map(tagConverter::convert).collect(Collectors.toList());
    }

    protected abstract D       fetchImageFromApi(final String imageName);
    protected abstract List<D> fetchAllImagesFromApi(final String repositoryName);
    protected abstract List<T> fetchTagsFromApi(final String imageName);
}
