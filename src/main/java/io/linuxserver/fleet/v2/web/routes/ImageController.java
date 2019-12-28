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

package io.linuxserver.fleet.v2.web.routes;

import io.javalin.http.Context;
import io.linuxserver.fleet.core.FleetAppController;
import io.linuxserver.fleet.v2.key.ImageLookupKey;
import io.linuxserver.fleet.v2.service.RepositoryService;
import io.linuxserver.fleet.v2.web.PageModelSpec;

public class ImageController extends AbstractPageHandler {

    private final RepositoryService repositoryService;

    public ImageController(final FleetAppController controller) {
        super(controller);
        repositoryService = controller.getRepositoryService();
    }

    @Override
    protected PageModelSpec handlePageLoad(final Context ctx) {

        final String imageLookupParam = ctx.queryParam("name");
        if (null != imageLookupParam) {

            final PageModelSpec modelSpec = new PageModelSpec("views/pages/image.ftl");
            modelSpec.addModelAttribute("image", repositoryService.lookupImage(new ImageLookupKey(imageLookupParam)));
            return modelSpec;

        } else {
            return new PageModelSpec("views/pages/not-found.ftl");
        }
    }

    @Override
    protected PageModelSpec handleFormSubmission(final Context ctx) {
        return new PageModelSpec("views/pages/unsupported.ftl");
    }
}
