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

package io.linuxserver.fleet.v2.cache;

import io.linuxserver.fleet.v2.key.ImageKey;
import io.linuxserver.fleet.v2.key.RepositoryKey;
import io.linuxserver.fleet.v2.types.Image;
import io.linuxserver.fleet.v2.types.Repository;

public class RepositoryCache extends AbstractItemCache<RepositoryKey, Repository> {

    public final Image findImage(final ImageKey imageKey) {

        if (isItemCached(imageKey.getRepositoryKey())) {

            final Repository repository = findItem(imageKey.getRepositoryKey());
            for (Image image : repository.getImages()) {

                if (imageKey.equals(image.getKey())) {
                    return image;
                }
            }
        }

        return null;
    }
}
