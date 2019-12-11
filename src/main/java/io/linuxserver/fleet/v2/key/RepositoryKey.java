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

public class RepositoryKey extends AbstractKey {

    private static final String KeyPattern = "^\\d+:[^/]++$";

    private final String name;

    public static RepositoryKey parse(final String keyAsString) {

        if (keyAsString.matches(KeyPattern)) {

            final String[] keyParts       = keyAsString.split(":");
            final int      repositoryId   = Integer.parseInt(keyParts[0]);
            final String   repositoryName = keyParts[1];

            return new RepositoryKey(repositoryId, repositoryName);

        } else {
            throw new IllegalArgumentException("Key pattern is malformed");
        }
    }

    public RepositoryKey(final Integer id, final String name) {
        super(id);
        this.name = name;
    }

    public RepositoryKey cloneWithId(int id) {
        return new RepositoryKey(id, name);
    }

    public final String getName() {
        return name;
    }

    @Override
    public String toString() {
        return super.toString() + ":" + name;
    }
}
