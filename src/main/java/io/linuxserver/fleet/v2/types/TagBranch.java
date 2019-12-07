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
import io.linuxserver.fleet.v2.key.TagBranchKey;

public class TagBranch extends AbstractHasKey<TagBranchKey> {

    private final String branchName;
    private final Tag    latestTag;

    public TagBranch(final TagBranchKey tagBranchKey, final String tagBranchName, final Tag latestTag) {
        super(tagBranchKey);

        this.branchName = tagBranchName;
        this.latestTag  = latestTag;
    }

    public final TagBranch cloneWithLatestTag(final Tag latestTag) {
        return new TagBranch(getKey(), getBranchName(), latestTag);
    }

    public final String getBranchName() {
        return branchName;
    }

    public final Tag getLatestTag() {
        return latestTag;
    }
    
    public final boolean isTrueLatest() {
        return "latest".equals(branchName);
    }
}
