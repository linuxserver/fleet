/*
 * Copyright (c)  2021 LinuxServer.io
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
package io.linuxserver.fleet.v2.types.api.external.templates;

public class ApiVolumeTemplate {

    private final String  containerPath;
    private final boolean readonly;
    private final String  description;

    public ApiVolumeTemplate(final String containerPath, final boolean readonly, final String description) {
        this.containerPath = containerPath;
        this.readonly = readonly;
        this.description = description;
    }

    public String getContainerPath() {
        return containerPath;
    }

    public boolean isReadonly() {
        return readonly;
    }

    public String getDescription() {
        return description;
    }
}
