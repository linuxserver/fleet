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

package io.linuxserver.fleet.sync;

import io.linuxserver.fleet.delegate.DockerHubDelegate;
import io.linuxserver.fleet.delegate.ImageDelegate;
import io.linuxserver.fleet.delegate.RepositoryDelegate;
import io.linuxserver.fleet.exception.SaveException;
import io.linuxserver.fleet.model.DockerHubImage;
import io.linuxserver.fleet.model.Image;
import io.linuxserver.fleet.model.Repository;
import io.linuxserver.fleet.sync.event.ImageUpdateEvent;
import io.linuxserver.fleet.sync.event.RepositoriesScannedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SyncIdleState extends AbstractSyncState implements SynchronisationState {

    private static final Logger LOGGER = LoggerFactory.getLogger(SyncIdleState.class);

    public SyncIdleState(ImageDelegate imageDelegate, RepositoryDelegate repositoryDelegate, DockerHubDelegate dockerHubDelegate) {
        super(imageDelegate, repositoryDelegate, dockerHubDelegate);
    }

    @Override
    public synchronized void synchronise(SynchronisationContext context) {

        updateState(context);

        SynchronisationListener contextListener = context.getListener();

        LOGGER.info("Starting synchronisation of all repositories");
        contextListener.onSynchronisationStart();

        List<String> repositories = getDockerHubDelegate().fetchAllRepositories();
        contextListener.onRepositoriesScanned(new RepositoriesScannedEvent(repositories));

        for (String repositoryName : repositories)
            synchroniseRepository(repositoryName, contextListener);

        LOGGER.info("Synchronisation complete");
        contextListener.onSynchronisationFinish();
    }

    private void updateState(SynchronisationContext context) {
        context.setState(new SyncRunningState(getImageDelegate(), getRepositoryDelegate(), getDockerHubDelegate()));
    }

    private void synchroniseRepository(String repositoryName, SynchronisationListener listener) {

        Repository repository = configureRepository(repositoryName);
        if (repository.isSyncEnabled()) {

            List<DockerHubImage> images = getDockerHubDelegate().fetchAllImagesFromRepository(repository.getName());

            int totalSize = images.size();
            LOGGER.info("Found {} images in Docker Hub", totalSize);
            for (int i = 0; i < totalSize; i++) {

                try {

                    DockerHubImage dockerHubImage = images.get(i);
                    Image image = configureImage(repository.getId(), dockerHubImage);

                    String maskedVersion = getMaskedVersion(repository.getName(), image.getName(), image.getVersionMask());
                    LOGGER.debug("Updated image version using mask. Mask=" + image.getVersionMask() + ", MaskedVersion=" + maskedVersion);

                    image.withPullCount(dockerHubImage.getPullCount()).withVersion(maskedVersion);

                    getImageDelegate().saveImage(image);
                    listener.onImageUpdated(new ImageUpdateEvent(image, i, totalSize));

                } catch (SaveException e) {
                    LOGGER.error("Unable to save updated image", e);
                }
            }

        } else {
            LOGGER.info("Skipping " + repositoryName);
        }
    }

    private String getMaskedVersion(String repositoryName, String imageName, String versionMask) {

        String tag = getDockerHubDelegate().fetchLatestImageTag(repositoryName, imageName);

        if (isTagJustLatestAndNotAVersion(tag) || null == versionMask)
            return tag;

        return extractMaskedVersion(tag, versionMask);
    }

    /**
     * Given the repository name from Docker Hub, this will attempt to find it in the database. If it exists, it gets returned,
     * otherwise a new record is created (with sync disabled) and that new record is returned instead.
     */
    private Repository configureRepository(String repositoryName) {

        Repository repository = getRepositoryDelegate().findRepositoryByName(repositoryName);

        if (isRepositoryNew(repository)) {

            try {

                return getRepositoryDelegate().saveRepository(new Repository(repositoryName));

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

        Image image = getImageDelegate().findImageByRepositoryAndImageName(repositoryId, dockerHubImage.getName());

        if (isImageNew(image)) {

            try {

                return getImageDelegate().saveImage(new Image(repositoryId, dockerHubImage.getName()));

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
