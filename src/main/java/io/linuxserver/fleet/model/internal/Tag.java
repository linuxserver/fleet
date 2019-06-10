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

package io.linuxserver.fleet.model.internal;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.time.LocalDateTime;

public class Tag {

    public static final Tag NONE = new Tag("<Never Built>", "<Never Built>", null);

    private String          version;
    private String          maskedVersion;
    private LocalDateTime   buildDate;

    public Tag(String version, String maskedVersion, LocalDateTime buildDate) {

        this.version        = version;
        this.maskedVersion  = maskedVersion;

        if (null != buildDate) {
            this.buildDate  = LocalDateTime.of(buildDate.toLocalDate(), buildDate.toLocalTime());
        }
    }

    public String getVersion() {
        return version;
    }

    public String getMaskedVersion() {
        return maskedVersion;
    }

    public LocalDateTime getBuildDate() {

        if (null != buildDate) {
            return LocalDateTime.of(buildDate.toLocalDate(), buildDate.toLocalTime());
        }

        return null;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
