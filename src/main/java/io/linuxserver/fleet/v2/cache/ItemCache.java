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

public interface ItemCache<KEY extends Key, ITEM extends HasKey<KEY>> {

    boolean isEmpty();

    void addItem(ITEM item);

    ITEM findItem(KEY key);

    void removeItem(KEY key);

    boolean isItemCached(KEY key);

    Collection<ITEM> getAllItems();

    void addAllItems(Collection<ITEM> items);

    int size();

    interface ItemCacheListener<ITEM> {

        void onItemCached(final ITEM item);
        void onItemRemoved(final ITEM item);
    }
}
