/*
 * Copyright (c)  2019 LinuxServer.io
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

package io.linuxserver.fleet.v2.web.request.json;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UpdateImageSpecRequest {

    @JsonProperty
    private String imageKey;

    @JsonProperty
    private boolean syncEnabled;

    @JsonProperty
    private String  versionMask;

    @JsonProperty
    private boolean stable;

    @JsonProperty
    private boolean hidden;

    @JsonProperty
    private boolean deprecated;

    public final String getImageKey() {
        return imageKey;
    }

    public final boolean isSyncEnabled() {
        return syncEnabled;
    }

    public final String getVersionMask() {
        return versionMask;
    }

    public final boolean isStable() {
        return stable;
    }

    public final boolean isHidden() {
        return hidden;
    }

    public boolean isDeprecated() {
        return deprecated;
    }
}
