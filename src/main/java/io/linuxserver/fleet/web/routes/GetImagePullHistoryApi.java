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
import io.linuxserver.fleet.model.Image;
import io.linuxserver.fleet.model.ImagePullStat;
import io.linuxserver.fleet.model.api.ApiImagePullHistory;
import io.linuxserver.fleet.model.api.ApiResponse;
import io.linuxserver.fleet.model.api.FleetApiException;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.List;

public class GetImagePullHistoryApi implements Route {

    private final ImageDelegate imageDelegate;

    public GetImagePullHistoryApi(ImageDelegate imageDelegate) {
        this.imageDelegate = imageDelegate;
    }

    @Override
    public Object handle(Request request, Response response) {

        String imageIdParam = request.queryParams("imageId");
        if (null == imageIdParam) {
            throw new FleetApiException(400, "Missing imageId param");
        }

        int imageId = Integer.parseInt(imageIdParam);
        List<ImagePullStat> imagePullStats = imageDelegate.fetchImagePullHistory(imageId, getGroupMode(request));

        Image image = imageDelegate.fetchImage(imageId);
        return new ApiResponse<>("OK", ApiImagePullHistory.fromPullStats(image, imagePullStats));
    }

    private ImagePullStat.GroupMode getGroupMode(Request request) {

        String groupMode = request.params("groupMode");
        return ImagePullStat.GroupMode.isValid(groupMode) ? ImagePullStat.GroupMode.valueOf(groupMode) : ImagePullStat.GroupMode.DAY;
    }
}
