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

package io.linuxserver.fleet.v2.types.meta;

public class ItemSyncSpec {

    public static final ItemSyncSpec Default = new ItemSyncSpec(false, false, true, true, null);

    private boolean deprecated;
    private boolean hidden;
    private boolean stable;
    private boolean synchronised;
    private String  versionMask;

    public ItemSyncSpec(final boolean deprecated,
                        final boolean hidden,
                        final boolean stable,
                        final boolean synchronised,
                        final String versionMask) {

        setDeprecated(deprecated);
        setHidden(hidden);
        setStable(stable);
        setSynchronised(synchronised);
        setVersionMask(versionMask);
    }

    public final void setDeprecated(boolean deprecated) {
        this.deprecated = deprecated;
    }

    public final void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public final void setStable(boolean stable) {
        this.stable = stable;
    }

    public final void setSynchronised(boolean synchronised) {
        this.synchronised = synchronised;
    }

    public final void setVersionMask(String versionMask) {
        this.versionMask = versionMask;
    }

    public final boolean isDeprecated() {
        return deprecated;
    }

    public final boolean isHidden() {
        return hidden;
    }

    public final boolean isStable() {
        return stable;
    }

    public final boolean isSynchronised() {
        return synchronised;
    }

    public final String getVersionMask() {
        return versionMask;
    }
}
