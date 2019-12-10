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

package io.linuxserver.fleet.v2.db;

public enum DbUpdateStatus {

    Inserted, Updated, NoChange, Exists;

    public final boolean isExpected(final DbUpdateStatus expected) {
        return this == expected;
    }

    public final boolean isExists() {
        return this == Exists;
    }

    public final boolean isInserted() {
        return this == Inserted;
    }

    public final boolean isUpdated() {
        return this == Updated;
    }

    public final boolean isNoChange() {
        return this == NoChange;
    }
}
