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

package io.linuxserver.fleet.core.config;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class VersionProperties {

    private final Version       version;
    private final String        buildUser;
    private final LocalDateTime buildDate;
    private final String        buildPlatform;

    public VersionProperties(final String version,
                             final String buildUser,
                             final String buildDate,
                             final String buildPlatform) {

        this.version       = new Version(version);
        this.buildUser     = buildUser;
        this.buildDate     = LocalDateTime.parse(buildDate, DateTimeFormatter.ISO_DATE_TIME);
        this.buildPlatform = buildPlatform;
    }

    public final Version getVersion() {
        return version;
    }

    public final String getBuildUser() {
        return buildUser;
    }

    public final LocalDateTime getBuildDate() {
        return buildDate;
    }

    public final String getBuildPlatform() {
        return buildPlatform;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
