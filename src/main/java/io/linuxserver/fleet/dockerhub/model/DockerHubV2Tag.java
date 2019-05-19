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

package io.linuxserver.fleet.dockerhub.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DockerHubV2Tag {

    @JsonProperty("name")
    private String name;

    @JsonProperty("full_size")
    private long fullSize;

    public String getName() {
        return name;
    }

    public long getFullSize() {
        return fullSize;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object other) {

        if (!(other instanceof DockerHubV2Tag)) {
            return false;
        }

        if (other == this) {
            return true;
        }

        return name.equals(((DockerHubV2Tag) other).name);
    }
}
