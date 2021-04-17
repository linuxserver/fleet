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
import io.linuxserver.fleet.v2.types.api.external.ExternalApiImage;
import io.linuxserver.fleet.v2.types.api.external.ExternalApiResponse;
import io.linuxserver.fleet.v2.types.api.external.templates.*;
import io.linuxserver.fleet.v2.types.docker.DockerCapability;
import io.linuxserver.fleet.v2.types.meta.template.*;
import io.linuxserver.fleet.v2.web.ApiException;

import java.util.List;

public class LegacyExternalApiController extends AbstractAppService {

    public LegacyExternalApiController(final FleetAppController controller) {
        super(controller);
    }

    public final void fetchAllImages(final Context ctx) {

        final boolean verboseOutput = ctx.queryParam("verbose", Boolean.class, "false").get();

        try {

            final AllImagesExternalApiResponse responseData = new AllImagesExternalApiResponse();

            final List<Repository> repositories = getController().getImageService().getAllShownRepositories();
            for (Repository repository : repositories) {

                for (Image image : repository.getImages()) {

                    final ExternalApiImage apiImage = responseData.addImage(image.getRepositoryName(),
                                                                            image.getName(),
                                                                            image.getPullCount(),
                                                                            image.getLatestTag().getVersion(),
                                                                            image.getMetaData().getCategory(),
                                                                            image.isStable());
                    if (verboseOutput) {
                        enrichImageWithTemplateData(apiImage, image.getMetaData().getTemplates());
                    }
                }
            }

            ctx.json(new ExternalApiResponse<>(ExternalApiResponse.ApiStatus.OK, responseData));

        } catch (IllegalArgumentException e) {
            throw new ApiException(e.getMessage(), e);
        }
    }

    private void enrichImageWithTemplateData(final ExternalApiImage apiImage, final ImageTemplateHolder templateHolder) {

        final ApiTemplateHolder apiTemplateHolder = new ApiTemplateHolder(templateHolder.isHostNetworkingEnabled(),
                                                                          templateHolder.isPrivilegedMode());

        for (PortTemplateItem port : templateHolder.getPorts()) {
            apiTemplateHolder.addPort(new ApiPortTemplate(port.getPort(), port.getProtocol(), port.getDescription()));
        }

        for (VolumeTemplateItem volume : templateHolder.getVolumes()) {
            apiTemplateHolder.addVolume(new ApiVolumeTemplate(volume.getVolume(), volume.isReadonly(), volume.getDescription()));
        }

        for (EnvironmentTemplateItem env : templateHolder.getEnv()) {
            apiTemplateHolder.addEnv(new ApiEnvTemplate(env.getEnv(), env.getExampleValue(), env.getDescription()));
        }

        for (DeviceTemplateItem device : templateHolder.getDevices()) {
            apiTemplateHolder.addDevice(new ApiDeviceTemplate(device.getDevice(), device.getDescription()));
        }

        for (DockerCapability capability : templateHolder.getCapabilities()) {
            apiTemplateHolder.addCapability(capability.name());
        }

        apiImage.setTemplateSpec(apiTemplateHolder);
    }
}
