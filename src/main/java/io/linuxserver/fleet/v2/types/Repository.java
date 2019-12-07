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

package io.linuxserver.fleet.v2.types;

import io.linuxserver.fleet.v2.key.AbstractHasKey;
import io.linuxserver.fleet.v2.key.RepositoryKey;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class Repository extends AbstractHasKey<RepositoryKey> {

    private final Set<Image> images;

    public Repository(final RepositoryKey key) {
        super(key);
        images = new TreeSet<>();
    }

    public final void addImage(final Image image) {
        images.add(image);
    }

    public final String getName() {
        return getKey().getName();
    }

    public final List<Image> getImages() {
        return new ArrayList<>(images);
    }
}
