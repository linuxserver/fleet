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

package io.linuxserver.fleet.v2.key;

public abstract class AbstractDatabaseKey implements Key {

    private final Integer id;

    AbstractDatabaseKey(final Integer id) {
        this.id = id;
    }

    @Override
    public final Integer getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {

        if (!(o instanceof Key)) {
            return false;
        }

        if (null == id) {
            return ((Key) o).getId() == null;
        }

        return ((Key) o).getId().equals(id);
    }

    @Override
    public int hashCode() {

        if (null == id) {
            return -1;
        }

        return id.hashCode();
    }

    @Override
    public String toString() {
        return null == id ? "<NO_ID>" : String.valueOf(id);
    }
}
