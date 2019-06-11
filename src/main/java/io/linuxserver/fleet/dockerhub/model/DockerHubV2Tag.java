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

import java.util.Objects;

public class DockerHubV2Tag {

    @JsonProperty("name")
    private String name;

    @JsonProperty("full_size")
    private long fullSize;

    @JsonProperty("last_updated")
    private String lastUpdated;

    public String getName() {
        return name;
    }

    public long getFullSize() {
        return fullSize;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, fullSize);
    }

    @Override
    public boolean equals(Object other) {

        if (!(other instanceof DockerHubV2Tag)) {
            return false;
        }

        if (other == this) {
            return true;
        }

        DockerHubV2Tag otherTag = (DockerHubV2Tag) other;
        return name.equals(otherTag.name) && fullSize == otherTag.fullSize;
    }
}
