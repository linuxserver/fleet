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

import io.linuxserver.fleet.cache.ImageCache;
import io.linuxserver.fleet.db.dao.ImageDAO;
import io.linuxserver.fleet.db.query.InsertUpdateResult;
import io.linuxserver.fleet.db.query.InsertUpdateStatus;
import io.linuxserver.fleet.exception.SaveException;
import io.linuxserver.fleet.model.internal.Image;
import io.linuxserver.fleet.model.internal.ImagePullStat;
import io.linuxserver.fleet.model.key.ImageKey;
import io.linuxserver.fleet.model.key.RepositoryKey;

import java.util.List;

public class ImageDelegate {

    private final ImageCache    imageCache;
    private final ImageDAO      imageDAO;

    public ImageDelegate(ImageDAO imageDAO) {

        this.imageDAO   = imageDAO;
        this.imageCache = new ImageCache();
    }

    /**
     * <p>
     * Performs a look-up to see if an image exists in the persisted database which matches
     * the given repository and image name. This is used to determine if, when scanning, Fleet
     * already has the image stored against the repository.
     * </p>
     */
    public Image findImageByRepositoryAndImageName(final ImageKey imageKey) {

        Image cachedImage = imageCache.get(imageKey);

        if (null == cachedImage) {

            Image image = imageDAO.findImageByRepositoryAndImageName(imageKey);

            if (null != image) {
                imageCache.updateCache(image);
            }

            return image;
        }

        return cachedImage;
    }

    public Image fetchImage(ImageKey imageKey) {
        return imageDAO.fetchImage(imageKey);
    }

    public List<Image> fetchImagesByRepository(final RepositoryKey repositoryKey) {

        List<Image> cachedImages = imageCache.getAll(repositoryKey);
        if (cachedImages.isEmpty()) {

            List<Image> images = imageDAO.fetchImagesByRepository(repositoryKey).getResults();
            images.forEach(imageCache::updateCache);

            return images;
        }

        return cachedImages;
    }

    public void removeImage(ImageKey imageKey) {

        Image existingImage = imageDAO.fetchImage(imageKey);
        if (null != existingImage) {

            imageDAO.removeImage(imageKey);
            imageCache.remove(imageKey);
        }
    }

    public Image saveImage(Image image) throws SaveException {

        InsertUpdateResult<Image> result = imageDAO.saveImage(image);

        if (result.getStatus() == InsertUpdateStatus.OK) {

            Image updatedImage = result.getResult();

            imageCache.updateCache(updatedImage);
            return result.getResult();
        }

        throw new SaveException(result.getStatusMessage());
    }

    public List<ImagePullStat> fetchImagePullHistory(ImageKey imageKey, ImagePullStat.GroupMode groupMode) {
        return imageDAO.fetchImagePullHistory(imageKey, groupMode);
    }
}
