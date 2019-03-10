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

package io.linuxserver.fleet.model.api;

import io.linuxserver.fleet.model.Image;

public class ApiImage {

    private String  name;
    private long    pullCount;
    private String  version;
    private boolean stable;

    public String getName() {
        return name;
    }

    public long getPullCount() {
        return pullCount;
    }

    public String getVersion() {
        return version;
    }

    public boolean isStable() {
        return stable;
    }

    public static ApiImage fromImage(Image image) {

        ApiImage apiImage = new ApiImage();

        apiImage.name       = image.getName();
        apiImage.pullCount  = image.getPullCount();
        apiImage.version    = image.getVersion();
        apiImage.stable     = !image.isUnstable();

        return apiImage;
    }
}
