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

import io.linuxserver.fleet.dockerhub.DockerHubException;
import io.linuxserver.fleet.exception.SaveException;
import io.linuxserver.fleet.model.DockerHubImage;
import io.linuxserver.fleet.model.Image;
import io.linuxserver.fleet.model.Repository;
import io.linuxserver.fleet.sync.event.ImageUpdateEvent;
import io.linuxserver.fleet.sync.event.RepositoriesScannedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DefaultSynchronisationState implements SynchronisationState {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultSynchronisationState.class);

    private static DefaultSynchronisationState state;

    private final AtomicBoolean running = new AtomicBoolean(false);

    private DefaultSynchronisationState() {}

    /**
     * <p>
     * Returns the current instance of the SyncState. Only one SyncState can be used
     * at runtime.
     * </p>
     */
    public static DefaultSynchronisationState instance() {

        if (null == state) {

            synchronized (DefaultSynchronisationState.class) {

                if (null == state) {

                    LOGGER.info("Creating single instance of sychronisation state");
                    state = new DefaultSynchronisationState();
                }
            }
        }

        return state;
    }

    /**
     * <p>
     * Performs a single synchronisation of Docker Hub repositories for the user. This state is aware
     * of its current running state and will only run if currently idle. It uses the provided dependencies
     * in the context.
     * </p>
     *
     * @param context
     *      All necessary dependencies required to perform a synchronisation. This should also include any
     *      listeners for feedback on the process.
     */
    @Override
    public void synchronise(SynchronisationContext context) {

        if (isProcessIdle()) {

            onStart(context);

            try {

                List<String> repositories = context.getDockerHubDelegate().fetchAllRepositories();
                onRepositoryScanned(context, repositories);

                for (String repositoryName : repositories)
                    synchroniseRepository(repositoryName, context);

            } catch (DockerHubException e) {

                LOGGER.error("Synchronisation process failed on the first step. Will skip for now.", e);
                onSkip(context);
            }

            onFinish(context);

        } else {
            onSkip(context);
        }
    }

    /**
     * <p>
     * Checks the currently running status to ensure the sync process is not run more than once.
     * </p>
     */
    private boolean isProcessIdle() {

        synchronized (running) {
            return !running.get();
        }
    }

    private void synchroniseRepository(String repositoryName, SynchronisationContext context) {

        Repository repository = configureRepository(repositoryName, context);
        if (repository.isSyncEnabled()) {

            List<DockerHubImage> images = context.getDockerHubDelegate().fetchAllImagesFromRepository(repository.getName());

            int totalSize = images.size();
            LOGGER.info("Found {} images in Docker Hub", totalSize);
            for (int i = 0; i < totalSize; i++) {

                try {

                    DockerHubImage dockerHubImage = images.get(i);
                    Image image = configureImage(repository.getId(), dockerHubImage, context);

                    String maskedVersion = getMaskedVersion(repository.getName(), image.getName(), image.getVersionMask(), context);
                    LOGGER.debug("Updated image version using mask. Mask=" + image.getVersionMask() + ", MaskedVersion=" + maskedVersion);

                    image.withPullCount(dockerHubImage.getPullCount()).withVersion(maskedVersion);

                    context.getImageDelegate().saveImage(image);
                    onImageUpdated(context, new ImageUpdateEvent(image, i, totalSize));

                } catch (SaveException e) {
                    LOGGER.error("Unable to save updated image", e);
                }
            }

        } else {
            LOGGER.info("Skipping " + repositoryName);
        }
    }

    private String getMaskedVersion(String repositoryName, String imageName, String versionMask, SynchronisationContext context) {

        String tag = context.getDockerHubDelegate().fetchLatestImageTag(repositoryName, imageName);

        if (null == tag)
            return "<Never Built>";

        if (isTagJustLatestAndNotAVersion(tag) || null == versionMask)
            return tag;

        return extractMaskedVersion(tag, versionMask);
    }

    /**
     * Given the repository name from Docker Hub, this will attempt to find it in the database. If it exists, it gets returned,
     * otherwise a new record is created (with sync disabled) and that new record is returned instead.
     */
    private Repository configureRepository(String repositoryName, SynchronisationContext context) {

        Repository repository = context.getRepositoryDelegate().findRepositoryByName(repositoryName);

        if (isRepositoryNew(repository)) {

            try {

                return context.getRepositoryDelegate().saveRepository(new Repository(repositoryName));

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
    private Image configureImage(int repositoryId, DockerHubImage dockerHubImage, SynchronisationContext context) {

        Image image = context.getImageDelegate().findImageByRepositoryAndImageName(repositoryId, dockerHubImage.getName());

        if (isImageNew(image)) {

            try {

                return context.getImageDelegate().saveImage(new Image(repositoryId, dockerHubImage.getName()));

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

    private void onStart(SynchronisationContext context) {

        synchronized (running) {
            running.set(true);
        }

        context.getListeners().forEach(SynchronisationListener::onSynchronisationStart);
    }

    private void onFinish(SynchronisationContext context) {

        synchronized (running) {
            running.set(false);
        }

        context.getListeners().forEach(SynchronisationListener::onSynchronisationFinish);
    }

    private void onSkip(SynchronisationContext context) {
        context.getListeners().forEach(SynchronisationListener::onSynchronisationSkipped);
    }

    private void onRepositoryScanned(SynchronisationContext context, List<String> repositories) {
        context.getListeners().forEach(l -> l.onRepositoriesScanned(new RepositoriesScannedEvent(repositories)));
    }

    private void onImageUpdated(SynchronisationContext context, ImageUpdateEvent event) {
        context.getListeners().forEach(l -> l.onImageUpdated(event));
    }
}
