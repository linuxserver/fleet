/*
 * Copyright (c)  2020 LinuxServer.io
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

package io.linuxserver.fleet.v2.types.api.external;

import io.linuxserver.fleet.v2.types.api.external.templates.ApiTemplateHolder;

public class ExternalApiImage {

    private final String  name;
    private final long    pullCount;
    private final String  version;
    private final String  category;
    private final boolean stable;

    private ApiTemplateHolder templateSpec;

    public ExternalApiImage(final String name, final long pullCount, final String version, final String category, final boolean stable) {
        this.name      = name;
        this.pullCount = pullCount;
        this.version   = version;
        this.category  = category;
        this.stable    = stable;
    }

    public final String getName() {
        return name;
    }

    public final long getPullCount() {
        return pullCount;
    }

    public final String getVersion() {
        return version;
    }

    public final String getCategory() {
        return category;
    }

    public final boolean isStable() {
        return stable;
    }

    public final void setTemplateSpec(final ApiTemplateHolder templateHolder) {
        this.templateSpec = templateHolder;
    }

    public final ApiTemplateHolder getTemplateSpec() {
        return templateSpec;
    }
}
