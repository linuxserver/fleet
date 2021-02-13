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

import java.util.Objects;

public class TagDigest implements Comparable<TagDigest> {

    private final long   size;
    private final String digest;
    private final String architecture;
    private final String archVariant;

    public TagDigest(final long size, final String digest, final String architecture, final String archVariant) {

        this.size         = size;
        this.digest       = digest;
        this.architecture = architecture;
        this.archVariant  = archVariant;
    }

    public final long getSize() {
        return size;
    }

    public final String getDigest() {
        return digest;
    }

    public final String getArchitecture() {
        return architecture;
    }

    public final String getArchVariant() {
        return archVariant;
    }

    @Override
    public int hashCode() {
        return Objects.hash(digest, architecture, archVariant);
    }

    @Override
    public boolean equals(Object obj) {

        if (null == obj) {
            return false;
        }

        if (!(obj instanceof TagDigest)) {
            return false;
        }

        final TagDigest other = (TagDigest) obj;

        return Objects.equals(digest,       other.digest) &&
               Objects.equals(architecture, other.architecture) &&
               Objects.equals(archVariant,  other.archVariant);
    }

    @Override
    public String toString() {
        return "DIGEST:" + digest + "--" + architecture + "/" + archVariant;
    }

    @Override
    public int compareTo(TagDigest o) {

        if (null == o) {
            return 1;
        }
        return toString().compareTo(o.toString());
    }
}
