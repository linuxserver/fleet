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
import io.javalin.http.UploadedFile;
import io.linuxserver.fleet.core.FleetAppController;
import io.linuxserver.fleet.v2.key.ImageKey;
import io.linuxserver.fleet.v2.service.ImageService;
import io.linuxserver.fleet.v2.types.docker.DockerCapability;
import io.linuxserver.fleet.v2.types.internal.ImageAppLogo;
import io.linuxserver.fleet.v2.types.internal.ImageGeneralInfoUpdateRequest;
import io.linuxserver.fleet.v2.types.internal.ImageTemplateRequest;
import io.linuxserver.fleet.v2.web.PageModelSpec;

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

        final String updateType = ctx.formParam("updateType", String.class).get();
        final ImageKey imageKey = ctx.formParam("imageKey",   ImageKey.class).get();

        switch (updateType) {

            case "GENERAL":
                handleGeneralUpdate(ctx, imageKey);
                break;

            case "TEMPLATE":
                handleTemplateUpdate(ctx, imageKey);
                break;

            default:
                throw new IllegalArgumentException("Unknown updateType provided: " + updateType);
        }

        return new PageModelSpec("redirect:/admin/image?imageKey=" + imageKey);
    }

    private void handleGeneralUpdate(final Context ctx, final ImageKey imageKey) {

        if (!ctx.isMultipartFormData()) {
            throw new IllegalArgumentException("Form submission must be form/multipart");
        }

        imageService.updateImageGeneralInfo(imageKey, makeInfoRequest(imageKey, ctx));
    }

    private void handleTemplateUpdate(final Context ctx, final ImageKey imageKey) {
        imageService.updateImageTemplate(imageKey, new ImageTemplateRequest(ctx.formParamMap()));
    }

    private ImageGeneralInfoUpdateRequest makeInfoRequest(final ImageKey imageKey, final Context ctx) {

        return new ImageGeneralInfoUpdateRequest(ctx.formParamMap(),
                                                 makeImageLogoIfPresent(imageKey, ctx.uploadedFile("ImageAppLogo")));
    }

    private ImageAppLogo makeImageLogoIfPresent(final ImageKey imageKey, final UploadedFile uploadedFile) {

        if (null != uploadedFile) {

            return new ImageAppLogo(imageKey,
                                    uploadedFile.getContent(),
                                    uploadedFile.getContentType(),
                                    uploadedFile.getFilename(),
                                    uploadedFile.getSize(),
                                    uploadedFile.getExtension());
        }

        return null;
    }
}
