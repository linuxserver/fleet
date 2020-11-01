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

package io.linuxserver.fleet.v2.client.docker.github;

import io.linuxserver.fleet.v2.client.docker.converter.AbstractDockerResponseConverter;
import io.linuxserver.fleet.v2.client.docker.github.model.GitHubImage;
import io.linuxserver.fleet.v2.types.docker.DockerImage;

public class GitHubImageConverter extends AbstractDockerResponseConverter<GitHubImage, DockerImage> {

    @Override
    protected final DockerImage doPlainConvert(final GitHubImage dockerApiImage) {
        return null;
    }

    @Override
    public final Class<GitHubImage> getConverterClass() {
        return GitHubImage.class;
    }
}
