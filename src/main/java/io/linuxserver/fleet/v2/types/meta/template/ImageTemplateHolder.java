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

package io.linuxserver.fleet.v2.types.meta.template;

import io.linuxserver.fleet.v2.types.docker.DockerCapability;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class ImageTemplateHolder {

    // Mappings
    private final Set<PortTemplateItem>        ports       = new TreeSet<>();
    private final Set<VolumeTemplateItem>      volumes     = new TreeSet<>();
    private final Set<EnvironmentTemplateItem> environment = new TreeSet<>();
    private final Set<DeviceTemplateItem>      devices     = new TreeSet<>();

    // Misc
    private final Set<DockerCapability>   capabilities          = new TreeSet<>();
    private final AtomicReference<String> restartPolicy         = new AtomicReference<>();
    private final AtomicBoolean           hostNetworkingEnabled = new AtomicBoolean();
    private final AtomicBoolean           privilegedMode        = new AtomicBoolean();

    public final boolean hasCapability(final DockerCapability capability) {
        return capabilities.contains(capability);
    }

    public final void addPort(final PortTemplateItem port) {
        ports.add(port);
    }

    public final void addVolume(final VolumeTemplateItem volume) {
        volumes.add(volume);
    }

    public final void addEnvironment(final EnvironmentTemplateItem env) {
        environment.add(env);
    }

    public final void addDevice(final DeviceTemplateItem device) {
        devices.add(device);
    }

    public final List<PortTemplateItem> getPorts() {
        return new ArrayList<>(ports);
    }

    public final List<VolumeTemplateItem> getVolumes() {
        return new ArrayList<>(volumes);
    }

    public final List<EnvironmentTemplateItem> getEnv() {
        return new ArrayList<>(environment);
    }

    public final List<DeviceTemplateItem> getDevices() {
        return new ArrayList<>(devices);
    }
}
