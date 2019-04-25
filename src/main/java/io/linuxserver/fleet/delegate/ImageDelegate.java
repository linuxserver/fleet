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

import io.linuxserver.fleet.db.dao.ImageDAO;
import io.linuxserver.fleet.db.query.InsertUpdateResult;
import io.linuxserver.fleet.db.query.InsertUpdateStatus;
import io.linuxserver.fleet.exception.SaveException;
import io.linuxserver.fleet.model.Image;
import io.linuxserver.fleet.model.ImagePullStat;

import java.util.List;

public class ImageDelegate {

    private final ImageDAO imageDAO;

    public ImageDelegate(ImageDAO imageDAO) {
        this.imageDAO = imageDAO;
    }

    /**
     * <p>
     * Performs a look-up to see if an image exists in the persisted database which matches
     * the given repository and image name. This is used to determine if, when scanning, Fleet
     * already has the image stored against the repository.
     * </p>
     */
    public Image findImageByRepositoryAndImageName(int repositoryId, String imageName) {
        return imageDAO.findImageByRepositoryAndImageName(repositoryId, imageName);
    }

    public Image fetchImage(int id) {
        return imageDAO.fetchImage(id);
    }

    public List<Image> fetchImagesByRepository(int repositoryId) {
        return imageDAO.fetchImagesByRepository(repositoryId).getResults();
    }

    public void removeImage(Integer id) {
        imageDAO.removeImage(id);
    }

    public Image saveImage(Image image) throws SaveException {

        InsertUpdateResult<Image> result = imageDAO.saveImage(image);

        if (result.getStatus() == InsertUpdateStatus.OK)
            return result.getResult();

        throw new SaveException(result.getStatusMessage());
    }

    public List<ImagePullStat> fetchImagePullHistory(int id) {
        return fetchImagePullHistory(id, ImagePullStat.GroupMode.DAY);
    }

    public List<ImagePullStat> fetchImagePullHistory(int id, ImagePullStat.GroupMode groupMode) {
        return imageDAO.fetchImagePullHistory(id, groupMode);
    }
}
