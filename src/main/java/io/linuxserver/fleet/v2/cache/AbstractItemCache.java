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

package io.linuxserver.fleet.v2.cache;

import io.linuxserver.fleet.v2.key.HasKey;
import io.linuxserver.fleet.v2.key.Key;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractItemCache<KEY extends Key, ITEM extends HasKey<KEY>> implements ItemCache<KEY, ITEM> {

    private final Map<KEY, ITEM> items;

    public AbstractItemCache() {
        items = new HashMap<>();
    }

    @Override
    public final void addItem(final ITEM item) {
        items.put(item.getKey(), item);
    }

    @Override
    public final ITEM findItem(final KEY key) {
        return items.get(key);
    }

    @Override
    public final void removeItem(final KEY key) {
        items.remove(key);
    }

    @Override
    public final boolean isItemCached(final KEY key) {
        return items.containsKey(key);
    }

    @Override
    public Collection<ITEM> getAllItems() {
        return items.values();
    }
}
