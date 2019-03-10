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

import io.linuxserver.fleet.exception.SaveException;
import io.linuxserver.fleet.model.DockerHubImage;
import io.linuxserver.fleet.model.Image;
import io.linuxserver.fleet.model.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>
 * Handles the one-way synchronisation of Docker Hub repositories and images over to the Fleet
 * database. Any newly created image in Docker Hub will be automatically picked up and stored, but any
 * new repositories will be marked as skipped until someone manually sets it to be synchronised.
 * </p>
 */
public class SynchronisationDelegate {

    private static final Logger LOGGER = LoggerFactory.getLogger(SynchronisationDelegate.class);

    private final ImageDelegate         imageDelegate;
    private final RepositoryDelegate    repositoryDelegate;
    private final DockerHubDelegate     dockerHubDelegate;

    public SynchronisationDelegate(ImageDelegate imageDelegate, RepositoryDelegate repositoryDelegate, DockerHubDelegate dockerHubDelegate) {

        this.imageDelegate      = imageDelegate;
        this.repositoryDelegate = repositoryDelegate;
        this.dockerHubDelegate  = dockerHubDelegate;
    }

    public void synchronise() {
        synchronise((message) -> LOGGER.info(message.toString()));
    }

    /**
     * <p>
     * Scans Docker Hub for all images against all registered repositories for the user. It will then update the pull
     * and version information for each.
     * </p>
     */
    public void synchronise(SynchronisationListener listener) {

        if (null == listener) {

            synchronise();
            return;
        }

        LOGGER.info("Starting synchronisation of all repositories");

        List<String> repositories = dockerHubDelegate.fetchAllRepositories();

        for (String repositoryName : repositories)
            synchroniseRepository(repositoryName, listener);

        LOGGER.info("Synchronisation complete");
    }

    private void synchroniseRepository(String repositoryName, SynchronisationListener listener) {

        Repository repository = configureRepository(repositoryName);
        if (repository.isSyncEnabled()) {

            listener.onEvent("Synchronising " + repository);
            List<DockerHubImage> images = dockerHubDelegate.fetchAllImagesFromRepository(repository.getName());

            LOGGER.info("Found " + images.size() + " images in Docker Hub");
            for (DockerHubImage dockerHubImage : images)
                synchroniseImage(repository, dockerHubImage, listener);

        } else {
            listener.onEvent("Skipping sync for " + repositoryName);
        }
    }

    private void synchroniseImage(Repository repository, DockerHubImage dockerHubImage, SynchronisationListener listener) {

        try {

            Image image = configureImage(repository.getId(), dockerHubImage);

            String maskedVersion = getMaskedVersion(repository.getName(), image.getName(), image.getVersionMask());
            LOGGER.debug("Updated image version using mask. Mask=" + image.getVersionMask() + ", MaskedVersion=" + maskedVersion);

            image.withPullCount(dockerHubImage.getPullCount()).withVersion(maskedVersion);

            listener.onEvent("Synchronising " + image);
            imageDelegate.saveImage(image);

        } catch (SaveException e) {
            LOGGER.error("Unable to save updated image", e);
        }
    }

    private String getMaskedVersion(String repositoryName, String imageName, String versionMask) {

        String tag = dockerHubDelegate.fetchLatestImageTag(repositoryName, imageName);

        if (isTagJustLatestAndNotAVersion(tag) || null == versionMask)
            return tag;

        return extractMaskedVersion(tag, versionMask);
    }

    /**
     * Given the repository name from Docker Hub, this will attempt to find it in the database. If it exists, it gets returned,
     * otherwise a new record is created (with sync disabled) and that new record is returned instead.
     */
    private Repository configureRepository(String repositoryName) {

        Repository repository = repositoryDelegate.findRepositoryByName(repositoryName);

        if (isRepositoryNew(repository)) {

            try {

                return repositoryDelegate.saveRepository(new Repository(repositoryName));

            } catch (SaveException e) {
                LOGGER.error("Tried to save new repository during sync but failed", e);
            }
        }

        return repository;
    }

    /**
     * Looks up the image in the database to see if it already exists. If it does, it gets returned, otherwise a base
     * image is created with just the top-level information, as the rest will get updated later.
     */
    private Image configureImage(int repositoryId, DockerHubImage dockerHubImage) {

        Image image = imageDelegate.findImageByRepositoryAndImageName(repositoryId, dockerHubImage.getName());

        if (isImageNew(image)) {

            try {

                return imageDelegate.saveImage(new Image(repositoryId, dockerHubImage.getName()));

            } catch (SaveException e) {
                LOGGER.error("Tried to save new image during sync but failed", e);
            }
        }

        return image;
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
    private boolean isTagJustLatestAndNotAVersion(String tag) {
        return "latest".equals(tag);
    }

    private boolean isRepositoryNew(Repository repository) {
        return null == repository;
    }

    private boolean isImageNew(Image image) {
        return null == image;
    }
}
