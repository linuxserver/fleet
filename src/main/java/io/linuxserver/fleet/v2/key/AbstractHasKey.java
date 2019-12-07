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

public abstract class AbstractHasKey<KEY extends Key> implements HasKey<KEY> {

    private final KEY key;

    public AbstractHasKey(final KEY key) {
        this.key = key;
    }

    @Override
    public final KEY getKey() {
        return key;
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }

    @Override
    public boolean equals(Object o) {

        if (!(o instanceof HasKey<?>)) {
            return false;
        }

        return key.equals(((HasKey<?>) o).getKey());
    }

    @Override
    public final String toString() {
        return key.toString();
    }

    @Override
    public int compareTo(HasKey<KEY> o) {

        if (null == o) {
            return -1;
        }

        final Integer otherId = o.getKey().getId();
        final Integer thisId  = getKey().getId();

        if (null == otherId && null == thisId) {
            return 0;
        }

        if (null == otherId) {
            return -1;
        } else {
            return o.getKey().getId().compareTo(getKey().getId());
        }
    }
}
