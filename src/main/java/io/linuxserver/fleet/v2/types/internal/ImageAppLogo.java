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

package io.linuxserver.fleet.v2.types.internal;

import io.linuxserver.fleet.v2.key.ImageKey;

import java.io.InputStream;

public class ImageAppLogo {

    private final ImageKey    imageKey;
    private final InputStream rawDataStream;
    private final String      mimeType;
    private final String      logoName;
    private final long        logoSize;
    private final String      fileExtension;

    public ImageAppLogo(final ImageKey imageKey,
                        final InputStream rawDataStream,
                        final String mimeType,
                        final String logoName,
                        final long logoSize,
                        final String fileExtension) {

        this.imageKey      = imageKey;
        this.rawDataStream = rawDataStream;
        this.mimeType      = mimeType;
        this.logoName      = logoName;
        this.logoSize      = logoSize;
        this.fileExtension = fileExtension;
    }

    public final ImageKey getImageKey() {
        return imageKey;
    }

    public final InputStream getRawDataStream() {
        return rawDataStream;
    }

    public final String getMimeType() {
        return mimeType;
    }

    public final String getLogoName() {
        return logoName;
    }

    public final long getLogoSize() {
        return logoSize;
    }

    public final String getFileExtension() {
        return fileExtension;
    }
}
