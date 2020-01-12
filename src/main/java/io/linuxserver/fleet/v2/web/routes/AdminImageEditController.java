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
import io.linuxserver.fleet.v2.key.ImageKey;
import io.linuxserver.fleet.v2.service.ImageService;
import io.linuxserver.fleet.v2.types.docker.DockerCapability;
import io.linuxserver.fleet.v2.web.PageModelSpec;
import io.linuxserver.fleet.v2.web.request.ImageTemplateUpdateFields;

import java.util.List;
import java.util.Map;

public class AdminImageEditController extends AbstractPageHandler {

    private ImageService imageService;

    public AdminImageEditController(final FleetAppController controller) {
        super(controller);
        imageService = controller.getImageService();
    }

    @Override
    protected PageModelSpec handlePageLoad(final Context ctx) {

        final String imageKeyParam = ctx.queryParam("imageKey");
        if (null != imageKeyParam) {

            final PageModelSpec modelSpec = new PageModelSpec("views/pages/admin/image-edit.ftl");
            modelSpec.addModelAttribute("image", imageService.getImage(ImageKey.parse(imageKeyParam)));
            modelSpec.addModelAttribute("containerCapabilities", DockerCapability.values());
            return modelSpec;

        } else {
            return new PageModelSpec("views/pages/not-found.ftl");
        }
    }

    @Override
    protected PageModelSpec handleFormSubmission(final Context ctx) {

        final String                    imageKey       = ctx.formParam("imageKey", String.class).get();
        final Map<String, List<String>> templateParams = ctx.formParamMap();

        getLogger().info("{}", ctx.formParamMap());
        //imageService.updateImageTemplate(imageKey, new ImageTemplateUpdateFields(templateParams));
        return null;
    }
}
