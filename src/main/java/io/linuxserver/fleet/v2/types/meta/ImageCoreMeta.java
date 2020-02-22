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

package io.linuxserver.fleet.v2.types.meta;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class ImageCoreMeta {

    private final String           appImagePath;
    private final String           baseImage;
    private final String           category;
    private final Set<ExternalUrl> externalUrls;

    public ImageCoreMeta(final String appImagePath,
                         final String baseImage,
                         final String category) {

        this.appImagePath = appImagePath;
        this.baseImage    = baseImage;
        this.category     = category;
        this.externalUrls = new TreeSet<>();
    }

    public final void enrichOtherWithExternalUrls(final ImageCoreMeta other) {
        externalUrls.forEach(other::addExternalUrl);
    }

    public final void addExternalUrl(final ExternalUrl externalUrl) {

        final boolean added = externalUrls.add(externalUrl);
        if (!added) {
            throw new IllegalArgumentException("External Url already present: " + externalUrl);
        }
    }

    public final void removeExternalUrl(final ExternalUrlKey externalUrlKey) {
        externalUrls.removeIf(url -> url.getKey().equals(externalUrlKey));
    }

    public final String getAppImagePath() {
        return appImagePath;
    }

    public final String getBaseImage() {
        return baseImage;
    }

    public final String getCategory() {
        return category;
    }

    public final List<ExternalUrl> getExternalUrls() {
        return new ArrayList<>(externalUrls);
    }
}
