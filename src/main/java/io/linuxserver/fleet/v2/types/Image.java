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

import io.linuxserver.fleet.v2.key.AbstractHasKey;
import io.linuxserver.fleet.v2.key.HasKey;
import io.linuxserver.fleet.v2.key.ImageKey;
import io.linuxserver.fleet.v2.key.RepositoryKey;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class Image extends AbstractHasKey<ImageKey> {

    private final long           pullCount;
    private final int            starCount;
    private final Set<TagBranch> tagBranches;

    public Image(final ImageKey key, final long pullCount, final int starCount) {
        super(key);

        this.pullCount   = pullCount;
        this.starCount   = starCount;
        this.tagBranches = new TreeSet<>();
    }

    public final Image cloneWithPullAndStarCount(final long pullCount, final int starCount) {

        final Image cloned = new Image(getKey(), pullCount, starCount);
        tagBranches.forEach(cloned::addTagBranch);

        return cloned;
    }

    public final RepositoryKey getRepositoryKey() {
        return getKey().getRepositoryKey();
    }

    public final String getName() {
        return getKey().getName();
    }


    public final List<TagBranch> getTagBranches() {
        return new ArrayList<>(tagBranches);
    }

    public final void addTagBranch(final TagBranch tagBranch) {

        if (getTagBranches().contains(tagBranch)) {
            removeTagBranch(tagBranch);
        }

        tagBranches.add(tagBranch);
    }

    public final void removeTagBranch(final TagBranch tagBranch) {

        for (TagBranch storedTagBranch : getTagBranches()) {

            if (storedTagBranch.equals(tagBranch)) {
                getTagBranches().remove(storedTagBranch);
            }
        }
    }

    public final long getPullCount() {
        return pullCount;
    }

    public final int getStarCount() {
        return starCount;
    }

    public final Tag getLatestTag() {

        for (TagBranch storedTagBranch : getTagBranches()) {

            if (storedTagBranch.isTrueLatest()) {
                return storedTagBranch.getLatestTag();
            }
        }

        return null;
    }

    @Override
    public final int compareTo(final HasKey<ImageKey> o) {
        return o.getKey().getName().compareTo(getKey().getName());
    }
}
