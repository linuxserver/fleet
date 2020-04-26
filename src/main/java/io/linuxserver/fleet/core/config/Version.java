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

public class Version {

    private int major;
    private int minor;
    private int patch;

    public Version(int major, int minor, int patch) {

        this.major = major;
        this.minor = minor;
        this.patch = patch;
    }

    public Version(String version) {

        String[] bits = version.split("\\.");

        this.major = Integer.parseInt(bits[0]);
        this.minor = Integer.parseInt(bits[1]);
        this.patch = Integer.parseInt(bits[2]);
    }

    public int getMajor() {
        return major;
    }

    public int getMinor() {
        return minor;
    }

    public int getPatch() {
        return patch;
    }

    public boolean isNewerThan(Version version) {

        if (this.major > version.major) {
            return true;
        } if (this.minor > version.minor) {
            return true;
        } else if (this.minor < version.minor) {
            return false;
        }

        return this.patch > version.patch;
    }

    @Override
    public String toString() {
        return major + "." + minor + "." + patch;
    }
}
