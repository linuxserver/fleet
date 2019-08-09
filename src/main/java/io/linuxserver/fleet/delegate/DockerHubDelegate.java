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
import io.linuxserver.fleet.dockerhub.util.DockerTagFinder;
import io.linuxserver.fleet.model.docker.DockerImage;
import io.linuxserver.fleet.model.docker.DockerTag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class DockerHubDelegate {

    private static final Logger LOGGER = LoggerFactory.getLogger(DockerHubDelegate.class);

    private final DockerHubClient dockerHubClient;
    private final DockerTagFinder dockerTagFinder;

    public DockerHubDelegate(DockerHubClient dockerHubClient) {

        this.dockerHubClient = dockerHubClient;
        this.dockerTagFinder = new DockerTagFinder();
    }

    public List<String> fetchAllRepositories() {
        return dockerHubClient.fetchAllRepositories().getNamespaces();
    }

    public List<DockerImage> fetchAllImagesFromRepository(String repositoryName) {

        List<DockerImage> images = new ArrayList<>();

        for (DockerHubV2Image apiImage : dockerHubClient.fetchImagesFromRepository(repositoryName))
            images.add(convertImage(apiImage));

        images.sort(Comparator.comparing(DockerImage::getName));

        return images;
    }

    public DockerImage fetchImageFromRepository(String repositoryName, String imageName) {
        return convertImage(dockerHubClient.fetchImageFromRepository(repositoryName, imageName));
    }

    public List<DockerTag> fetchAllTagsForImage(String repositoryName, String imageName) {
        return dockerHubClient.fetchAllTagsForImage(repositoryName, imageName).stream().map(this::convertTag).collect(Collectors.toList());
    }

    public DockerTag fetchLatestImageTag(String repositoryName, String imageName) {

        List<DockerTag> tags = fetchAllTagsForImage(repositoryName, imageName);

        if (tags.isEmpty()) {
            return null;
        }

        return dockerTagFinder.findVersionedTagMatchingBranch(tags, "latest");
    }

    private DockerImage convertImage(DockerHubV2Image dockerHubV2Image) {

        if (dockerHubV2Image == null) {
            return null;
        }

        return new DockerImage(
            dockerHubV2Image.getName(),
            dockerHubV2Image.getNamespace(),
            dockerHubV2Image.getDescription(),
            dockerHubV2Image.getStarCount(),
            dockerHubV2Image.getPullCount(),
            parseDockerHubDate(dockerHubV2Image.getLastUpdated())
        );
    }

    private DockerTag convertTag(DockerHubV2Tag dockerHubV2Tag) {

        if (dockerHubV2Tag == null) {
            return null;
        }

        return new DockerTag(
            dockerHubV2Tag.getName(),
            dockerHubV2Tag.getFullSize(),
            parseDockerHubDate(dockerHubV2Tag.getLastUpdated())
        );
    }

    private LocalDateTime parseDockerHubDate(String date) {

        if (null == date)
            return null;

        try {
            return LocalDateTime.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'"));
        } catch (DateTimeParseException e) {
            return LocalDateTime.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"));
        } catch (Exception e) {

            LOGGER.warn("parseDockerHubDate(" + date + ") unable to parse date.");
            return null;
        }
    }
}
