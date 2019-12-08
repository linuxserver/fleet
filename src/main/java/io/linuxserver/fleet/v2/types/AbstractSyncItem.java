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
import io.linuxserver.fleet.v2.key.Key;
import io.linuxserver.fleet.v2.types.meta.ItemSyncSpec;

public abstract class AbstractSyncItem<ITEM extends AbstractSyncItem, KEY extends Key> extends AbstractHasKey<KEY> implements HasSyncSpec {

    private final ItemSyncSpec syncSpec;

    public AbstractSyncItem(final KEY key, final ItemSyncSpec syncSpec) {
        super(key);
        this.syncSpec = syncSpec;
    }

    public abstract ITEM cloneWithSyncSpec(final ItemSyncSpec syncSpec);

    public final ItemSyncSpec getSpec() {
        return syncSpec;
    }

    @Override
    public boolean isSyncEnabled() {
        return getSpec().isSynchronised();
    }

    @Override
    public boolean isStable() {
        return getSpec().isStable();
    }

    @Override
    public boolean isDeprecated() {
        return getSpec().isDeprecated();
    }

    @Override
    public String getVersionMask() {
        return getSpec().getVersionMask();
    }

    @Override
    public boolean isHidden() {
        return getSpec().isHidden();
    }
}
