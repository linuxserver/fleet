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

package io.linuxserver.fleet.delegate;

import io.linuxserver.fleet.dockerhub.DockerHubClient;
import io.linuxserver.fleet.dockerhub.model.DockerHubV2Image;
import io.linuxserver.fleet.dockerhub.model.DockerHubV2Tag;
import io.linuxserver.fleet.model.DockerHubImage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class DockerHubDelegate {

    private final DockerHubClient dockerHubClient;

    public DockerHubDelegate(DockerHubClient dockerHubClient) {
        this.dockerHubClient = dockerHubClient;
    }

    public List<String> fetchAllRepositories() {
        return dockerHubClient.fetchAllRepositories().getNamespaces();
    }

    public List<DockerHubImage> fetchAllImagesFromRepository(String repositoryName) {

        List<DockerHubImage> images = new ArrayList<>();

        for (DockerHubV2Image apiImage : dockerHubClient.fetchImagesFromRepository(repositoryName))
            images.add(convertApiImageToInternalImage(apiImage));

        images.sort(Comparator.comparing(DockerHubImage::getName));

        return images;
    }

    public String fetchLatestImageTag(String repositoryName, String imageName) {

        DockerHubV2Tag dockerHubV2Tag = dockerHubClient.fetchLatestTagForImage(repositoryName, imageName);

        if (null != dockerHubV2Tag)
            return dockerHubV2Tag.getName();

        return null;
    }

    private DockerHubImage convertApiImageToInternalImage(DockerHubV2Image apiImage) {

        return new DockerHubImage(
            apiImage.getName(),
            apiImage.getNamespace(),
            apiImage.getDescription(),
            apiImage.getStarCount(),
            apiImage.getPullCount(),
            parseDockerHubDate(apiImage.getLastUpdated())
        );
    }

    private LocalDateTime parseDockerHubDate(String date) {

        if (null == date)
            return null;

        return LocalDateTime.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'"));
    }
}
