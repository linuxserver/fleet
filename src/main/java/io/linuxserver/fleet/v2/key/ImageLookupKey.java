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

package io.linuxserver.fleet.v2.key;

import io.linuxserver.fleet.v2.types.Image;

public class ImageLookupKey extends AbstractLookupKey<Image> {

    private static final String KeyPattern = "^[^/]+/[^/]+$";

    private final String lookupRepositoryName;
    private final String lookupImageName;

    public ImageLookupKey(final String query) {
        super(query);

        if (query.matches(KeyPattern)) {

            final String[] names = query.split("/");
            lookupRepositoryName = names[0];
            lookupImageName      = names[1];

        } else {
            throw new IllegalArgumentException("Malformed lookup query for ImageLookupKey");
        }
    }

    @Override
    public final boolean isLookupKeyFor(final Image image) {

        if (null == image) {
            return false;
        }

        return image.getRepositoryName().equals(lookupRepositoryName) && image.getName().equals(lookupImageName);
    }
}
