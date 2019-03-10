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

import java.util.List;

public class DockerHubV2ScanResult<T> {

    @JsonProperty("count")
    private int count;

    @JsonProperty("next")
    private String next;

    @JsonProperty("previous")
    private String previous;

    @JsonProperty("results")
    private List<T> results;

    public final int getCount() {
        return count;
    }

    public final String getNext() {
        return next;
    }

    public final String getPrevious() {
        return previous;
    }

    public final List<T> getResults() {
        return results;
    }
}
