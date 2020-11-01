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

import io.linuxserver.fleet.v2.client.docker.AbstractDockerApiClient;
import io.linuxserver.fleet.v2.client.docker.github.model.GitHubImage;
import io.linuxserver.fleet.v2.client.docker.github.model.GitHubTag;

import java.util.List;

public class GitHubContainerRegistryClient extends AbstractDockerApiClient<GitHubImage, GitHubTag, GitHubImageConverter, GitHubTagConverter> {

    public GitHubContainerRegistryClient() {
        super(new GitHubImageConverter(), new GitHubTagConverter());
    }

    @Override
    protected final GitHubImage fetchImageFromApi(String imageName) {
        return null;
    }

    @Override
    protected final List<GitHubImage> fetchAllImagesFromApi(String repositoryName) {
        return null;
    }

    @Override
    protected final List<GitHubTag> fetchTagsFromApi(String imageName) {
        return null;
    }

    @Override
    public final boolean isRepositoryValid(String repositoryName) {
        return false;
    }
}
