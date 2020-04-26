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

public class TagDigest {

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
}
