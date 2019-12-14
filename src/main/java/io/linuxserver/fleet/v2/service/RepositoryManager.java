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
import io.linuxserver.fleet.v2.cache.RepositoryCache;
import io.linuxserver.fleet.v2.db.ImageDAO;
import io.linuxserver.fleet.v2.key.ImageKey;
import io.linuxserver.fleet.v2.key.ImageLookupKey;
import io.linuxserver.fleet.v2.key.RepositoryKey;
import io.linuxserver.fleet.v2.types.Image;
import io.linuxserver.fleet.v2.types.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RepositoryManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(RepositoryManager.class);

    private final ImageDAO        imageDAO;
    private final RepositoryCache repositoryCache;

    public RepositoryManager(final ImageDAO imageDAO) {

        this.imageDAO        = imageDAO;
        this.repositoryCache = new RepositoryCache();

        reloadCache();
    }

    public final void reloadCache() {
        repositoryCache.addAllItems(imageDAO.fetchAllRepositories());
    }

    public final Repository storeRepository(final Repository repository) {

        final InsertUpdateResult<Repository> result = imageDAO.storeRepository(repository);
        if (result.isError()) {

            LOGGER.error("Unable to store repository {}. Update returned error: {}", repository, result.getStatusMessage());
            throw new RuntimeException("Failed to store repository: " + result.getStatusMessage());
        }

        final Repository storedRepository = result.getResult();
        repositoryCache.addItem(storedRepository);

        return storedRepository;
    }

    public final Image storeImage(final Image image) {

        final InsertUpdateResult<Image> result = imageDAO.storeImage(image);
        if (result.isError()) {

            LOGGER.error("Unable to store image {}. Update returned error: {}", image, result.getStatusMessage());
            throw new RuntimeException("Failed to store image: " + result.getStatusMessage());
        }

        final Image      storedImage           = result.getResult();
        final Repository imageParentRepository = repositoryCache.findItem(storedImage.getRepositoryKey());

        imageParentRepository.addImage(storedImage);

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
        return repositoryCache.getAllItems().stream().findFirst().orElse(null);
    }

    public final List<Repository> getAllRepositories() {
        return new ArrayList<>(repositoryCache.getAllItems());
    }

    public final List<Repository> getAllSynchronisedRepositories() {
        return getAllRepositories().stream().filter(Repository::isSyncEnabled).collect(Collectors.toList());
    }
}
