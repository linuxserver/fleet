/*
 * Copyright (c)  2019 LinuxServer.io
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

package io.linuxserver.fleet.v2.web.routes;

import io.javalin.http.Context;
import io.linuxserver.fleet.core.FleetAppController;
import io.linuxserver.fleet.v2.service.AbstractAppService;
import io.linuxserver.fleet.v2.types.Image;
import io.linuxserver.fleet.v2.types.Repository;
import io.linuxserver.fleet.v2.types.api.external.AllImagesExternalApiResponse;
import io.linuxserver.fleet.v2.types.api.external.ExternalApiResponse;
import io.linuxserver.fleet.v2.web.ApiException;

import java.util.List;

public class LegacyExternalApiController extends AbstractAppService {

    public LegacyExternalApiController(final FleetAppController controller) {
        super(controller);
    }

    public final void fetchAllImages(final Context ctx) {

        try {

            final AllImagesExternalApiResponse responseData = new AllImagesExternalApiResponse();

            final List<Repository> repositories = getController().getImageService().getAllShownRepositories();
            for (Repository repository : repositories) {

                for (Image image : repository.getImages()) {

                    responseData.addImage(image.getRepositoryName(),
                                          image.getName(),
                                          image.getPullCount(),
                                          image.getLatestTag().getVersion(),
                                          image.isStable());
                }
            }

            ctx.json(new ExternalApiResponse<>(ExternalApiResponse.ApiStatus.OK, responseData));

        } catch (IllegalArgumentException e) {
            throw new ApiException(e.getMessage(), e);
        }
    }
}
