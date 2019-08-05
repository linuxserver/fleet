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

package io.linuxserver.fleet.cache;

import io.linuxserver.fleet.model.internal.Image;
import io.linuxserver.fleet.model.key.ImageKey;
import io.linuxserver.fleet.model.key.RepositoryKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class ImageCache {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImageCache.class);

    private final Map<RepositoryKey, List<Image>> cachedImages;

    public ImageCache() {
        this.cachedImages = new HashMap<>();
    }

    public void updateCache(Image image) {

        Image updatedImage = Image.copyOf(image);

        if (cachedImages.containsKey(updatedImage.getKey().getRepositoryKey())) {

            List<Image> images = cachedImages.get(updatedImage.getKey().getRepositoryKey());
            int cachedImageLocation = images.indexOf(updatedImage);

            if (cachedImageLocation > -1) {

                LOGGER.info("updateCache({}) Updating existing cached image.", updatedImage);
                images.set(cachedImageLocation, updatedImage);

            } else {

                LOGGER.info("updateCache({}) Adding image to cache", updatedImage);
                images.add(updatedImage);
            }

        } else {

            List<Image> images = new ArrayList<>();
            images.add(updatedImage);

            LOGGER.info("updateCache({}) Creating new cache for repository {}", updatedImage, updatedImage.getRepositoryId());
            cachedImages.put(updatedImage.getKey().getRepositoryKey(), images);
        }
    }

    public List<Image> getAll(RepositoryKey repositoryKey) {

        if (cachedImages.containsKey(repositoryKey)) {
            return cachedImages.get(repositoryKey).stream().map(Image::copyOf).collect(Collectors.toList());
        }

        return new ArrayList<>();
    }

    public Image get(ImageKey imageKey) {

        final RepositoryKey repositoryKey = imageKey.getRepositoryKey();

        if (cachedImages.containsKey(repositoryKey)) {
            return cachedImages.get(repositoryKey).stream().filter(i -> i.getName().equals(imageKey.getName())).findFirst().orElse(null);
        }

        return null;
    }

    public void remove(ImageKey imageKey) {

        final RepositoryKey repositoryKey = imageKey.getRepositoryKey();

        if (cachedImages.containsKey(repositoryKey)) {
            cachedImages.get(repositoryKey).removeIf(image -> image.getKey().equals(imageKey));
        }
    }
}
