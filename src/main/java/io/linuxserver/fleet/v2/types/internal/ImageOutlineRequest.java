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

package io.linuxserver.fleet.v2.types.internal;

import io.linuxserver.fleet.v2.key.RepositoryKey;

import java.time.LocalDateTime;

public class ImageOutlineRequest {

    private final RepositoryKey repositoryKey;
    private final String        imageName;
    private final String        imageDescription;
    private final LocalDateTime imageLastUpdated;

    public ImageOutlineRequest(final RepositoryKey repositoryKey,
                               final String imageName,
                               final String imageDescription,
                               final LocalDateTime imageLastUpdated) {

        this.repositoryKey    = repositoryKey;
        this.imageName        = imageName;
        this.imageDescription = imageDescription;
        this.imageLastUpdated = imageLastUpdated;
    }

    public final RepositoryKey getRepositoryKey() {
        return repositoryKey;
    }

    public final String getImageName() {
        return imageName;
    }

    public final String getImageDescription() {
        return imageDescription;
    }

    public final LocalDateTime getImageLastUpdated() {
        return imageLastUpdated;
    }
}
