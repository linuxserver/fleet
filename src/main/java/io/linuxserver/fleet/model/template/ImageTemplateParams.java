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

package io.linuxserver.fleet.model.template;

import io.linuxserver.fleet.model.Image;

import java.util.ArrayList;
import java.util.List;

public class ImageTemplateParams {

    private final String imageName;
    private final String repositoryName;

    private String  url;
    private String  restartPolicy;
    private boolean hostNetwork;
    private boolean privileged;

    private final List<ImageTemplateEnvironment> environments;
    private final List<ImageTemplateExtraParam>  extra;
    private final List<ImageTemplateVolume>      volumes;
    private final List<ImageTemplatePort>        ports;

    public ImageTemplateParams(String repositoryName, String imageName) {

        this.repositoryName = repositoryName;
        this.imageName      = imageName;

        this.environments = new ArrayList<>();
        this.extra          = new ArrayList<>();
        this.volumes        = new ArrayList<>();
        this.ports          = new ArrayList<>();
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setRestartPolicy(String restartPolicy) {
        this.restartPolicy = restartPolicy;
    }

    public void setHostNetwork(boolean hostNetwork) {
        this.hostNetwork = hostNetwork;
    }

    public void setPrivileged(boolean privileged) {
        this.privileged = privileged;
    }

    public void withEnvironment(ImageTemplateEnvironment environment) {
        environments.add(environment);
    }

    public void withExtraParam(ImageTemplateExtraParam extraParam) {
        extra.add(extraParam);
    }

    public void withPort(ImageTemplatePort port) {
        ports.add(port);
    }

    public void withVolume(ImageTemplateVolume volume) {
        volumes.add(volume);
    }

    public String getRepositoryName() {
        return repositoryName;
    }

    public String getImageName() {
        return imageName;
    }

    public String getUrl() {
        return url;
    }

    public String getRestartPolicy() {
        return restartPolicy;
    }

    public boolean isHostNetwork() {
        return hostNetwork;
    }

    public boolean isPrivileged() {
        return privileged;
    }

    public List<ImageTemplateEnvironment> getEnvironments() {
        return environments;
    }

    public List<ImageTemplateExtraParam> getExtra() {
        return extra;
    }

    public List<ImageTemplateVolume> getVolumes() {
        return volumes;
    }

    public List<ImageTemplatePort> getPorts() {
        return ports;
    }
}
