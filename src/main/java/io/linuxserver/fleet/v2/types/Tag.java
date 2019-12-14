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

package io.linuxserver.fleet.v2.types;

import java.time.LocalDateTime;
import java.util.*;

public class Tag {

    public static final Tag DefaultUnknown = new Tag("Unknown", null, Collections.emptySet());

    private final String         version;
    private final Set<TagDigest> digests;
    private final LocalDateTime  buildDate;

    public Tag(final String version, final LocalDateTime buildDate, final Set<TagDigest> digests) {

        this.version   = version;
        this.digests   = Collections.unmodifiableSet(digests);
        this.buildDate = (null == buildDate ? null : LocalDateTime.of(buildDate.toLocalDate(), buildDate.toLocalTime()));
    }

    public final List<TagDigest> getDigests() {
        return new ArrayList<>(digests);
    }

    public String getVersion() {
        return version;
    }

    public LocalDateTime getBuildDate() {

        if (null != buildDate) {
            return LocalDateTime.of(buildDate.toLocalDate(), buildDate.toLocalTime());
        }

        return null;
    }

    @Override
    public String toString() {
        return version;
    }
}
