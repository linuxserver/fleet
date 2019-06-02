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

import io.linuxserver.fleet.model.Image;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ImageCache {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImageCache.class);

    private final Map<Integer, List<Image>> cachedImages;

    public ImageCache() {
        this.cachedImages = new HashMap<>();
    }

    public void updateCache(Image image) {

        Image updatedImage = Image.copyOf(image);

        if (cachedImages.containsKey(updatedImage.getRepositoryId())) {

            List<Image> images = cachedImages.get(updatedImage.getRepositoryId());
            int cachedImageLocation = images.indexOf(updatedImage);

            if (cachedImageLocation > -1) {

                LOGGER.info("updateCache({}) Updating existing cached image.", updatedImage.getName());
                images.set(cachedImageLocation, updatedImage);

            } else {

                LOGGER.info("updateCache({}) Adding image to cache", updatedImage.getName());
                images.add(updatedImage);
            }

        } else {

            List<Image> images = new ArrayList<>();
            images.add(updatedImage);

            LOGGER.info("updateCache({}:{}) Creating new cache for repository {}", updatedImage.getName(), updatedImage.getRepositoryId());
            cachedImages.put(updatedImage.getRepositoryId(), images);
        }
    }

    public List<Image> getAll(int repositoryId) {

        if (cachedImages.containsKey(repositoryId)) {
            return cachedImages.get(repositoryId).stream().map(Image::copyOf).collect(Collectors.toList());
        }

        return new ArrayList<>();
    }

    public Image get(int repositoryId, String imageName) {

        List<Image> images = cachedImages.get(repositoryId);
        return images.stream().filter(i -> i.getName().equals(imageName)).findFirst().orElse(null);
    }

    public void remove(Image image) {

        if (cachedImages.containsKey(image.getRepositoryId())) {

            List<Image> images = cachedImages.get(image.getRepositoryId());
            images.remove(image);
        }
    }
}
