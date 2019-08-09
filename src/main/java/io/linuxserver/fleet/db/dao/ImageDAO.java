package io.linuxserver.fleet.db.dao;

import io.linuxserver.fleet.db.query.InsertUpdateResult;
import io.linuxserver.fleet.db.query.LimitedResult;
import io.linuxserver.fleet.model.internal.Image;
import io.linuxserver.fleet.model.internal.ImagePullStat;
import io.linuxserver.fleet.model.key.ImageKey;
import io.linuxserver.fleet.model.key.RepositoryKey;

import java.util.List;

public interface ImageDAO {

    Image findImageByRepositoryAndImageName(ImageKey imageKey);

    Image fetchImage(ImageKey imageKey);

    LimitedResult<Image> fetchImagesByRepository(RepositoryKey repositoryKey);

    InsertUpdateResult<Image> saveImage(Image image);

    void removeImage(ImageKey imageKey);

    List<ImagePullStat> fetchImagePullHistory(ImageKey imageKey, ImagePullStat.GroupMode groupMode);
}
