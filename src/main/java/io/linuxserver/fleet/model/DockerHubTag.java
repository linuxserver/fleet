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

package io.linuxserver.fleet.model;

public class DockerHubTag {

    private final String    name;
    private final long      size;
    private final String    buildDate;

    public DockerHubTag(String name, long size, String buildDate) {

        this.name = name;
        this.size = size;
        this.buildDate = buildDate;
    }

    public String getName() {
        return name;
    }

    public long getSize() {
        return size;
    }

    public String getBuildDate() {
        return buildDate;
    }
}
