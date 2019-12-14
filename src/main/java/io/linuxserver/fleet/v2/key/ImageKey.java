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

public class ImageKey extends AbstractDatabaseKey {

    private static final String KeyPattern = "^\\d+:\\d+:[^/]+/[^/]+$";

    private final RepositoryKey repositoryKey;
    private final String        name;

    @Deprecated
    public ImageKey(final String name, final RepositoryKey repositoryKey) {
        this(null, name, repositoryKey);
    }

    public ImageKey(final Integer id, final String name, final RepositoryKey repositoryKey) {
        super(id);

        this.repositoryKey = repositoryKey;
        this.name          = name;
    }

    public static ImageKey parse(final String keyAsString) {

        if (keyAsString.matches(KeyPattern)) {

            final String[] keyParts       = keyAsString.split(":");
            final String[] names          = keyParts[2].split("/");
            final int      repositoryId   = Integer.parseInt(keyParts[0]);
            final int      imageId        = Integer.parseInt(keyParts[1]);
            final String   repositoryName = names[0];
            final String   imageName      = names[1];

            return new ImageKey(imageId, imageName, new RepositoryKey(repositoryId, repositoryName));

        } else {
            throw new IllegalArgumentException("Key pattern is malformed");
        }
    }

    public final ImageLookupKey getAsLookupKey() {
        return new ImageLookupKey(getRepositoryKey().getName() + "/" + getName());
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
        return repositoryKey.getId() + ":" + super.toString() + ":" + repositoryKey.getName() + "/" + name;
    }
}
