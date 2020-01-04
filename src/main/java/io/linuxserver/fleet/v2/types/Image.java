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

import io.linuxserver.fleet.v2.key.HasKey;
import io.linuxserver.fleet.v2.key.ImageKey;
import io.linuxserver.fleet.v2.key.RepositoryKey;
import io.linuxserver.fleet.v2.types.meta.ImageMetaData;
import io.linuxserver.fleet.v2.types.meta.ItemSyncSpec;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Image extends AbstractSyncItem<ImageKey, Image> {

    private static final DateTimeFormatter DefaultImageTimeFormat = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm:ss");

    private final ImageCountData countData;
    private final String         description;
    private final LocalDateTime  lastUpdated;
    private final Set<TagBranch> tagBranches;
    private final ImageMetaData  metaData;

    public Image(final ImageKey key,
                 final ItemSyncSpec syncSpec,
                 final ImageMetaData metaData,
                 final ImageCountData countData,
                 final String description,
                 final LocalDateTime lastUpdated) {

        super(key, syncSpec);

        this.countData   = countData;
        this.metaData    = metaData;
        this.description = description;
        this.lastUpdated = parseDateTime(lastUpdated);
        this.tagBranches = new HashSet<>();
    }

    public final Image cloneForUpdate(final long pullCount,
                                      final int starCount,
                                      final String description,
                                      final LocalDateTime lastUpdated) {

        final Image cloned = new Image(getKey(), getSpec(), getMetaData(), new ImageCountData(pullCount,starCount), description, lastUpdated);
        tagBranches.forEach(t -> cloned.addTagBranch(t.cloneForUpdate()));

        return cloned;
    }

    public final Image cloneForUpdate() {
        return cloneWithSyncSpec(getSpec());
    }

    @Override
    public final Image cloneWithSyncSpec(final ItemSyncSpec syncSpec) {

        final Image cloned = new Image(getKey(), syncSpec, getMetaData(), countData, getDescription(), getLastUpdated());
        tagBranches.forEach(t -> cloned.addTagBranch(t.cloneForUpdate()));

        return cloned;
    }

    public final String getFullName() {
        return getRepositoryName() + "/" + getName();
    }

    public final RepositoryKey getRepositoryKey() {
        return getKey().getRepositoryKey();
    }

    public final String getRepositoryName() {
        return getRepositoryKey().getName();
    }

    public final String getName() {
        return getKey().getName();
    }

    public final String getDescription() {
        return description;
    }

    public final ImageMetaData getMetaData() {
        return metaData;
    }

    public final LocalDateTime getLastUpdated() {
        return parseDateTime(lastUpdated);
    }

    public final String getLastUpdatedAsString() {

        final LocalDateTime lastUpdated = parseDateTime(this.lastUpdated);
        return null == lastUpdated ? null : DefaultImageTimeFormat.format(lastUpdated);
    }

    public final List<TagBranch> getTagBranches() {
        return new ArrayList<>(tagBranches);
    }

    public final TagBranch findTagBranchByName(final String branchName) {

        for (TagBranch tagBranch : tagBranches) {

            if (tagBranch.getBranchName().equals(branchName)) {
                return tagBranch;
            }
        }

        return null;
    }

    public final void addTagBranch(final TagBranch tagBranch) {

        final boolean added = tagBranches.add(tagBranch);
        if (!added) {
            throw new IllegalArgumentException("TagBranch " + tagBranch + " already present in Image");
        }
    }

    public final void removeTagBranch(final TagBranch tagBranch) {
        getTagBranches().removeIf(storedTagBranch -> storedTagBranch.equals(tagBranch));
    }

    public final long getPullCount() {
        return countData.getPullCount();
    }

    public final int getStarCount() {
        return countData.getStarCount();
    }

    public final Tag getLatestTag() {

        for (TagBranch storedTagBranch : getTagBranches()) {

            if (storedTagBranch.isNamedLatest()) {
                return storedTagBranch.getLatestTag();
            }
        }

        return Tag.DefaultUnknown;
    }

    @Override
    public final int compareTo(final HasKey<ImageKey> o) {
        return getKey().getName().compareTo(o.getKey().getName());
    }

    @Override
    public String toString() {
        return getRepositoryName() + "/" + getName() + "@" + getLatestTag();
    }

    private LocalDateTime parseDateTime(final LocalDateTime dateTime) {
        return null == dateTime ? null : LocalDateTime.of(dateTime.toLocalDate(), dateTime.toLocalTime());
    }
}
