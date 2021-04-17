/*
 * Copyright (c)  2021 LinuxServer.io
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
package io.linuxserver.fleet.v2.types.api.external.templates;

import java.util.ArrayList;
import java.util.List;

public class ApiTemplateHolder {

    private final List<ApiPortTemplate>   ports                = new ArrayList<>();
    private final List<ApiVolumeTemplate> volumes              = new ArrayList<>();
    private final List<ApiEnvTemplate>    environmentVariables = new ArrayList<>();
    private final List<ApiDeviceTemplate> devices              = new ArrayList<>();
    private final List<String>            capabilities         = new ArrayList<>();

    public boolean hostNetwork;
    public boolean privileged;

    public ApiTemplateHolder(final boolean hostNetwork, final boolean privileged) {
        this.hostNetwork = hostNetwork;
        this.privileged = privileged;
    }

    public final void addCapability(final String cap) {
        capabilities.add(cap);
    }

    public final void addDevice(final ApiDeviceTemplate deviceTemplate) {
        devices.add(deviceTemplate);
    }

    public final void addEnv(final ApiEnvTemplate envTemplate) {
        environmentVariables.add(envTemplate);
    }

    public final void addPort(final ApiPortTemplate portTemplate) {
        ports.add(portTemplate);
    }

    public final void addVolume(final ApiVolumeTemplate volumeTemplate) {
        volumes.add(volumeTemplate);
    }

    public List<String> getCapabilities() {
        return capabilities;
    }

    public List<ApiDeviceTemplate> getDevices() {
        return devices;
    }

    public List<ApiEnvTemplate> getEnvironmentVariables() {
        return environmentVariables;
    }

    public List<ApiPortTemplate> getPorts() {
        return ports;
    }

    public List<ApiVolumeTemplate> getVolumes() {
        return volumes;
    }

    public boolean isHostNetwork() {
        return hostNetwork;
    }

    public boolean isPrivileged() {
        return privileged;
    }
}
