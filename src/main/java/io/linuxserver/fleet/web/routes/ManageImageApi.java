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
import io.linuxserver.fleet.model.api.ApiResponse;
import io.linuxserver.fleet.model.api.FleetApiException;
import spark.Request;
import spark.Response;
import spark.Route;

public class ManageImageApi implements Route {

    private final ImageDelegate imageDelegate;

    public ManageImageApi(ImageDelegate imageDelegate) {
        this.imageDelegate = imageDelegate;
    }

    @Override
    public Object handle(Request request, Response response) {

        try {

            int imageId = Integer.parseInt(request.queryParams("imageId"));

            Image image = imageDelegate.fetchImage(imageId);
            if (null == image) {

                response.status(404);
                response.header("Content-Type", "application/json");
                return new ApiResponse<>("Error", "Image not found.");
            }

            Action action = Action.valueOf(request.queryParams("action"));
            switch (action) {

                case SHOW:
                case HIDE:
                    image.withHidden(Action.HIDE.equals(action));
                    break;
                case STABLE:
                case UNSTABLE:
                    image.withUnstable(Action.UNSTABLE.equals(action));
                    break;
                case MASK:
                    String versionMask = cleanParam(request.queryParams("versionMask"));
                    image.withVersionMask(versionMask);
                    break;
            }

            imageDelegate.saveImage(image);

            response.header("Content-Type", "application/json");
            return new ApiResponse<>("OK", "Image updated.");

        } catch (Exception e) {
            throw new FleetApiException(500, e.getMessage(), e);
        }
    }

    private String cleanParam(String param) {

        if ("".equalsIgnoreCase(param))
            return null;

        return param;
    }

    public enum Action {
        SHOW, HIDE, MASK, STABLE, UNSTABLE
    }
}
