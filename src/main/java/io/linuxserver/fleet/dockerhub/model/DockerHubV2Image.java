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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class DockerHubV2Image {

    @JsonProperty("user")
    private String user;

    @JsonProperty("name")
    private String name;

    @JsonProperty("namespace")
    private String namespace;

    @JsonProperty("description")
    private String description;

    @JsonProperty("star_count")
    private int starCount;

    @JsonProperty("pull_count")
    private long pullCount;

    @JsonProperty("last_updated")
    private String lastUpdated;

    public final String getUser() {
        return user;
    }

    public final String getName() {
        return name;
    }

    public final String getNamespace() {
        return namespace;
    }

    public String getDescription() {
        return description;
    }

    public final int getStarCount() {
        return starCount;
    }

    public final long getPullCount() {
        return pullCount;
    }

    public final String getLastUpdated() {
        return lastUpdated;
    }
}
