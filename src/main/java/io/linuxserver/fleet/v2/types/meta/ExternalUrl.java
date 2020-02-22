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

package io.linuxserver.fleet.v2.types.meta;

import io.linuxserver.fleet.v2.key.AbstractHasKey;

public class ExternalUrl extends AbstractHasKey<ExternalUrlKey> {

    private final ExternalUrlType type;
    private final String          name;
    private final String          absoluteUrl;

    public ExternalUrl(final ExternalUrlKey key,
                       final ExternalUrlType type,
                       final String name,
                       final String absoluteUrl) {
        super(key);
        this.type        = type;
        this.name        = name;
        this.absoluteUrl = absoluteUrl;
    }

    public final ExternalUrlType getType() {
        return type;
    }

    public final String getName() {
        return name;
    }

    public final String getAbsoluteUrl() {
        return absoluteUrl;
    }

    @Override
    public final String toString() {
        return type + "[" + name + "]{" + absoluteUrl + "}";
    }

    public enum ExternalUrlType {

        Support(    "life-ring", "Support information relating to setup, debugging, or other issues"),
        Application("server",    "Primary link to application source or marketing"),
        Donation(   "donate",    "Accepted donations"),
        Misc(       "link",      "Other external resource");

        private final String icon;
        private final String description;

        ExternalUrlType(final String icon,
                    final String description) {
            this.icon = icon;
            this.description = description;
        }

        public final String getIcon() {
            return icon;
        }

        public final String getDescription() {
            return description;
        }
    }
}
