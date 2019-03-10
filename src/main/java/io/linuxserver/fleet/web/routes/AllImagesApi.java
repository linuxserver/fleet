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

package io.linuxserver.fleet.web.routes;

import io.linuxserver.fleet.delegate.ImageDelegate;
import io.linuxserver.fleet.delegate.RepositoryDelegate;
import io.linuxserver.fleet.model.*;
import io.linuxserver.fleet.model.api.ApiImage;
import io.linuxserver.fleet.model.api.ApiImagesWithTotalCount;
import io.linuxserver.fleet.model.api.ApiResponse;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AllImagesApi implements Route {

    private final RepositoryDelegate repositoryDelegate;
    private final ImageDelegate      imageDelegate;

    public AllImagesApi(RepositoryDelegate repositoryDelegate, ImageDelegate imageDelegate) {

        this.repositoryDelegate = repositoryDelegate;
        this.imageDelegate      = imageDelegate;
    }

    @Override
    public Object handle(Request request, Response response) {

        Map<String, List<ApiImage>> mappedImages = new HashMap<>();
        long totalCount = 0;

        List<Repository> repositories = repositoryDelegate.fetchAllRepositories();
        for (Repository repository : repositories) {

            if (repository.isSyncEnabled()) {

                List<Image> savedImages = imageDelegate.fetchImagesByRepository(repository.getId());
                List<ApiImage> apiImages = new ArrayList<>();

                for (Image savedImage : savedImages) {

                    if (!savedImage.isHidden()) {

                        totalCount += savedImage.getPullCount();
                        apiImages.add(ApiImage.fromImage(savedImage));
                    }
                }

                mappedImages.put(repository.getName(), apiImages);
            }
        }

        return new ApiResponse<>("OK", new ApiImagesWithTotalCount(totalCount, mappedImages));
    }
}
