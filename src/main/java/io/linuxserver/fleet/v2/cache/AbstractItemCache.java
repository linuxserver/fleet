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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public abstract class AbstractItemCache<KEY extends Key, ITEM extends HasKey<KEY>> implements ItemCache<KEY, ITEM> {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private final List<ItemCacheListener<ITEM>> listeners;
    private final Map<KEY, ITEM>                items;

    public AbstractItemCache() {

        listeners = new ArrayList<>();
        items     = new HashMap<>();
    }

    public final void registerCacheListener(final ItemCacheListener<ITEM> listener) {

        LOGGER.info("Registering new cache listener {}", listener);
        listeners.add(listener);
    }

    @Override
    public final void addItem(final ITEM item) {

        final ITEM cached = items.put(item.getKey(), item);

        LOGGER.info("Item {} cached", item);
        listeners.forEach(l -> l.onItemCached(cached));
    }

    @Override
    public final ITEM findItem(final KEY key) {
        return items.get(key);
    }

    @Override
    public final void removeItem(final KEY key) {

        final ITEM removed = items.remove(key);

        LOGGER.info("Item {} removed from cache", removed);
        listeners.forEach(l -> l.onItemRemoved(removed));
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
