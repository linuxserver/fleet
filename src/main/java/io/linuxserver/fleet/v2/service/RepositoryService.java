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

package io.linuxserver.fleet.v2.service;

import io.linuxserver.fleet.db.query.InsertUpdateResult;
import io.linuxserver.fleet.dockerhub.util.DockerTagFinder;
import io.linuxserver.fleet.v2.cache.RepositoryCache;
import io.linuxserver.fleet.v2.db.ImageDAO;
import io.linuxserver.fleet.v2.key.ImageKey;
import io.linuxserver.fleet.v2.key.ImageLookupKey;
import io.linuxserver.fleet.v2.key.RepositoryKey;
import io.linuxserver.fleet.v2.types.*;
import io.linuxserver.fleet.v2.types.docker.DockerImage;
import io.linuxserver.fleet.v2.types.docker.DockerTag;
import io.linuxserver.fleet.v2.types.internal.ImageOutlineRequest;
import io.linuxserver.fleet.v2.types.internal.RepositoryOutlineRequest;
import io.linuxserver.fleet.v2.types.internal.TagBranchOutlineRequest;
import io.linuxserver.fleet.v2.types.meta.ItemSyncSpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class RepositoryService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RepositoryService.class);

    private final ImageDAO        imageDAO;
    private final RepositoryCache repositoryCache;

    public RepositoryService(final ImageDAO imageDAO) {

        this.imageDAO        = imageDAO;
        this.repositoryCache = new RepositoryCache();

        reloadCache();
    }

    public final void reloadCache() {

        repositoryCache.clear();
        repositoryCache.addAllItems(imageDAO.fetchAllRepositories());
    }

    public final Image updateImageSpec(final ImageKey imageKey, final ItemSyncSpec updatedSpec) {

        final Image cachedImage = getImage(imageKey);
        if (null == cachedImage) {
            throw new IllegalArgumentException("Image key " + imageKey + " did not match known image");
        }

        final Image updated = cachedImage.cloneWithSyncSpec(updatedSpec);
        return storeImage(updated);
    }

    public final Repository updateRepositorySpec(final RepositoryKey repositoryKey, final ItemSyncSpec updatedSpec) {

        final Repository cachedRepository = getRepository(repositoryKey);
        if (null == cachedRepository) {
            throw new IllegalArgumentException("Repository key " + repositoryKey + " did not match known repository");
        }

        final Repository updatedRepository = cachedRepository.cloneWithSyncSpec(updatedSpec);

        final InsertUpdateResult<Repository> result = imageDAO.storeRepository(updatedRepository);
        if (result.isError()) {

            LOGGER.error("Unable to store repository {}. Update returned error: {}", repositoryKey, result.getStatusMessage());
            throw new RuntimeException("Failed to store repository: " + result.getStatusMessage());
        }

        final Repository storedRepository = result.getResult();
        repositoryCache.addItem(storedRepository);

        return storedRepository;
    }

    public Repository createRepositoryOutline(final RepositoryOutlineRequest repositoryOutlineRequest) {

        final InsertUpdateResult<Repository> result = imageDAO.createRepositoryOutline(repositoryOutlineRequest);
        if (result.isError()) {

            LOGGER.error("Unable to create repository outline {}, reason: {}", repositoryOutlineRequest, result.getStatusMessage());
            throw new RuntimeException("Unable to create repository outline");
        }

        final Repository repositoryOutline = result.getResult();
        LOGGER.info("Successfully created outline for repository");
        repositoryCache.addItem(repositoryOutline);

        return repositoryOutline;
    }

    public final Image createImageOutline(final ImageOutlineRequest request) {

        final InsertUpdateResult<Image> result = imageDAO.createImageOutline(request);
        if (result.isError()) {

            LOGGER.error("Unable to create image outline {}, reason: {}", request, result.getStatusMessage());
            throw new RuntimeException("Unable to create outline");

        }

        final Image imageOutline = result.getResult();
        LOGGER.info("Successfully created outline for image {}", imageOutline);
        updateCache(imageOutline);

        return imageOutline;
    }

    public final void removeImage(final ImageKey imageKey) {

        final Image cachedImage = repositoryCache.findImage(imageKey);
        if (null != cachedImage) {

            final InsertUpdateResult<Void> removalResult = imageDAO.removeImage(cachedImage);
            if (removalResult.isError()) {
                throw new RuntimeException("Unable to remove persisted image: " + removalResult.getStatusMessage());
            }

            repositoryCache.findItem(cachedImage.getRepositoryKey()).removeImage(cachedImage);
        }
    }

    public final void removeRepository(final RepositoryKey repositoryKey) {

        if (repositoryCache.isItemCached(repositoryKey)) {

            final Repository cached = repositoryCache.findItem(repositoryKey);
            final InsertUpdateResult<Void> removalResult = imageDAO.removeRepository(cached);
            if (removalResult.isError()) {
                throw new RuntimeException("Unable to remove repository " + repositoryKey);
            }

            repositoryCache.removeItem(cached.getKey());

        } else {
            throw new IllegalArgumentException("Unable to find cached repository " + repositoryKey);
        }
    }

    public final Image storeImage(final Image image) {

        final InsertUpdateResult<Image> result = imageDAO.storeImage(image);
        if (result.isError()) {

            LOGGER.error("Unable to store image {}. Update returned error: {}", image, result.getStatusMessage());
            throw new RuntimeException("Failed to store image: " + result.getStatusMessage());
        }

        final Image storedImage = result.getResult();
        updateCache(storedImage);

        return storedImage;
    }

    public final Image getImage(final ImageKey imageKey) {
        return repositoryCache.findImage(imageKey);
    }

    public final Image lookupImage(final ImageLookupKey imageLookupKey) {
        return repositoryCache.lookupImage(imageLookupKey);
    }

    public final Repository getRepository(final RepositoryKey repositoryKey) {
        return repositoryCache.findItem(repositoryKey);
    }

    public final Repository getFirstRepository() {
        return getAllShownRepositories().stream().findFirst().orElse(null);
    }

    public final List<Repository> getAllRepositories() {
        return new ArrayList<>(repositoryCache.getAllItems());
    }

    public final List<Repository> getAllShownRepositories() {
        return getAllRepositories().stream().filter(r -> !r.isHidden()).collect(Collectors.toList());
    }

    public Image applyImageUpdate(final ImageKey imageKey, final DockerImage latestImage) {

        final Image cachedImage = getImage(imageKey);
        if (null == cachedImage) {

            LOGGER.warn("Attempted to update an image which is not currently cached. {} Skipping...", imageKey);
            return null;

        } else {

            final Image cloned = cachedImage.cloneForUpdate(latestImage.getPullCount(),
                                                            latestImage.getStarCount(),
                                                            latestImage.getDescription(),
                                                            latestImage.getBuildDate());

            for (TagBranch branch : cloned.getTagBranches()) {

                final DockerTag matchingTag = DockerTagFinder.findVersionedTagMatchingBranch(latestImage.getTags(), branch.getBranchName());
                if (null == matchingTag) {
                    LOGGER.warn("Unable to find tag for branch {} in image {}. Will not update tags.", branch.getBranchName(), cloned.getFullName());
                } else {

                    branch.updateLatestTag(new Tag(matchingTag.getName(),
                            matchingTag.getBuildDate(),
                            matchingTag.getDigests().stream()
                                    .filter(Objects::nonNull)
                                    .map(d -> new TagDigest(d.getSize(),
                                            d.getDigest(),
                                            d.getArchitecture(),
                                            d.getArchVariant())).collect(Collectors.toSet())));
                }
            }

            return storeImage(cloned);
        }
    }

    public void trackBranchOnImage(final ImageKey imageKey, final String branchName) {

        final Image image = repositoryCache.findImage(imageKey);
        if (null == image) {
            throw new IllegalArgumentException("Could not find image with key " + imageKey);
        }

        if (image.findTagBranchByName(branchName) != null) {
            throw new IllegalArgumentException("Image is already tracking branch " + branchName);
        }

        final InsertUpdateResult<TagBranch> outlineResult = imageDAO.createTagBranchOutline(new TagBranchOutlineRequest(imageKey, branchName));
        if (outlineResult.isError()) {
            throw new RuntimeException(outlineResult.getStatusMessage());
        }

        final Image updatableClone = image.cloneForUpdate();
        updatableClone.addTagBranch(outlineResult.getResult());
        storeImage(updatableClone);
    }

    private void updateCache(final Image storedImage) {

        final Repository imageParentRepository = repositoryCache.findItem(storedImage.getRepositoryKey());

        if (null != imageParentRepository) {
            imageParentRepository.addImage(storedImage);
        } else {
            LOGGER.warn("Could not find repository for image {}", storedImage);
        }
    }
}
