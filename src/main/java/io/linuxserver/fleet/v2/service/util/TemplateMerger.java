/*
 * Copyright (c)  2020 LinuxServer.io
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

package io.linuxserver.fleet.v2.service.util;

import io.linuxserver.fleet.v2.types.Image;
import io.linuxserver.fleet.v2.types.docker.DockerCapability;
import io.linuxserver.fleet.v2.types.internal.ImageTemplateRequest;
import io.linuxserver.fleet.v2.types.meta.template.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TemplateMerger {

    private static final Logger LOGGER = LoggerFactory.getLogger(TemplateMerger.class);

    public final Image mergeTemplateRequestIntoImage(final Image image, final ImageTemplateRequest templateRequest) {

        final ImageTemplateHolder templateHolder = makeTemplateHolder(templateRequest);
        addMisc(templateRequest, templateHolder);
        addPorts(templateRequest, templateHolder);
        addVolumes(templateRequest, templateHolder);
        addEnvironment(templateRequest, templateHolder);
        addDevices(templateRequest, templateHolder);

        final Image cloned = image.cloneWithMetaData(image.getMetaData().cloneWithTemplate(templateHolder));
        return cloned;
    }

    private ImageTemplateHolder makeTemplateHolder(final ImageTemplateRequest templateRequest) {

        return new ImageTemplateHolder(templateRequest.getRegistryUrl(),
                                       templateRequest.getRestartPolicy(),
                                       templateRequest.isHostNetworkEnabled(),
                                       templateRequest.isPrivilegedMode());
    }

    private void addPorts(final ImageTemplateRequest request, final ImageTemplateHolder holder) {

        for (ImageTemplateRequest.TemplateItem<String> port : request.getPorts()) {

            holder.addPort(new PortTemplateItem(Integer.parseInt(port.getName()),
                                                port.getDescription(),
                                                PortTemplateItem.Protocol.fromName(port.getSecondaryField())));
        }
    }

    private void addVolumes(final ImageTemplateRequest request, final ImageTemplateHolder holder) {

        for (ImageTemplateRequest.TemplateItem<Boolean> volume : request.getVolumes()) {

            holder.addVolume(new VolumeTemplateItem(volume.getName(),
                                                    volume.getDescription(),
                                                    volume.getSecondaryField()));
        }
    }

    private void addEnvironment(final ImageTemplateRequest request, final ImageTemplateHolder holder) {

        for (ImageTemplateRequest.TemplateItem<Void> env : request.getEnvironment()) {
            holder.addEnvironment(new EnvironmentTemplateItem(env.getName(), env.getDescription()));
        }
    }
    private void addDevices(final ImageTemplateRequest request, final ImageTemplateHolder holder) {

        for (ImageTemplateRequest.TemplateItem<Void> device : request.getDevices()) {
            holder.addDevice(new DeviceTemplateItem(device.getName(), device.getDescription()));
        }
    }


    private void addMisc(final ImageTemplateRequest request, final ImageTemplateHolder holder) {

        if (null != request.getCapabilities()) {
            for (String capability : request.getCapabilities()) {

                try {
                    holder.addCapability(DockerCapability.valueOf(capability));
                } catch (IllegalArgumentException e) {
                    LOGGER.warn("Attempted to add unknown capability {}", capability);
                }
            }
        }
    }
}
