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

package io.linuxserver.fleet.v2.types.api;

import io.linuxserver.fleet.v2.types.Image;

public class ApiImageWrapper extends AbstractApiWrapper<Image> {

    public ApiImageWrapper(final Image originalObject) {
        super(originalObject);
    }

    public final String getName() {
        return getOriginalObject().getName();
    }

    public final String getVersionMask() {
        return getOriginalObject().getVersionMask();
    }

    public final boolean isSyncEnabled() {
        return getOriginalObject().isSyncEnabled();
    }

    public final boolean isHidden() {
        return getOriginalObject().isHidden();
    }

    public final boolean isDeprecated() {
        return getOriginalObject().isDeprecated();
    }

    public final boolean isStable() {
        return getOriginalObject().isStable();
    }
}
