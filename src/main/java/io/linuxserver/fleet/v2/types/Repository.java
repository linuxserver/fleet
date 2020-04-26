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

import io.linuxserver.fleet.v2.cache.ImageCache;
import io.linuxserver.fleet.v2.key.RepositoryKey;
import io.linuxserver.fleet.v2.types.meta.ItemSyncSpec;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Repository extends AbstractSyncItem<RepositoryKey, Repository> {

    private final ImageCache images;

    public Repository(final RepositoryKey key, final ItemSyncSpec syncSpec) {
        super(key, syncSpec);
        images = new ImageCache();
    }

    @Override
    public Repository cloneWithSyncSpec(final ItemSyncSpec syncSpec) {

        final Repository cloned = new Repository(getKey(), syncSpec);
        images.getAllItems().forEach(i -> addImage(i.cloneWithSyncSpec(i.getSpec())));

        return cloned;
    }

    public final void addImage(final Image image) {
        images.addItem(image);
    }

    public final String getName() {
        return getKey().getName();
    }

    public final List<Image> getImages() {

        final List<Image> imageList = new ArrayList<>(images.getAllItems());
        Collections.sort(imageList);
        return imageList;
    }

    public final long getTotalPulls() {

        long totalPulls = 0;
        for (Image image : getImages()) {
            totalPulls += image.getPullCount();
        }
        return totalPulls;
    }

    public final int getTotalStars() {

        int totalStars = 0;
        for (Image image : getImages()) {
            totalStars += image.getStarCount();
        }
        return totalStars;
    }

    @Override
    public final boolean isHidden() {
        return !isSyncEnabled();
    }

    @Override
    public final boolean isStable() {
        return true;
    }

    @Override
    public final boolean isDeprecated() {
        return false;
    }

    public final void removeImage(final Image image) {
        images.removeItem(image.getKey());
    }

    @Override
    public final String toString() {
        return getName() + "[nImages=" + images.size() + "]";
    }
}
