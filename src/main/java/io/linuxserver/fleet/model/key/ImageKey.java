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

package io.linuxserver.fleet.model.key;

public class ImageKey extends AbstractKey {

    private final RepositoryKey repositoryKey;
    private final String        name;

    public ImageKey(final String name, final RepositoryKey repositoryKey) {
        this(null, name, repositoryKey);
    }

    public ImageKey(final Integer id, final String name, final RepositoryKey repositoryKey) {
        super(id);

        this.repositoryKey = repositoryKey;
        this.name          = name;
    }

    public static ImageKey makeForLookup(final int imageId) {
        return new ImageKey(imageId, "<LookupKey>", null);
    }

    public ImageKey cloneWithId(int id) {
        return new ImageKey(id, name, repositoryKey);
    }

    public final RepositoryKey getRepositoryKey() {
        return repositoryKey;
    }

    public final String getName() {
        return name;
    }

    @Override
    public String toString() {
        return super.toString() + ":" + (repositoryKey == null ? "<LookupKey>" : repositoryKey.getName()) + "/" + name;
    }
}
