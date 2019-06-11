package io.linuxserver.fleet.db.dao;

import io.linuxserver.fleet.db.query.InsertUpdateResult;
import io.linuxserver.fleet.db.query.LimitedResult;
import io.linuxserver.fleet.model.internal.Image;
import io.linuxserver.fleet.model.internal.ImagePullStat;

import java.util.List;

public interface ImageDAO {

    Image findImageByRepositoryAndImageName(int repositoryId, String imageName);

    Image fetchImage(Integer id);

    LimitedResult<Image> fetchImagesByRepository(int repositoryId);

    InsertUpdateResult<Image> saveImage(Image image);

    void removeImage(Integer id);

    List<ImagePullStat> fetchImagePullHistory(Integer imageId, ImagePullStat.GroupMode groupMode);
}
