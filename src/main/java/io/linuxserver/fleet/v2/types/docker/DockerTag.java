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

package io.linuxserver.fleet.v2.types.docker;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DockerTag {

    private final String        name;
    private final long          size;
    private final LocalDateTime buildDate;

    private final List<DockerTagManifestDigest> digests = new ArrayList<>();

    public DockerTag(String name, long size, LocalDateTime buildDate) {

        this.name = name;
        this.size = size;
        this.buildDate = buildDate;
    }

    public final void addDigest(final DockerTagManifestDigest digest) {
        digests.add(digest);
    }

    public final List<DockerTagManifestDigest> getDigests() {
        return digests;
    }

    public String getName() {
        return name;
    }

    public long getSize() {
        return size;
    }

    public LocalDateTime getBuildDate() {
        return buildDate;
    }

    @Override
    public final String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
