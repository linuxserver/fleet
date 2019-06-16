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
import io.linuxserver.fleet.model.docker.DockerImage;
import io.linuxserver.fleet.model.docker.DockerTag;
import io.linuxserver.fleet.model.internal.Image;
import io.linuxserver.fleet.model.internal.Repository;
import io.linuxserver.fleet.model.internal.Tag;
import io.linuxserver.fleet.sync.event.ImageUpdateEvent;
import io.linuxserver.fleet.sync.event.RepositoriesScannedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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

            try {

                onStart(context);

                List<String> repositories = context.getDockerHubDelegate().fetchAllRepositories();

                onRepositoryScanned(context, repositories);
                checkAndRemoveMissingRepositories(repositories, context);

                for (String repositoryName : repositories)
                    synchroniseRepository(repositoryName, context);

            } catch (DockerHubException e) {

                LOGGER.error("Synchronisation process failed on the first step. Will skip for now.", e);
                onSkip(context);

            } finally {
                onFinish(context);
            }

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

            List<DockerImage> images = context.getDockerHubDelegate().fetchAllImagesFromRepository(repository.getName());
            checkAndRemoveMissingImages(repository, images, context);

            int totalSize = images.size();
            LOGGER.info("Found {} images in Docker Hub", totalSize);
            for (int i = 0; i < totalSize; i++) {

                try {

                    DockerImage dockerImage = images.get(i);
                    Image image = configureImage(repository, dockerImage, context);

                    String versionMask = getVersionMask(repository.getVersionMask(), image.getVersionMask());
                    Tag maskedVersion = getLatestTagAndCreateMaskedVersion(repository.getName(), image.getName(), versionMask, context);
                    LOGGER.debug("Updated image version using mask. Mask=" + versionMask + ", MaskedVersion=" + maskedVersion);

                    image.withPullCount(dockerImage.getPullCount());
                    image.updateTag(maskedVersion);

                    context.getImageDelegate().saveImage(image);
                    onImageUpdated(context, new ImageUpdateEvent(image, i + 1, totalSize));

                } catch (SaveException e) {
                    LOGGER.error("Unable to save updated image", e);
                }
            }

        } else {
            LOGGER.info("Skipping " + repositoryName);
        }
    }

    private void checkAndRemoveMissingRepositories(List<String> repositories, SynchronisationContext context) {

        LOGGER.info("Checking for any removed repositories.");
        for (Repository storedRepository : context.getRepositoryDelegate().fetchAllRepositories()) {

            if (!repositories.contains(storedRepository.getName())) {

                LOGGER.info("Found repository which no longer exists in Docker Hub. Removing {}", storedRepository.getName());
                context.getRepositoryDelegate().removeRepository(storedRepository.getId());
            }
        }
    }

    private void checkAndRemoveMissingImages(Repository repository, List<DockerImage> images, SynchronisationContext context) {

        List<String> dockerHubImageNames = images.stream().map(DockerImage::getName).collect(Collectors.toList());

        LOGGER.info("Checking for any removed images under {}", repository.getName());
        for (Image storedImage : context.getImageDelegate().fetchImagesByRepository(repository.getId())) {

            if (!dockerHubImageNames.contains(storedImage.getName())) {

                LOGGER.info("Found image which no longer exists in Docker Hub. Removing {}", storedImage.getName());
                context.getImageDelegate().removeImage(storedImage.getId());
            }
        }
    }

    private String getVersionMask(String repositoryMask, String imageMask) {
        return imageMask == null ? repositoryMask : imageMask;
    }

    private Tag getLatestTagAndCreateMaskedVersion(String repositoryName, String imageName, String versionMask, SynchronisationContext context) {

        DockerTag tag = context.getDockerHubDelegate().fetchLatestImageTag(repositoryName, imageName);

        if (null == tag)
            return Tag.NONE;

        if (isTagJustLatestAndNotAVersion(tag) || null == versionMask)
            return new Tag(tag.getName(), tag.getName(), tag.getBuildDate());

        return new Tag(tag.getName(), extractMaskedVersion(tag.getName(), versionMask), tag.getBuildDate());
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
    private Image configureImage(Repository repository, DockerImage dockerHubImage, SynchronisationContext context) {

        Image image = context.getImageDelegate().findImageByRepositoryAndImageName(repository.getId(), dockerHubImage.getName());

        if (isImageNew(image)) {

            try {

                return context.getImageDelegate().saveImage(new Image(repository, dockerHubImage.getName()));

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
    private boolean isTagJustLatestAndNotAVersion(DockerTag tag) {
        return "latest".equals(tag.getName());
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
