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

public class ImageTemplateHolder {

    // Mappings
    private final Set<PortTemplateItem>        ports       = new TreeSet<>();
    private final Set<VolumeTemplateItem>      volumes     = new TreeSet<>();
    private final Set<EnvironmentTemplateItem> environment = new TreeSet<>();
    private final Set<DeviceTemplateItem>      devices     = new TreeSet<>();

    // Misc
    private final Set<DockerCapability> capabilities = new TreeSet<>();

    private final String  registryUrl;
    private final String  restartPolicy;
    private final boolean hostNetworkingEnabled;
    private final boolean privilegedMode;

    public ImageTemplateHolder(final String registryUrl,
                               final String restartPolicy,
                               final boolean hostNetworkingEnabled,
                               final boolean privilegedMode) {

        this.registryUrl           = registryUrl;
        this.restartPolicy         = restartPolicy;
        this.hostNetworkingEnabled = hostNetworkingEnabled;
        this.privilegedMode        = privilegedMode;
    }

    public final boolean hasCapability(final DockerCapability capability) {
        return capabilities.contains(capability);
    }

    public final void addCapability(final DockerCapability capability) {
        capabilities.add(capability);
    }

    public final List<DockerCapability> getCapabilities() {
        return new ArrayList<>(capabilities);
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

    public final String getRestartPolicy() {
        return restartPolicy;
    }

    public final boolean isHostNetworkingEnabled() {
        return hostNetworkingEnabled;
    }

    public final boolean isPrivilegedMode() {
        return privilegedMode;
    }

    public final String getRegistryUrl() {
        return registryUrl;
    }
}
