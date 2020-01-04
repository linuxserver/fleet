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
import io.linuxserver.fleet.v2.key.TagBranchKey;

import java.util.concurrent.atomic.AtomicReference;

public class TagBranch extends AbstractHasKey<TagBranchKey> {

    private final String                   branchName;
    private final AtomicReference<Boolean> branchProtected;
    private final AtomicReference<Tag>     latestTag;

    public TagBranch(final TagBranchKey tagBranchKey, final String tagBranchName, final boolean branchProtected, final Tag latestTag) {
        super(tagBranchKey);

        this.branchName      = tagBranchName;
        this.branchProtected = new AtomicReference<>(branchProtected);
        this.latestTag       = new AtomicReference<>(latestTag);
    }

    public final void updateLatestTag(final Tag latestTag) {
        this.latestTag.set(latestTag);
    }

    public final void setBranchProtected(final boolean branchProtected) {
        this.branchProtected.set(branchProtected);
    }

    public final String getBranchName() {
        return branchName;
    }

    public final Tag getLatestTag() {
        return latestTag.get();
    }
    
    public final boolean isNamedLatest() {
        return "latest".equals(getBranchName());
    }

    public final boolean isBranchProtected() {
        return branchProtected.get();
    }

    public final TagBranch cloneForUpdate() {
        return new TagBranch(getKey(), getBranchName(), isBranchProtected(), getLatestTag());
    }
}
