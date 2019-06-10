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

package io.linuxserver.fleet.model.internal;

import java.util.Collections;
import java.util.List;

public class RepositoryWithImages {

    private final Repository    repository;
    private final List<Image>   images;

    public RepositoryWithImages(Repository repository, List<Image> images) {

        this.repository = repository;
        this.images = Collections.unmodifiableList(images);
    }

    public Repository getRepository() {
        return repository;
    }

    public List<Image> getImages() {
        return images;
    }

    public boolean isEveryImageStable() {
        return images.stream().noneMatch(Image::isUnstable);
    }
}
