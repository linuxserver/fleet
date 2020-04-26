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

public class ImageCountData {

    private final long pullCount;
    private final int  starCount;

    public ImageCountData(final long pullCount, final int starCount) {

        this.pullCount = pullCount;
        this.starCount = starCount;
    }

    public final long getPullCount() {
        return pullCount;
    }

    public final int getStarCount() {
        return starCount;
    }
}
