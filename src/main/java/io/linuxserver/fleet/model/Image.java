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

package io.linuxserver.fleet.model;

/**
 * <p>
 * Representation of a stored image in the Fleet database. Each image contains
 * specific information regarding its build status and pull count.
 * </p>
 */
public class Image extends PersistableItem<Image> {

    private final int       repositoryId;
    private final String    name;

    private String          version;
    private long            pullCount;
    private String          versionMask;
    private boolean         unstable;
    private boolean         hidden;

    private boolean         deprecated;
    private String          deprecationReason;

    public Image(Integer id, int repositoryId, String name) {

        super(id);

        this.name           = name;
        this.repositoryId   = repositoryId;
    }

    public Image(int repositoryId, String name) {
        this(null, repositoryId, name);
    }

    public Image withVersion(String version) {

        this.version = version;
        return this;
    }

    public Image withPullCount(long pullCount) {

        this.pullCount = pullCount;
        return this;
    }

    public Image withVersionMask(String versionMask) {

        this.versionMask = versionMask;
        return this;
    }

    public Image withHidden(boolean hidden) {

        this.hidden = hidden;
        return this;
    }

    public Image withUnstable(boolean unstable) {

        this.unstable = unstable;
        return this;
    }

    public Image withDeprecated(boolean deprecated) {

        this.deprecated = deprecated;
        return this;
    }

    public Image withDeprecationReason(String deprecationReason) {

        this.deprecationReason = deprecationReason;
        return this;
    }

    public int getRepositoryId() {
        return repositoryId;
    }

    public String getName() {
        return name;
    }

    public long getPullCount() {
        return pullCount;
    }

    public String getVersion() {
        return version;
    }

    public String getVersionMask() {
        return versionMask;
    }

    public boolean isUnstable() {
        return unstable;
    }

    public boolean isHidden() {
        return hidden;
    }

    public boolean isDeprecated() {
        return deprecated;
    }

    public String getDeprecationReason() {
        return deprecationReason;
    }
}
