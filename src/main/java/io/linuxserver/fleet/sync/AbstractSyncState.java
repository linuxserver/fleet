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

package io.linuxserver.fleet.sync;

import io.linuxserver.fleet.delegate.DockerHubDelegate;
import io.linuxserver.fleet.delegate.ImageDelegate;
import io.linuxserver.fleet.delegate.RepositoryDelegate;

abstract class AbstractSyncState {

    private final ImageDelegate imageDelegate;
    private final RepositoryDelegate repositoryDelegate;
    private final DockerHubDelegate dockerHubDelegate;

    AbstractSyncState(ImageDelegate imageDelegate, RepositoryDelegate repositoryDelegate, DockerHubDelegate dockerHubDelegate) {

        this.imageDelegate = imageDelegate;
        this.repositoryDelegate = repositoryDelegate;
        this.dockerHubDelegate = dockerHubDelegate;
    }

    ImageDelegate getImageDelegate() {
        return imageDelegate;
    }

    RepositoryDelegate getRepositoryDelegate() {
        return repositoryDelegate;
    }

    DockerHubDelegate getDockerHubDelegate() {
        return dockerHubDelegate;
    }
}
